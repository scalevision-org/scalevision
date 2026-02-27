from fastapi import FastAPI, HTTPException, Response
from models import ScanRequest, ProcessRequest
import store
import time
import uuid

app = FastAPI(title="ScaleVision AI Service - MVP", version="2.1.0")

@app.post("/scan", status_code=201)
def start_scan(request: ScanRequest):
    # Guardamos el modo_corte para definir el comportamiento del análisis
    store.create_job(str(request.id))
    store.update_job(str(request.id), {"modo_corte": request.modo_corte})
    
    return {
        "status": "SUBIDO",
        "message": "Video recibido y en cola de espera"
    }

@app.get("/scan/{id}")
def get_scan_status(id: uuid.UUID, response: Response):
    job = store.get_job(str(id))
    if not job:
        raise HTTPException(status_code=404, detail={"error_code": "JOB_NOT_FOUND", "message": "No existe un análisis asociado"})

    modo_corte = job.get("modo_corte", "dynamic")
    elapsed = time.time() - job["created_at"]

    # LOGICA CONDICIONAL: Center vs Dynamic
    if modo_corte == "center":
        # Si es center, no hay espera de procesamiento de IA (YOLO/ByteTrack)
        # Se marca como PROCESADO inmediatamente con fallback activo
        return {
            "status": "PROCESADO",
            "message": "Modo manual detectado. Listo para recorte central.",
            "metadata": {
                "processing_time_ms": 100,
                "video_duration_seconds": 15.5,
                "frame_count": 450
            },
            "subjects": [],
            "fallback": {
                "is_active": True,
                "strategy": "CENTER_CROP",
                "reason": "Solicitud explícita de recorte central (bypass IA)"
            }
        }
    
    else:
        # Modo Dynamic: Simulación de estados de IA basada en tiempo (5 segundos)
        if elapsed < 5:
            response.status_code = 202
            return {"status": "PROCESANDO", "message": "YOLO analizando frames y ByteTrack siguiendo sujetos"}

        return {
            "status": "PROCESADO",
            "message": "Análisis completado",
            "metadata": {
                "processing_time_ms": 4500,
                "video_duration_seconds": 15.5,
                "frame_count": 450
            },
            "subjects": [
                {"subject_id": "person_1", "thumbnail_url": "https://blob.mock/t1.jpg"},
                {"subject_id": "person_2", "thumbnail_url": "https://blob.mock/t2.jpg"}
            ],
            "fallback": {"is_active": False, "strategy": None, "reason": None}
        }

@app.post("/process-video", status_code=201)
def process_video(request: ProcessRequest):
    job = store.get_job(str(request.id))
    
    if not job:
        raise HTTPException(status_code=404, detail={"error_code": "JOB_NOT_FOUND"})
    
    # Validación de estado: Si es center, el tiempo de creación no importa tanto, 
    # pero para dynamic validamos que hayan pasado los 5s de simulación.
    is_ready = job.get("modo_corte") == "center" or (time.time() - job["created_at"]) >= 5
    
    if not is_ready:
        raise HTTPException(status_code=409, detail={"error_code": "INVALID_JOB_STATE"})

    # Regla de Negocio: Strategy Validation
    if request.strategy == "DYNAMIC_CROP":
        if not request.target_subject_id:
            raise HTTPException(status_code=422, detail={"error_code": "SUBJECT_REQUIRED", "message": "ID obligatorio para DYNAMIC_CROP"})
        # En modo center, la lista de subjects está vacía, por lo que esto fallará correctamente
        if request.target_subject_id not in job.get("subjects", ["person_1", "person_2"]):
            raise HTTPException(status_code=422, detail={"error_code": "SUBJECT_NOT_FOUND", "message": "El sujeto no existe"})
    
    elif request.strategy == "CENTER_CROP" and request.target_subject_id is not None:
         raise HTTPException(status_code=422, detail={"error_code": "INVALID_PARAM", "message": "target_subject_id debe ser null para CENTER_CROP"})

    store.update_job(str(request.id), {
        "process_status": "RENDERING",
        "process_started_at": time.time()
    })

    return {
        "status": "RENDER_ENQUEUED",
        "message": "Orden de renderizado recibida por el motor FFmpeg"
    }

@app.get("/process-video/{id}")
def get_render_status(id: uuid.UUID, response: Response):
    job = store.get_job(str(id))
    if not job or not job.get("process_started_at"):
        raise HTTPException(status_code=404, detail={"error_code": "ID_NOT_FOUND"})

    elapsed = time.time() - job["process_started_at"]

    if elapsed < 5:
        response.status_code = 202
        return {"status": "RENDERING", "message": "FFmpeg ejecutando el recorte"}

    return {
        "id": str(id),
        "status": "RENDER_COMPLETED",
        "message": "Video final generado exitosamente",
        "output_video_url": "https://objectstorage.mock/final_render_720.mp4",
        "metadata": {
            "final_resolution": "720x1280",
            "render_time_ms": 5200,
            "file_size_mb": 8.5
        }
    }