## ScaleVision AI Service (MVP)

Servicio de backend de Inteligencia Artificial para la detección automatizada de sujetos y procesamiento de recorte de video (Smart Cropping). Diseñado para ScaleVision bajo un esquema de Desarrollo Basado en Contratos (Contract-Driven Development).

## Descripción General

Este servicio implementa el pipeline de procesamiento de video en dos fases críticas:

**Fase 1 (Scan):** Análisis del video original mediante modelos de visión (YOLO/ByteTrack) para identificar sujetos de interés o habilitar un modo de recorte manual/central.

**Fase 2 (Process):** Ejecución del renderizado mediante FFmpeg aplicando la estrategia seleccionada (FACE_TRACKING o CENTER_CROP).

## Arquitectura Técnica

Framework: FastAPI (Python 3.9+)

Validación de Datos: Pydantic (Alineación estricta con contratos JSON v2.1.0)

Gestión de Estado: Máquina de estados asíncrona simulada en memoria.

Bypass de IA: Lógica integrada para omitir el procesamiento pesado cuando el usuario selecciona modo_corte: center, optimizando el Time-to-Render.

## Flujo de Trabajo (Endpoints)

**Phase 1:** Análisis (Scan)

POST /scan: Registra el video y define el modo de análisis.

Modo face_tracking: Activa detección de sujetos.

Modo center_crop: Bypass inmediato hacia estrategia de respaldo.

GET /scan/{id}: Consulta el estado del análisis. Devuelve metadatos de sujetos detectados o sugerencias de fallback.

**Phase 2:** Procesamiento (Render)

POST /process-video: Recibe la orden de recorte. Valida que el target_subject_id exista si la estrategia es dinámica.

GET /process-video/{id}: Entrega la URL final del video procesado y metadatos de renderizado (resolución, tamaño, tiempo).

## Reglas de Negocio Implementadas

Validación de Estrategia: No se permite ejecutar un FACE_TRAKING sin un target_subject_id válido obtenido en la Fase 1.

Guardias de Estado: El proceso de renderizado (Fase 2) está bloqueado hasta que la Fase 1 devuelva un estado PROCESADO.

Bypass Eficiente: Si se inicializa en modo center, el sistema ignora las esperas de los modelos YOLO para permitir una experiencia de usuario instantánea.

## Instalación y Ejecución

Instalar dependencias:

```bash
pip install --no-cache-dir -r requirements.txt
```

Iniciar el servidor:

```bash
uvicorn main:app
```


## Contratos de Interfaz

El servicio cumple rigurosamente con las especificaciones definidas en:

- request_scan.json (v2.0.0)

- request_process.json (v2.1.0)

<br>Lead Developer: Laura Dominguez
<br>AI Developer: Jorge Castro
<br>Estado del Proyecto: MVP - EN PRUEBAS DE INTEGRACION