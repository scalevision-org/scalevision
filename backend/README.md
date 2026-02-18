# ScaleVision Backend (MVP)

Backend del proyecto ScaleVision para procesar videos y generar shorts verticales.

## Objetivo del backend

- Exponer API REST para iniciar procesamiento de video.
- Consultar estado del trabajo con polling HTTP.
- Recibir callback de IA cuando termina o falla.
- Mantener el modelo de dominio con arquitectura hexagonal.

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Maven Wrapper
- Spring Web
- Spring Data JPA
- H2 (runtime)
- Bean Validation
- Lombok

## Arquitectura

Se usa arquitectura hexagonal:

- `domain`: entidades y reglas de negocio.
- `application`: casos de uso y puertos.
- `infrastructure`: controladores REST y adaptadores externos.

Más detalle en:

- `docs/ARCHITECTURE.md`

## API actual (MVP)

<<<<<<< HEAD
- `POST /videos/process`: crea un job de procesamiento.
- `GET /jobs/{jobId}`: consulta estado por polling.
- `POST /callbacks/ai`: callback del servicio IA.

Contrato completo y ejemplos en:

- `docs/API.md`

## Flujo de polling

1. Frontend llama `POST /videos/process`.
2. Backend responde `202 Accepted` con `jobId`.
3. Frontend consulta `GET /jobs/{jobId}` cada 2-5 segundos.
4. Servicio IA llama `POST /callbacks/ai` con `COMPLETED` o `FAILED`.
5. Frontend sigue consultando hasta estado final.

## Puertos de trabajo

- Backend: `http://localhost:8080`
- IA: `http://localhost:8000/svmvp` (base URL configurable)
- Frontend: `http://localhost:3000`

## Configuración mínima

Archivo actual:

- `src/main/resources/application.properties`

Propiedad disponible:

- `spring.application.name=scalevision-backend`

Propiedad opcional para IA:

=======
Endpoints oficiales con prefijo:

- `POST /svmvp/ai/scan-subjects`: fase A (discovery).
- `POST /svmvp/videos/process`: fase B (inicio async).
- `GET /svmvp/jobs/{jobId}`: estado del job (polling).
- `POST /svmvp/callbacks/ai`: callback seguro IA -> Backend.
- `GET /svmvp/ai/job/{jobId}`: proxy polling a worker IA.
- `GET /svmvp/ai/health`: healthcheck del worker IA.

Contrato completo y ejemplos en:

- `docs/API.md`

## Flujo de polling

1. Frontend llama `POST /videos/process`.
2. Backend responde `202 Accepted` con `jobId`.
3. Frontend consulta `GET /svmvp/jobs/{jobId}` cada 2-5 segundos.
4. Servicio IA llama `POST /svmvp/callbacks/ai` con `COMPLETED` o `FAILED`.
5. Frontend sigue consultando hasta estado final.

## Puertos de trabajo

- Backend: `http://localhost:8080/svmvp`
- IA: `http://localhost:8000/svmvp` (base URL configurable)
- Frontend: `http://localhost:3000`

## Configuración mínima

Archivo actual:

- `src/main/resources/application.properties`

Propiedad disponible:

- `spring.application.name=scalevision-backend`
- `ai.callback.schema-version=1.0.0`

Propiedad opcional para IA:

>>>>>>> origin/feature/backend-contract-alignment-v2
- `ai.service.base-url=http://localhost:8000/svmvp`

## Ejecución local

Desde `/Users/tinus/Developer/scalevision/backend`:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Desarrollo por actividades

- Base principal backend: `scalevision-backend`
- Ramas de trabajo: `feature/backend-activity-N-...`
- Flujo recomendado: feature -> PR a `scalevision-backend` -> merge -> borrar feature

Guía práctica de trabajo en ramas y PR:

- `docs/DEVELOPMENT.md`

## Estado actual del MVP

- Dominio base implementado (`ProcessingJob`, `JobStatus`).
- Casos de uso de iniciar proceso y consultar estado.
- Contrato inicial IA por HTTP validado con tests de contrato.
- Polling implementado como estrategia oficial (sin WebSocket en MVP).

## Equipo Backend

- Backend Lead: [Florentino López](https://github.com/TinusLopez)
- Backend Dev: [Edwin Mancilla](https://github.com/edwinmancilla)
