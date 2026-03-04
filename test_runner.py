# test_runner.py
import os
import sys
import uuid
from modules.orquestador import ScaleVisionPipeline

# 1. Validación estricta de entorno: Requerimos un video real.
VIDEO_TEST = "video_personas.mp4"

if not os.path.exists(VIDEO_TEST):
    print(f"\n[ERROR CRÍTICO] Falta el archivo '{VIDEO_TEST}'.")
    print("Laura: Descarga un video corto (5-10s) de una persona real.")
    print(f"Guárdalo exactamente como '{VIDEO_TEST}' en esta misma carpeta y vuelve a ejecutar.")
    sys.exit(1)

if __name__ == "__main__":
    print("==================================================")
    print(" SCALEVISION AI - PRUEBA DE PIPELINE CON FUEGO REAL")
    print("==================================================")
    
    pipeline = ScaleVisionPipeline()
    
    # ==========================================
    # CASO 1: FLUJO IDEAL (Face Tracking)
    # ==========================================
    job_id_1 = str(uuid.uuid4())
    print(f"\n>>> [JOB {job_id_1}] INICIANDO FASE 1: SCAN (Buscando sujetos con YOLO26)")
    
    # Simulamos el request del Frontend para Face Tracking
    scan_res = pipeline.ejecutar_fase_1_scan(job_id_1, VIDEO_TEST, "face_tracking")
    
    print("\n[RESULTADO FASE 1]")
    print(f"Fallback Activo: {scan_res['fallback']['is_active']}")
    print(f"Sujetos Encontrados: {len(scan_res['subjects'])}")
    
    # Lógica condicional exacta a la que hará el Frontend/Backend
    if not scan_res["fallback"]["is_active"] and len(scan_res["subjects"]) > 0:
        # El usuario elige el primer sujeto que YOLO detectó y filtró
        target_id = scan_res["subjects"][0]["subject_id"]
        print(f"-> Frontend muestra miniaturas. Usuario selecciona: {target_id}")
        
        print(f"\n>>> [JOB {job_id_1}] INICIANDO FASE 2: PROCESS (Ejecutando tracking y FFmpeg)")
        process_res = pipeline.ejecutar_fase_2_proceso(
            job_id=job_id_1, 
            video_path=VIDEO_TEST, 
            strategy="face_tracking", 
            target_subject_id=target_id
        )
        print("\n[RESULTADO FASE 2]")
        print(f"Estado: {process_res['status']}")
        print(f"Video Final: {process_res['output_video_url']}")
        print(f"Comando usado: {process_res['ffmpeg_cmd']}")
        
    else:
        print("\n-> YOLO no encontró personas reales que cumplan la regla del 70%. Fallback activado.")
        print(f"Razón: {scan_res['fallback']['reason']}")
        
        # Simulamos que el usuario ACEPTA el Center Crop (Caso 2)
        print(f"\n>>> [JOB {job_id_1}] INICIANDO FASE 2: PROCESS (Modo Seguro - Center Crop)")
        process_res = pipeline.ejecutar_fase_2_proceso(
            job_id=job_id_1, 
            video_path=VIDEO_TEST, 
            strategy="center_crop"
        )
        print("\n[RESULTADO FASE 2 FALLBACK]")
        print(f"Estado: {process_res['status']}")
        print(f"Video Final: {process_res['output_video_url']}")

    print("\n==================================================")
    print(" PRUEBA FINALIZADA. REVISA LA CARPETA 'scalevision_data/processed'")
    print("==================================================")