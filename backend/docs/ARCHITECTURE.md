# Arquitectura Backend (Hexagonal)

## Vista general

El backend usa arquitectura hexagonal para separar reglas de negocio de tecnología.

Capas:

- `domain`: modelo puro de negocio.
- `application`: casos de uso + puertos.
- `infrastructure`: REST, HTTP client, persistencia, config.

## Estructura actual

### 1. Domain

Ubicación: `src/main/java/com/scalevision/backend/domain`

- `model/ProcessingJob.java`
- `model/JobStatus.java`
- `exception/InvalidStatusTransitionException.java`

Responsabilidad:

- Reglas de transición de estado del job.
- Datos principales del procesamiento.

### 2. Application

Ubicación: `src/main/java/com/scalevision/backend/application`

Puertos de entrada (`port/in`):

- `ProcessVideoUseCase`
- `GetJobStatusUseCase`

Puertos de salida (`port/out`):

- `JobRepository`
- `AIServicePort`
- `NotificationPort` (reservado para evolución)

Servicios:

- `ProcessVideoService`
- `GetJobStatusService`

Responsabilidad:

- Coordinar flujo entre dominio y adaptadores.

### 3. Infrastructure

Ubicación: `src/main/java/com/scalevision/backend/infrastructure`

Adapters de entrada REST:

- `VideoController`
- `JobStatusController`
- `CallbackController`
- `GlobalExceptionHandler`

Adapter de salida IA:

- `AIServiceHttpAdapter`

Persistencia (modelo inicial):

- `JobEntity`
- `JobMapper`

## Flujo principal con polling

1. `POST /videos/process` crea job con estado `PENDING`.
2. Se llama al servicio IA y el job pasa a `PROCESSING`.
3. Frontend consulta `GET /jobs/{jobId}` por polling.
4. IA envía `POST /callbacks/ai` con `COMPLETED` o `FAILED`.
5. El backend actualiza estado y el frontend ve el estado final en el siguiente poll.

## Decisión técnica del MVP

- Se usa polling HTTP para sincronizar estados con frontend.
- No se usa WebSocket en esta versión MVP.

## Diagrama simple

```text
Frontend --POST /videos/process--> Backend
Frontend <--202 + jobId---------- Backend

Frontend --GET /jobs/{jobId}----> Backend (polling)
Frontend <--status/progress------ Backend

Backend --POST /ai/process-video-> IA
IA ------POST /callbacks/ai------> Backend
```
