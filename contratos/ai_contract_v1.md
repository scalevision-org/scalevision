# Especificación de Interfaz IA ↔ Backend: ScaleVision MVP

## 1. Información General

* **Título**: Contrato AI ↔ Backend
* **Descripción**: Interfaz entre Backend y servicio de IA para la automatización del recorte de video (MVP)
* **Versión**: 1.0.0
* **Release Stage**: MVP
* **Lead Proyectos**: Laura Dominguez
* **AI Team**: Jorge Castro
* **Servidor**: `https://ai-service.scalevision.app/ai/v1`

## 2. Restricciones Globales (Constraints)

* **Tamaño máximo de archivo**: 100 MB
* **Duración máxima de video**: 240 segundos (4 minutos)
* **FPS máximo**: 30
* **Resolución máxima**: 1920x1080
* **Ratio de aparición mínimo**: 0.8
* **Codecs permitidos**: h264
* **Contenedores**: mp4, mov
* **Tecnologías Core**: YOLO, ByteTrack, FFmpeg

## 3. Políticas de Ejecución y Resiliencia

### 3.1 Modo Seguro (Safe Mode)

* **Umbral de confianza**: 0.6
* **Estrategia Fallback**: `CENTER_CROP`
* **Disparadores**: Errores de IA recuperables o baja confianza en la detección
* **Descripción**: Si la IA falla de forma recuperable, el sistema ignora coordenadas dinámicas y aplica un recorte central estático.

### 3.2 Expiración de Tareas (Job Expiration)

* **Timeout**: 120 segundos
* **Acción en Timeout**: El Backend debe consultar el estado vía polling o marcar el trabajo como expirado.

---

## 4. Definición de Endpoints

### Fase 1: Descubrimiento de Sujetos (Discovery)

**POST `/scan`**

* **Cuerpo**: `{ "job_id": "UUID", "video_url": "OCI_PAR_URL" }`

**GET `/scan/{job_id}` (Polling)**

* **PENDING**: Video aceptado en cola.
* **PROCESSING**: Analizando contenido.
* **SUCCESS**: Retorna lista de sujetos con `thumbnail_base64`, `appearance_ratio` y `reference_data` (timestamp y bounding box).

### Fase 2: Ejecución de Recorte (Execution)

**POST `/process-video`**

* **Cuerpo**: `{ "job_id": "UUID", "target_subject_id": "ID", "output_bucket_path": "PATH" }`

**GET `/process-video/{job_id}` (Polling)**

* **PROCESSING**: Renderizando (Stage: `FFMPEG_CROP`).
* **SUCCESS**: Retorna `output_video_url` y la estrategia aplicada (`DYNAMIC_CROP` o `CENTER_CROP`).

---

## 5. Glosario de Errores

* **MAX_DURATION_EXCEEDED**: Video excede los 240s.
* **RENDERING_FAILED**: Error crítico en FFmpeg.
