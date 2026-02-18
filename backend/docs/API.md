# API Contract (Backend MVP + Spec v2)

Base URL oficial backend:

- `http://localhost:8080/svmvp`

Compatibilidad temporal:

- También existen rutas sin prefijo `/svmvp`.

## 1) Fase A - Descubrimiento de sujetos

Endpoint:

- `POST /ai/scan-subjects`

Request:

```json
{
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
  "status": "PROCESSING"
}
```

## 3) Polling de estado en Backend

Endpoint:

- `GET /jobs/{jobId}`

Response `200`:

```json
{
  "jobId": "uuid",
  "status": "PROCESSING",
  "progressPercentage": 50,
  "result": null,
  "error": null
}
```

Cuando termina:

- `result.outputVideoUrl` (si aplica)
- `result.aiResultPayload` (payload bruto devuelto por IA en callback)

## 4) Callback IA -> Backend (seguro)

Endpoint:

- `POST /callbacks/ai`

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
  "status": "COMPLETED"
}
```

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
