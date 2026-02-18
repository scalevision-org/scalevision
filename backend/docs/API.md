<<<<<<< HEAD
# API Backend MVP

Base URL backend local:

- `http://localhost:8080`

## 1) Crear procesamiento

Endpoint:

- `POST /videos/process`
=======
# API Contract (Backend MVP + Spec v2)

Base URL oficial backend:

- `http://localhost:8080/svmvp`

Compatibilidad temporal:

- También existen rutas sin prefijo `/svmvp`.

## 1) Fase A - Descubrimiento de sujetos

Endpoint:

- `POST /ai/scan-subjects`
>>>>>>> origin/feature/backend-contract-alignment-v2

Request:

```json
{
<<<<<<< HEAD
  "videoUrl": "https://example.com/video.mp4",
  "targetAspectRatio": "9:16",
  "targetDuration": 30,
  "focusArea": "speaker"
}
```

Respuesta exitosa:

- Status: `202 Accepted`

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
=======
  "video_url": "https://objectstorage.../video.mp4?par=...",
  "min_appearance_ratio": 0.8,
  "config": {
    "max_subjects": 3
  }
}
```

Response `200`:

```json
{
  "status": "success",
  "subjects": [
    {
      "tempId": "subject_01",
      "thumbnailBase64": "data:image/jpeg;base64,...",
      "appearanceRatio": 0.92,
      "subjectType": "person",
      "referenceData": {
        "timestamp": 12.5,
        "box": [0.4, 0.2, 0.6, 0.5]
      }
    }
  ]
}
```

## 2) Fase B - Iniciar procesamiento

Endpoint:

- `POST /videos/process`

Request (extendido):

```json
{
  "video_url": "https://objectstorage.../video.mp4?par=...",
  "callback_url": "https://api.backend.com/svmvp/callbacks/ai",
  "webhook_secret": "sv_secret_job_scoped",
  "tracking_mode": "user_selected",
  "target_selection": {
    "reference_timestamp": 12.5,
    "reference_box": [0.4, 0.2, 0.6, 0.5]
  },
  "config": {
    "target_aspect_ratio": "9:16",
    "fps_sampled": 30,
    "include_trajectory_data": false
  }
}
```

Request simple (MVP también válido):

```json
{
  "videoUrl": "https://cdn.test/video.mp4",
  "targetAspectRatio": "9:16",
  "targetDuration": 30,
  "focusArea": "center"
}
```

Response `202`:

```json
{
  "jobId": "uuid",
>>>>>>> origin/feature/backend-contract-alignment-v2
  "status": "PROCESSING"
}
```

<<<<<<< HEAD
Errores comunes:

- `400 Bad Request` si `videoUrl` es inválido o falta.

## 2) Consultar estado (Polling)
=======
## 3) Polling de estado en Backend
>>>>>>> origin/feature/backend-contract-alignment-v2

Endpoint:

- `GET /jobs/{jobId}`

<<<<<<< HEAD
Ejemplo:

- `GET /jobs/b3b3f4cf-91be-46e4-a883-ef6d496f8f4a`

Respuesta en proceso:

- Status: `200 OK`

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
=======
Response `200`:

```json
{
  "jobId": "uuid",
>>>>>>> origin/feature/backend-contract-alignment-v2
  "status": "PROCESSING",
  "progressPercentage": 50,
  "result": null,
  "error": null
}
```

<<<<<<< HEAD
Respuesta final exitosa:

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
  "status": "COMPLETED",
  "progressPercentage": 100,
  "result": {
    "outputVideoUrl": "https://cdn.example.com/shorts/final.mp4"
  },
  "error": null
}
```

Respuesta final con error:

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
  "status": "FAILED",
  "progressPercentage": 100,
  "result": null,
  "error": {
    "code": "PROCESSING_ERROR",
    "message": "AI timeout",
    "retryable": false
  }
}
```

Error por job inexistente:

- `404 Not Found`

## 3) Callback de IA
=======
Cuando termina:

- `result.outputVideoUrl` (si aplica)
- `result.aiResultPayload` (payload bruto devuelto por IA en callback)

## 4) Callback IA -> Backend (seguro)
>>>>>>> origin/feature/backend-contract-alignment-v2

Endpoint:

- `POST /callbacks/ai`

<<<<<<< HEAD
Request (éxito):

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
  "status": "COMPLETED",
  "outputUrl": "https://cdn.example.com/shorts/final.mp4"
}
```

Request (fallo):

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
  "status": "FAILED",
  "errorMessage": "AI timeout"
}
```

También soporta alias en snake_case:

- `job_id`
- `output_url`
- `error_message`

Respuesta:

- Status: `200 OK`

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
=======
Headers recomendados:

- `Content-Type: application/json`
- `X-AI-Schema-Version: 1.0.0`
- `X-Webhook-Secret: <secret-del-job>`

Payload éxito:

```json
{
  "job_id": "uuid",
  "status": "completed",
  "processing_stats": {
    "duration_sec": 48.3
  },
  "crop_recommendations": [
    {
      "start_time": 0.0,
      "end_time": 48.3,
      "crop_box": [0.35, 0.0, 0.75, 1.0],
      "confidence": 0.98
    }
  ]
}
```

Payload error:

```json
{
  "job_id": "uuid",
  "status": "failed",
  "error_code": "MODEL_TIMEOUT",
  "retryable": true
}
```

Response `200`:

```json
{
  "jobId": "uuid",
>>>>>>> origin/feature/backend-contract-alignment-v2
  "status": "COMPLETED"
}
```

<<<<<<< HEAD
## Polling recomendado para frontend

- Frecuencia sugerida: cada `2` a `5` segundos.
- Terminar polling cuando `status` sea `COMPLETED` o `FAILED`.

## Integración con IA

Base URL configurada en backend:

- `ai.service.base-url` (default `http://localhost:8000/svmvp`)

Endpoint consumido por backend:

- `POST /ai/process-video`

Ruta completa por default:

- `http://localhost:8000/svmvp/ai/process-video`
=======
## 5) Polling/Health proxy hacia IA

Endpoints:

- `GET /ai/job/{jobId}`
- `GET /ai/health`

Objetivo:

- Permitir fallback de monitoreo si webhook falla o demora.

## 6) Contrato Backend -> IA (worker)

Base URL IA configurable:

- `ai.service.base-url` (default: `http://localhost:8000/svmvp`)

Llamadas que hace backend:

- `POST /ai/scan-subjects`
- `POST /ai/process-video`
- `GET /ai/job/{jobId}`
- `GET /health`
>>>>>>> origin/feature/backend-contract-alignment-v2
