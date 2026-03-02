# AI_MVP_Scope_V1.md | ScaleVision AI Engineering

## 1. Objetivo Operativo

Establecer el alcance del pipeline de IA para el MVP, priorizando la resiliencia del sistema, el rastreo avanzado de objetos y la optimización de costos mediante infraestructura de alto rendimiento en **OCI (Oracle Cloud Infrastructure)**.

---

## 2. Especificaciones Técnicas Core

### A. Motor de Detección y Tracking

* **Detector:** **YOLO26-S** (Arquitectura NMS-Free). Elegido por su eliminación de cuellos de botella en post-procesamiento y reducción de latencia en un 40%.
* **Tracker:** **ByteTrack**. Implementado para asignar IDs únicos a cada objeto y mantener la continuidad del rastreo incluso en casos de oclusión parcial o cruces entre personas.
* **Procesamiento:** El servicio de IA descarga el video, realiza la inferencia frame-a-frame y aplica el overlay mediante FFmpeg.

### B. Restricciones y Guardrails

* **Peso Máximo de Archivo:** 100 MB por video (Control de costos de transferencia y almacenamiento).
* **Mecanismo de Status:** **Polling**. El Backend consultará el estado del job (`PENDING`, `PROCESSING`, `SUCCESS`, `FAILED`) y el porcentaje de avance cada 3-5 segundos a través de un endpoint dedicado en el microservicio de IA.

---

## 3. Estrategia de Resiliencia (Graceful Degradation)

Para evitar entregas vacías ante fallos críticos de hardware o timeouts:

* **Regla del 60%:** Si el proceso de IA alcanza al menos el **60% de progreso** antes de un error no recuperable, el sistema renderizará y entregará el video procesado hasta ese punto (recortado a la mitad).
* **Justificación:** En productos de video B2B, es preferible entregar un resultado parcial funcional que una pantalla de error, mejorando la percepción de fiabilidad del cliente.

---

## 4. Stack Tecnológico y Librerías

| Componente | Herramienta | Justificación |
| :--- | :--- | :--- |
| **Inferencia** | `ultralytics` (v2026) | Soporte nativo para YOLO26 y optimización de tensores. |
| **Rastreo** | `ByteTrack` | El estándar de la industria por su balance entre velocidad y precisión. |
| **Manipulación Video** | `FFmpeg-python` | Control granular de codecs y dibujo de overlays sin sobrecarga de memoria. |
| **Infraestructura** | **OCI (Oracle Cloud)** | **Justificación:** OCI ofrece instancias GPU (A10) y ARM (Ampere) a una fracción del costo de AWS, permitiendo escalar el MVP con mayor margen de utilidad. |
| **Comunicación** | `FastAPI` | Framework asíncrono ideal para manejar múltiples peticiones de Polling. |

---

## 5. Justificación de Arquitectura

1. **OCI vs AWS:** Se elige OCI por su modelo de costos predecible y su alto rendimiento en cargas de trabajo de IA. Esto permite reinvertir el presupuesto ahorrado en el pago de la deuda de capital y escalabilidad de la plataforma.
2. **YOLO26 + ByteTrack:** Esta combinación garantiza un output profesional sin "saltos" en las detecciones, posicionando a ScaleVision como un producto de gama alta desde su MVP.
3. **Eficiencia de Datos:** El límite de 100MB asegura que el pipeline no se sature, manteniendo los tiempos de respuesta dentro de los SLAs esperados por el mercado.

<br>

--------

**Elaborado por:** Laura Dominguez Ai Lead & Equipo AI
<br>**Fecha de Elaboración:** 12 de Febrero 2026
<br>**Actualizaciónes:**
<br> - **Fecha:** 16 de febrero de 2026
<br>
<br>**Aprobado por:**
