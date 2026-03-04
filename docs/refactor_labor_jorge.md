# Reporte de Refactorización: Módulo de Reconocimiento y Reglas de Negocio (Fase 1)

**Fecha:** 2 de Marzo, 2026  
**Responsable:** Laura Domínguez (Tech Lead / AI Architect)  
**Módulo:** `modules/recognition.py` (Anteriormente `detect_people.py`)  

## 1. Contexto y Valor de la Aportación Inicial
El script exploratorio `detect_people.py` desarrollado por Jorge validó con éxito la viabilidad técnica de nuestro core de IA. Su prueba de concepto demostró que la integración de **YOLO con ByteTrack** es funcional y capaz de detectar sujetos en tiempo real. 

**Aportaciones retenidas en la arquitectura final:**
* El uso del modelo `yolov8n.pt` / `yolo26n.pt` para optimizar latencia.
* La activación del tracker mediante el parámetro `persist=True` y `tracker="bytetrack.yaml"`.
* La lógica base de iteración de frames con OpenCV (`cv2.VideoCapture`).

## 2. Por qué fue necesaria la refactorización (Brecha de Producción)
El script original era excelente para pruebas locales, pero presentaba impedimentos para operar en un entorno Cloud-Native y cumplir con el contrato de la API:

1. **Dependencia de Interfaz Gráfica (UI):** El código original dependía de `cv2.imshow` y `cv2.waitKey` para renderizar ventanas locales y pausar el video. En un contenedor Docker en OCI, esto causaría un error fatal, ya que los servidores no tienen entorno gráfico.
2. **Ausencia de Reglas de Negocio:** El sistema detectaba a cualquier persona que pasara frente a la cámara, sin importar cuánto tiempo estuviera en pantalla. 
3. **Ausencia de Extracción de Artefactos:** El código original dibujaba cuadros verdes sobre el video, pero no extraía las miniaturas limpias que el Frontend necesita para la selección del usuario.

## 3. Implementación de las Reglas de Negocio (El Nuevo Estándar)
Se refactorizó el código hacia una clase modular (`VideoRecognizer`) totalmente desacoplada, implementando las siguientes reglas estrictas de ScaleVision:

### A. Filtro de Relevancia (Regla del 70%)
Se implementó un diccionario en memoria que cuenta exactamente en cuántos frames aparece cada ID persistente asignado por ByteTrack. Al finalizar el video, el sistema calcula el ratio (`frames_presente / frames_totales`). **Solo los sujetos que aparecen en el 70% o más del video son enviados al Backend.**

### B. Límite de Candidatos (Máximo 3)
Para no saturar la UI del Frontend y mantener la carga útil (payload) ligera, el algoritmo ordena a los sujetos válidos por su ratio de aparición y retorna un arreglo estricto con un máximo de **3 candidatos**.

### C. Extracción Dinámica de Miniaturas
En lugar de tomar un frame general al inicio del video, el nuevo módulo intercepta las coordenadas del Bounding Box (YOLO) en tiempo de ejecución y usa *Slicing* de matrices de NumPy (`frame[y1:y2, x1:x2]`) para recortar exactamente el rostro/cuerpo del sujeto, guardándolo como la miniatura oficial para el Frontend.

### D. Alineación con Salida Vertical (9:16) y 720p
El módulo de reconocimiento ahora se integra con el orquestador general que asegura que, una vez que el usuario devuelve el ID del sujeto seleccionado a través de la API, el motor de renderizado genera un archivo estrictamente en formato MP4, con códec H.264, y resolución **720x1280 (Vertical)**.

## 4. Conclusión
El trabajo de investigación de Jorge nos ahorró tiempo de validación del modelo. La refactorización actual envuelve esa validación en un motor matemático sin estado (Stateless), preparado para ser desplegado en los workers de OCI y comunicarse de forma asíncrona con el orquestador principal de Spring Boot.