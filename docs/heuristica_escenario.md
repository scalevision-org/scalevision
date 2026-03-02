## 👥 Equipo Core de ScaleVision (MVP)

| Rol | Responsable | Enfoque Principal |
| :--- | :--- | :--- |
| **Tech Lead / AI Architect** | Laura Domínguez |- Arquitectura AI, - Orquestación Asíncrona (FastAPI), - Contratos y validación Backend - Renderizado Híbrido (FFmpeg/OpenCV). |
| **AI Developer** | Jorge | - Pruebas Unitarias y de Integracion (`pytest`, `unittest.mock`), Motores de Visión Computacional (YOLOv8/ByteTrack), optimizacion del motor computacional. |
<br>
---
# Actualización del Motor de IA: Heurística Espacial y Tolerancia a Oclusión

**Versión:** 1.2.0 (Fase 1 - Reconocimiento)
<br>**Objetivo:** Resolver falsos positivos en videos con público en primer plano (Foreground Bias) y pérdida de rastreo por cruces (ID Switches).

## El Problema (Evidencia Empírica)
Durante las pruebas con video real (`prueba_2.mp4` - Danza Folclórica), el modelo original presentaba dos fallas críticas en entornos complejos:
1. **Sesgo de Primer Plano (Foreground Noise):** La IA seleccionaba a las personas del público (ej. chica con lentes, señor en primera fila) porque sus rostros ocupaban un área mayor al estar cerca de la cámara y aparecían en el 100% del video.
2. **Colapso por Oclusión (ID Switch):** Los protagonistas reales (las bailarinas) se cruzan constantemente en el escenario. Al cruzarse las faldas, el motor *ByteTrack* pierde la certeza matemática, destruye el ID original y asigna uno nuevo. Debido a esto, las bailarinas jamás alcanzaban la regla estricta del **70% de aparición en pantalla**.

## La Solución Arquitectónica
Se refactorizó el motor de reconocimiento (`modules/recognition.py`) abandonando la estadística cruda e inyectando "Sentido Común Espacial".

### 1. Filtro de Zonas Muertas (Heurística Y-Axis)
Se implementó un límite espacial dinámico. Si el origen superior de una persona detectada (`y1`) comienza por debajo del **55% de la altura total del video**, el algoritmo lo clasifica automáticamente como "público o ruido de primer plano" y lo descarta. El encuadre ahora prioriza la mitad superior, donde ocurre la acción real (el escenario).

### 2. Tolerancia a Oclusión (Ratio 70% -> 40%)
Para absorber los fallos inherentes de cualquier IA de rastreo durante cruces rápidos (faldas superpuestas, giros), **se redujo el ratio mínimo de permanencia del 70% al 40%**. 
* **Por qué funciona:** Si una bailarina baila durante el 45% del video, se cruza con otra, y se le asigna un nuevo ID para el 55% restante, nuestro filtro de 40% asegura que su primer ID sobreviva la validación. Combinado con la heurística espacial, esto garantiza capturar a los protagonistas sin dejar entrar ruido temporal.

## Resultados (Validado en Pipeline)
* **Antes:** Falsos positivos (Auditorio/Público). Fallback activado.
* **Ahora:** Captura limpia de los 3 protagonistas principales con miniaturas generadas en su punto de máxima exposición (Mayor Área + 20% Padding). Sincronización de audio y recorte dinámico ejecutados con éxito.


## Histórico de Pruebas de Humo y Arquitectura (02/Mar/2026)

Este log documenta la evolución del motor de IA hasta alcanzar el grado de producción para el MVP:

1. **Validación de SO y Pipeline de Renderizado**
   * **Objetivo:** Ejecutar la tubería base de FFmpeg.
   * **Incidencia:** Fallo silencioso del sistema operativo al no encontrar filtros válidos de FFmpeg.
   * **Resolución:** Refactorización de `os.system()` a `subprocess.run()` para captura de `stderr` y manejo de excepciones críticas a nivel de servidor.

2. **Validación de Recorte Seguro (Center Crop)**
   * **Objetivo:** Forzar la salida a MP4 H.264 en resolución 720x1280.
   * **Resultado:** Éxito. Degradación segura comprobada.

3. **Validación de Tracking Dinámico y Media Móvil**
   * **Objetivo:** Seguimiento del sujeto y estabilización de cámara (Centroid Smoothing).
   * **Incidencia:** El *Bounding Box* crudo generaba saltos de cámara.
   * **Resolución:** Implementación de cálculo vectorial con NumPy (Moving Average de 30 frames) y centrado estricto de altura (Height Padding) para no cortar extremidades.

4. **Validación de Estados Asíncronos y Polling API**
   * **Objetivo:** Mantener el estado de los Jobs en memoria sin bloquear el hilo principal.
   * **Incidencia:** Pérdida de estado (Error 404) por regeneración de archivos y Hot Reloading de Uvicorn.
   * **Resolución:** Estabilización del servidor desactivando flags de desarrollo y verificando el paso de estados `SUBIDO` -> `PROCESANDO` -> `PROCESADO`.

5. **Prueba de Fuego: Oclusión Compleja y Sesgo Espacial**
   * **Objetivo:** Detectar sujetos bajo interferencia severa (Danza Folclórica).
   * **Incidencia:** Detección de falsos positivos (Público en primera fila) y pérdida de ID de las bailarinas por cruce de faldas.
   * **Resolución:** Implementación de Heurística de Escenario (ignorar rostros debajo del 55% de la pantalla) y reducción de la Regla de Negocio a un 40% de aparición para tolerar cortes de *ByteTrack*. Éxito confirmado.