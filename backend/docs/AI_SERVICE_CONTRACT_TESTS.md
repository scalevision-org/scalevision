# AI Service Contract Tests

## Objetivo
Validar el contrato HTTP entre Backend e IA para request de procesamiento y callback de resultados.

## Cobertura
### Request Backend -> IA (`POST /ai/process-video`)
- request con todos los campos -> aceptado
- request sin `job_id` -> error 400
- request con `video_url` inválida -> error 422
- timeout de IA -> error controlado

### Callback IA -> Backend (`POST /svmvp/callbacks/ai`)
- callback `COMPLETED` -> 200
- callback `FAILED` -> 200
- callback con `job_id` inexistente -> 404
- callback con formato inválido -> 400

## Herramientas
- JUnit 5
- WireMock
- MockMvc (standalone)

## Archivo de test
- `src/test/java/com/scalevision/backend/integration/AIServiceContractTest.java`
