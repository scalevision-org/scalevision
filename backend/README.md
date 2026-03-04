# ScaleVision Backend MVC + IA (MVP)

Backend en Java 21 + Spring Boot 3 con integración real hacia servicio IA (FastAPI/Python).

## Estructura MVC

- `controller/`: expone endpoints REST para FE.
- `service/`: reglas de negocio + orquestación BE <-> IA.
- `repository/`: acceso a H2 con Spring Data JPA.
- `entity/`: modelo `VideoPoc`, `VideoStatus`, `ModoCorte`.
- `dto/`: contratos request/response.
- `config/`: CORS y manejo global de errores.

## Requisitos

- Java 21
- Maven 3.9+
- Servicio IA levantado en `http://localhost:8000` (o URL configurada)

## Ejecutar local

```bash
cd backend
mvn clean spring-boot:run
```

Base URL local:

- `http://localhost:8080/svmvp`

H2 Console:

- `http://localhost:8080/svmvp/h2-console`
- JDBC URL: `jdbc:h2:mem:scalevisionmvc`
- User: `sa`
- Password: vacío

## Configuración IA (application.yml)

- `app.ai.base-url`: URL del servicio IA (default `http://localhost:8000`)
- `app.ai.local-processing-dir`: carpeta local para copiar archivo hacia entorno IA local

## Storage local

- `uploads/originals/` para video original subido.
- `uploads/finals/` para video final cortado.
- `uploads/thumbnails/` para mini-vistas.

## Endpoints FE

Ver contrato completo en:

- `docs/API_CONTRACT_FE.md`

Lista rápida:

- `POST http://localhost:8080/svmvp/videos/subir`
- `GET http://localhost:8080/svmvp/videos/estado/{id}`
- `GET http://localhost:8080/svmvp/videos/mini-vistas/{id}`
- `POST http://localhost:8080/svmvp/videos/cortar-video/{id}`
- `GET http://localhost:8080/svmvp/videos/final/{id}`

## Flujo real (Fase 1 + Fase 2)

1. FE sube video en `/videos/subir`.
2. BE registra video en H2 y llama IA `POST /scan`.
3. FE consulta estado en `/videos/estado/{id}`; BE consulta IA `GET /scan/{id}`.
4. Cuando IA responde `PROCESADO`, BE expone mini-vistas y fallback.
5. FE solicita corte en `/videos/cortar-video/{id}`.
6. BE llama IA `POST /process-video`.
7. FE consulta `/videos/final/{id}`; BE consulta IA `GET /process-video/{id}`.
8. Cuando IA responde `RENDER_COMPLETED/CORTADO`, BE expone `urlVideoFinal`.
