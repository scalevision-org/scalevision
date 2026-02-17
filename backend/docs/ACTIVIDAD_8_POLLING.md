# [BACKEND] Infrastructure - Configurar Polling

## Estado
Esta actividad reemplaza la actividad original:
- `[BACKEND] Infrastructure - Configurar WebSocket`

## Descripción
Configurar Polling para que el frontend consulte el estado del job en intervalos regulares, en lugar de usar WebSocket.

## Objetivos
- Implementar endpoint de estado para polling en backend.
- Exponer estado/progreso/resultado/error de un job.
- Implementar servicio de consulta de estado (`GetJobStatusService`).
- Integrar consulta de estado en el flujo actual (incluyendo callback IA).
- Estandarizar respuestas HTTP para estados no encontrados y errores.
- Escribir tests de endpoint y servicio.

## Archivos a crear
- `src/main/java/com/scalevision/backend/application/service/GetJobStatusService.java`
- `src/main/java/com/scalevision/backend/infrastructure/adapter/in/rest/JobStatusController.java`
- `src/main/java/com/scalevision/backend/infrastructure/adapter/in/rest/dto/JobStatusResponse.java`
- `src/test/java/com/scalevision/backend/application/service/GetJobStatusServiceTest.java`
- `src/test/java/com/scalevision/backend/infrastructure/adapter/in/rest/JobStatusControllerTest.java`

## Archivos a ajustar (si aplica)
- `src/main/java/com/scalevision/backend/application/port/in/GetJobStatusUseCase.java`
- `src/main/java/com/scalevision/backend/infrastructure/adapter/in/rest/GlobalExceptionHandler.java`
- `src/main/java/com/scalevision/backend/infrastructure/adapter/in/rest/CallbackController.java`

## Configuración Polling (Frontend <-> Backend)
- Endpoint polling: `GET /svmvp/jobs/{jobId}`
- Intervalo sugerido frontend: cada 2-3 segundos
- Timeout sugerido frontend: 120 segundos
- Estados: `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`, `EXPIRED`

## Contrato de respuesta (MVP)
### 200 OK (processing)
```json
{
  "jobId": "uuid",
  "status": "PROCESSING",
  "progressPercentage": 45,
  "result": null,
  "error": null
}
```

### 200 OK (completed)
```json
{
  "jobId": "uuid",
  "status": "COMPLETED",
  "progressPercentage": 100,
  "result": {
    "outputVideoUrl": "https://...",
    "cropRecommendations": []
  },
  "error": null
}
```

### 200 OK (failed)
```json
{
  "jobId": "uuid",
  "status": "FAILED",
  "progressPercentage": 100,
  "result": null,
  "error": {
    "code": "MODEL_TIMEOUT",
    "message": "Tracking exceeded timeout.",
    "retryable": true
  }
}
```

## Flujo
1. Frontend inicia proceso (`POST /svmvp/videos/process`).
2. Backend retorna `202 Accepted` + `jobId`.
3. Frontend consulta `GET /svmvp/jobs/{jobId}` cada 2-3 segundos.
4. Backend responde estado actual del job.
5. IA envía callback y backend actualiza estado.
6. Polling termina cuando `status = COMPLETED | FAILED | EXPIRED`.

## Casos de error
- `404 Not Found` -> Job no existe
- `400 Bad Request` -> jobId inválido
- `500 Internal Server Error` -> error inesperado

## Tests
- [ ] GET job existente en PROCESSING -> 200 + status correcto
- [ ] GET job COMPLETED -> 200 + result con outputVideoUrl
- [ ] GET job FAILED -> 200 + error con code/message
- [ ] GET job inexistente -> 404
- [ ] GET jobId inválido -> 400
- [ ] Cobertura de mapeo domain -> response

## Definición de Hecho
- [ ] Polling implementado en endpoint `GET /svmvp/jobs/{jobId}`
- [ ] `GetJobStatusService` implementado
- [ ] DTO de respuesta definido y documentado
- [ ] Tests unitarios y MockMvc pasando
- [ ] Integración con flujo de callback validada
- [ ] Sin dependencias WebSocket
- [ ] Documentación actualizada
- [ ] Code review aprobado
