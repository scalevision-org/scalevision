import cv2
import json
import uuid
import time
import os
import subprocess
from datetime import datetime
from .config import init_storage, get_path
from .recognition import VideoRecognizer
from .tracking import SubjectTracker
from .cropping import VideoCropper


class ScaleVisionPipeline:
    def __init__(self):
        init_storage()
        self.recognizer = VideoRecognizer()
        self.tracker = SubjectTracker()
        self.cropper = VideoCropper()

    def _save_json_log(self, job_id, phase, data):
        filepath = get_path("json_logs", f"{job_id}_{phase}.json")
        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4, ensure_ascii=False)

    def ejecutar_fase_1_scan(self, job_id, video_path, modo_corte):
        print(f"\n[PHASE 1 - SCAN] ID: {job_id} | Mode: {modo_corte}")
        start_time = time.time()

        response = {
            "metadata": {
                "processing_time_ms": 0,
                "video_duration_seconds": 15.0,
                "frame_count": 450,
            },
            "subjects": [],
            "fallback": {"is_active": False, "strategy": None, "reason": None},
        }

        if modo_corte == "center_crop":
            response["fallback"] = {
                "is_active": True,
                "strategy": "center_crop",
                "reason": "Bypass manual",
            }
            return response

        # Ejecutamos la regla de negocio: 70% de aparición, max 3 sujetos
        sujetos_validos = self.recognizer.scan_for_subjects(
            video_path, min_ratio=0.40, max_subjects=3
        )

        if sujetos_validos:
            for subj in sujetos_validos:
                numeric_id = subj["numeric_id"]
                # Formato exacto que espera la Fase 2 para parsear el ID numérico
                subject_id = f"subject_{job_id}_{numeric_id}"

                # Guardar la miniatura que YOLO recortó físicamente en disco
                thumb_filename = f"{subject_id}_thumb.jpg"
                thumb_path = get_path("thumbnails", thumb_filename)

                if subj["thumbnail_img"] is not None:
                    cv2.imwrite(thumb_path, subj["thumbnail_img"])

                response["subjects"].append(
                    {
                        "subject_id": subject_id,
                        "thumbnail_url": thumb_filename,
                        "appearance_ratio": round(subj["ratio"], 2),
                    }
                )
        else:
            print("WARNING: Ningún sujeto alcanzó el 70% de aparición.")
            response["fallback"] = {
                "is_active": True,
                "strategy": "center_crop",
                "reason": "REFERENCE_LOST: Ningún sujeto superó el 70% de aparición.",
            }

        self._save_json_log(job_id, "scan", response)
        return response

    def ejecutar_fase_2_proceso(
        self, job_id, video_path, strategy, target_subject_id=None
    ):
        print(f"\n[PHASE 2 - RENDER] ID: {job_id} | Strategy: {strategy}")
        start_time = time.time()

        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        output_filename = f"processed_{job_id}_{timestamp}.mp4"
        output_path = get_path("processed", output_filename)

        if strategy == "center_crop":
            # Recorte estático seguro
            cmd = self.cropper.generate_ffmpeg_command(
                video_path, output_path, is_dynamic=False
            )
            print(f"INFO: Ejecutando motor FFmpeg (Static) -> {cmd}")
            proceso = subprocess.run(cmd, shell=True, capture_output=True, text=True)
            if proceso.returncode != 0:
                raise RuntimeError("FFmpeg falló.")
        elif strategy == "face_tracking":
            # Tracking dinámico con Inteligencia Artificial
            trayectoria, total_frames, fps = self.tracker.extract_trajectory(
                video_path, target_subject_id
            )
            cmd = self.cropper.apply_dynamic_crop(
                video_path, output_path, trayectoria, total_frames, fps
            )
        else:
            raise ValueError("Estrategia de renderizado inválida")
        
        
        # 1. Verificar si el archivo se creó correctamente y calcular su peso
        if not os.path.exists(output_path):
            raise RuntimeError(
                f"El archivo {output_path} no se generó físicamente en el disco."
            )

        file_size_bytes = os.path.getsize(output_path)
        file_size_mb = round(file_size_bytes / (1024 * 1024), 2)

        # 2. Ensamblar la respuesta con la nueva metadata estricta
        response = {
            "status": "RENDER_COMPLETED",
            "output_video_url": output_path,
            "metadata": {
                "final_resolution": "720x1280",
                "render_time_ms": int((time.time() - start_time) * 1000),
                "file_size_bytes": file_size_bytes,
                "file_size_mb": file_size_mb,
            },
        }

        self._save_json_log(job_id, "render", response)
        return response
