# ScaleVision Backend MVC (MVP)

Backend en Java 21 + Spring Boot 3 para flujo de videos con polling.

## Estructura MVC

- `controller/`: expone endpoints REST para FE.
- `service/`: reglas de negocio y transiciones de estado.
- `repository/`: acceso a H2 con Spring Data JPA.
- `entity/`: modelo `VideoPoc` y enum `VideoStatus`.
- `dto/`: contratos request/response.
- `config/`: CORS y manejo global de errores.

## Requisitos

- Java 21
- Maven 3.9+

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
- Password: vacio

## Endpoints FE

Ver contrato completo en:

- `docs/API_CONTRACT_FE.md`

## Flujo (polling)

1. FE llama `POST /videos/subir`.
2. FE hace polling a `GET /videos/estado/{id}` cada 3-5s.
3. Cuando llega a `PROCESADO`, FE consulta `GET /videos/mini-vistas/{id}`.
4. FE envía mini-vista elegida con `POST /videos/cortar-video/{id}`.
5. FE hace polling a `GET /videos/final/{id}` hasta obtener `CORTADO`.
