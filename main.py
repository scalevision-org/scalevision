from fastapi import FastAPI, HTTPException, Response, BackgroundTasks
from models import ScanRequest, ProcessRequest
import store
import uuid
import time
from modules.orquestador import ScaleVisionPipeline

app = FastAPI(title="ScaleVision AI Service - MVP", version="2.2.0")

# 1. Instanciamos el pipeline globalmente al arrancar el worker
pipeline = ScaleVisionPipeline()

# ==========================================
# TAREAS EN SEGUNDO PLANO (WORKERS REALES)
# ==========================================


def worker_fase_1_scan(job_id: str, video_path: str, modo_corte: str):
    """Ejecuta el escaneo de YOLO en background y actualiza la BD en memoria."""
    try:
        # En producción, descargamos el video de OCI Object Storage aquí usando la URI.
        # Para prueba local hoy, extraeremos el nombre del archivo de la URL enviada.
        resultado = pipeline.ejecutar_fase_1_scan(job_id, video_path, modo_corte)

        store.update_job(
            job_id,
            {
                "scan_status": "PROCESADO",
                "subjects": resultado.get("subjects", []),
                "fallback": resultado.get("fallback", {}),
                "scan_metadata": resultado.get("metadata", {}),
            },
        )
    except Exception as e:
        store.update_job(job_id, {"scan_status": "ERROR", "error_message": str(e)})


from modules.cloud import CloudIntegration

# Instanciamos el cliente cloud globalmente
cloud_client = CloudIntegration()


def worker_fase_2_proceso(
    job_id: str,
    video_path: str,
    strategy: str,
    target_subject_id: str,
    callback_url: str = None,
    webhook_secret: str = None,
):
    """Ejecuta FFmpeg en background, actualiza la BD y notifica vía Webhook."""
    try:
        # 1. Procesamiento de IA y Renderizado (lo que ya funciona)
        resultado = pipeline.ejecutar_fase_2_proceso(
            job_id, video_path, strategy, target_subject_id
        )

        # 2. Actualizamos la BD local en memoria
        store.update_job(
            job_id,
            {
                "process_status": "RENDER_COMPLETED",
                "output_video_url": resultado.get("output_video_url"),
                "render_metadata": resultado.get("metadata", {}),
            },
        )

        # 3. [NUEVO] Subida a OCI y Webhook al Backend Java
        if callback_url and webhook_secret:
            # Aquí iría la subida a OCI: cloud_client.upload_to_oci(resultado["output_video_url"])

            payload_exito = {
                "job_id": job_id,
                "status": "completed",
                "processing_stats": resultado.get("metadata", {}),
                "output_video_path": resultado.get("output_video_url"),
            }
            cloud_client.send_webhook_callback(
                callback_url, webhook_secret, payload_exito
            )

    except Exception as e:
        store.update_job(job_id, {"process_status": "ERROR", "error_message": str(e)})
        # Webhook de fallo fatal
        if callback_url and webhook_secret:
            payload_error = {
                "job_id": job_id,
                "status": "failed",
                "error_code": "RENDER_ERROR",
                "message": str(e),
            }
            cloud_client.send_webhook_callback(
                callback_url, webhook_secret, payload_error
            )


# ==========================================
# ENDPOINTS REST (POLLING)
# ==========================================


@app.post("/scan", status_code=201)
def start_scan(request: ScanRequest, background_tasks: BackgroundTasks):
    job_id_str = str(request.id)

    # Adaptación para prueba local: si mandas "http://algo/con_persona.mp4", se queda con "con_persona.mp4"
    video_path = (
        str(request.video_url).split("/")[-1]
        if "/" in str(request.video_url)
        else str(request.video_url)
    )

    store.create_job(job_id_str)
    store.update_job(
        job_id_str,
        {
            "modo_corte": request.modo_corte,
            "video_path": video_path,
            "scan_status": "PROCESANDO",
        },
    )

    # Delegamos el análisis pesado a un hilo en background real
    background_tasks.add_task(
        worker_fase_1_scan, job_id_str, video_path, request.modo_corte
    )

    return {
        "status": "SUBIDO",
        "message": "Video recibido. YOLO escaneando en background.",
    }


@app.get("/scan/{id}")
def get_scan_status(id: uuid.UUID, response: Response):
    job = store.get_job(str(id))
    if not job:
        raise HTTPException(
            status_code=404,
            detail={
                "error_code": "JOB_NOT_FOUND",
                "message": "No existe un análisis asociado",
            },
        )

    estado_actual = job.get("scan_status", "SUBIDO")

    if estado_actual == "PROCESANDO":
        response.status_code = 202
        return {
            "status": "PROCESANDO",
            "message": "YOLO analizando frames y ByteTrack siguiendo sujetos",
        }

    if estado_actual == "ERROR":
        raise HTTPException(
            status_code=500,
            detail={"error_code": "SCAN_FAILED", "message": job.get("error_message")},
        )

    return {
        "status": "PROCESADO",
        "message": "Análisis completado",
        "metadata": job.get("scan_metadata", {}),
        "subjects": job.get("subjects", []),
        "fallback": job.get(
            "fallback", {"is_active": False, "strategy": None, "reason": None}
        ),
    }


@app.post("/process-video", status_code=201)
def process_video(request: ProcessRequest, background_tasks: BackgroundTasks):
    job_id_str = str(request.id)
    job = store.get_job(job_id_str)

    if not job:
        raise HTTPException(status_code=404, detail={"error_code": "JOB_NOT_FOUND"})

    if job.get("scan_status") != "PROCESADO":
        raise HTTPException(
            status_code=409,
            detail={
                "error_code": "INVALID_JOB_STATE",
                "message": "El escaneo no ha terminado.",
            },
        )

    # Regla de Negocio: Strategy Validation
    if request.strategy == "FACE_TRACKING":
        if not request.target_subject_id:
            raise HTTPException(
                status_code=422,
                detail={
                    "error_code": "SUBJECT_REQUIRED",
                    "message": "ID obligatorio para FACE_TRACKING",
                },
            )

        valid_subjects = [s["subject_id"] for s in job.get("subjects", [])]
        if request.target_subject_id not in valid_subjects:
            raise HTTPException(
                status_code=422,
                detail={
                    "error_code": "SUBJECT_NOT_FOUND",
                    "message": "El sujeto no existe",
                },
            )

    elif request.strategy == "CENTER_CROP" and request.target_subject_id is not None:
        raise HTTPException(
            status_code=422,
            detail={
                "error_code": "INVALID_PARAM",
                "message": "target_subject_id debe ser null para CENTER_CROP",
            },
        )

    store.update_job(
        job_id_str, {"process_status": "RENDERING", "process_started_at": time.time()}
    )

    # Delegamos el recorte y renderizado a FFmpeg en background
    background_tasks.add_task(
        worker_fase_2_proceso,
        job_id_str,
        job.get("video_path"),
        request.strategy,
        request.target_subject_id,
    )

    return {
        "status": "RECIBIDO",
        "message": "Orden de renderizado recibida por el motor FFmpeg",
    }


@app.get("/process-video/{id}")
def get_render_status(id: uuid.UUID, response: Response):
    job = store.get_job(str(id))
    if not job or not job.get("process_started_at"):
        raise HTTPException(status_code=404, detail={"error_code": "ID_NOT_FOUND"})

    estado_actual = job.get("process_status", "RENDERING")

    if estado_actual == "CORTANDO":
        response.status_code = 202
        return {"status": "CORTANDO", "message": "FFmpeg ejecutando el recorte"}

    if estado_actual == "ERROR":
        raise HTTPException(
            status_code=500,
            detail={"error_code": "RENDER_FAILED", "message": job.get("error_message")},
        )

    return {
        "id": str(id),
        "status": "CORTADO",
        "message": "Video final generado exitosamente",
        "output_video_url": job.get("output_video_url"),
        "metadata": job.get("render_metadata", {}),
    }
