# API Backend MVP

Base URL backend local:

- `http://localhost:8080`

## 1) Crear procesamiento

Endpoint:

- `POST /videos/process`

Request:

```json
{
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
  "status": "PROCESSING"
}
```

Errores comunes:

- `400 Bad Request` si `videoUrl` es inválido o falta.

## 2) Consultar estado (Polling)

Endpoint:

- `GET /jobs/{jobId}`

Ejemplo:

- `GET /jobs/b3b3f4cf-91be-46e4-a883-ef6d496f8f4a`

Respuesta en proceso:

- Status: `200 OK`

```json
{
  "jobId": "b3b3f4cf-91be-46e4-a883-ef6d496f8f4a",
  "status": "PROCESSING",
  "progressPercentage": 50,
  "result": null,
  "error": null
}
```

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

Endpoint:

- `POST /callbacks/ai`

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
  "status": "COMPLETED"
}
```

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
