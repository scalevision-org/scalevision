# Documentación Técnica MVP - ScaleVision

ScaleVision es una arquitectura distribuida de microservicios diseñada para el recorte inteligente y procesamiento automático de videos (Reframing/Center Crop/Face Tracking) utilizando Inteligencia Artificial.

El sistema completo se divide en tres componentes principales orquestados bajo un mismo entorno mediante Docker y expuestos a través de un proxy inverso.

---

## 1. Frontend (Aplicación Web)

La interfaz de usuario está construida para ser rápida, reactiva y ofrecer un feedback constante del estado del procesamiento de video al usuario final.

**Tecnologías Principales:**
- **React 18 / Vite:** Entorno de construcción ultrarrápido y renderizado optimizado de componentes. Vite ofrece una recarga en caliente extremadamente veloz durante el desarrollo.
- **TypeScript:** Agrega tipado estático estricto, reduciendo errores en tiempo de ejecución y escalando el código de forma más segura e intuitiva.
- **Tailwind CSS:** Framework de CSS utilitario. Permite estilizar rápidamente los componentes y ofrece soporte nativo e impecable para `dark mode`.
- **Zustand:** Manejador de estado global de la aplicación. Extremadamente ligero en comparación con Redux. Gestiona estados centrales como el `videoProcess.store`.
- **React Router DOM:** Permite la navegación sin recargas (`SPA` - Single Page Application), gestionando transiciones suaves entre la vista de subida (`/upload`) y resultados (`/preview`).
- **Lucide React & Sonner:** Librería de iconografía dinámica nativa y sistema avanzado de notificaciones "Toasts" (para mantener informado al usuario de cargas, errores y éxitos invisibles).

**Soluciones Técnicas Clave:**
- Reproductor Mockup 100% manipulable que evita las restricciones de navegadores sobre `auto-play` interactuando asincrónicamente con la API de `<video>`.
- Manipulación de respuesta en red (conversión de Fetch a `Blob(video/mp4)`) para forzar la descarga de videos generados desde la IA burlando de forma segura las políticas Cross-Origin.

---

## 2. Backend (Orquestador y Persistencia)

Actúa como el coordinador central del sistema. Expone los endpoints robustos hacia el Front, mantiene la integridad de los datos y orquesta la comunicación delegando el trabajo pesado al servicio de Inteligencia Artificial. Sigue los principios de la **Arquitectura Hexagonal**.

**Tecnologías Principales:**
- **Java 17 & Spring Boot 3:** Framework robusto de grado empresarial para la creación de la API RESTful de manera confiable.
- **Maven:** Gestor inteligente de ciclos de vida, dependencias y empaquetado final (`.jar`).
- **H2 Database (In-Memory):** Base de datos relacional ligera operando directamente en memoria RAM, ideal para esta etapa del MVP, garantizando velocidad I/O extrema al guardar los estados.
- **Hibernate / JPA:** Mapeo Objeto-Relacional (ORM) que simplifica sustancialmente las transacciones con la base de datos sin redactar SQL puro.
- **RestTemplate / HTTP Client:** Componentes que permiten enviar llamadas al servicio de la IA sin frenar ni bloquear el Event Loop que atiende al Frontend.

**Soluciones Técnicas Clave:**
- Aislamiento total de los puertos, exponiendo únicamente rutas seguras empezando por `/api/`.
- Gestión de almacenamiento de Archivos Multiparte temporales.
- Máquina de estados inmutable que protege el flujo asegurando que un video pase rigurosamente por `SUBIDO` ➔ `PROCESANDO` ➔ `PROCESADO` ➔ `CORTANDO` ➔ `CORTADO`.

---

## 3. Inteligencia Artificial (Motor de Video)

Un microservicio aislado e independiente configurado para operar como "Worker Background". Especializado al 100% en cálculos de Visión Computacional, operaciones matriciales de imágenes y recodificación de hardware.

**Tecnologías Principales:**
- **Python 3.10+:** Ecosistema definitivo para Machine Learning y Data Science.
- **FastAPI:** Framework asíncrono para construir APIs bajo los estándares OpenAPI. Altísimo rendimiento a la altura de NodeJS y Go.
- **Uvicorn:** Servidor ASGI ultrarrápido responsable de correr FastAPI de fondo.
- **YOLOv8 (Ultralytics):** Conjunto de modelos neuronales pre-entrenados del Estado del Arte. Utilizado para el escaneo cuadro a cuadro, detectando cajas delimitadoras (`bounding boxes`), rostros y siluetas humanas con máxima precisión (Confidence > 0.6).
- **OpenCV (cv2):** Librería líder en manipulación de imágenes y visión artificial. Responsable del recorte en memoria (RAM) y la generación ultrarrápida de vistas previas (Thumbnails) base64 y matrices Numpy.
- **ByteTrack / SORT:** Algoritmos complementarios de seguimiento (`Tracking`) que asignan un ID a cada humano manteniéndolo en foco constante sin importar que se crucen entre la escena.
- **FFmpeg:** El motor industrial subyacente de renderizado interactuando directamente a nivel de sistema. Ejecuta recortes de proporciones personalizadas y exporta sin recomprimir desmesuradamente el Bitrate (Fast Rendering).

**Soluciones Técnicas Clave:**
- **Background Tasks:** El servidor jamás detiene su ejecución frente a un usuario; recibe los audios/videos, responde estatus HTTP `202 Petición Aceptada` y disocia el renderizado pesado hacia hilos paralelos (Workers).
- **StaticFiles:** Alojamiento estático para la entrega transparente de metadatos procesados como minivistas locales en disco y videos exportados evitando que Java tenga que triangular las respuestas pesadas en Base64.
- Corrección matemática para encuadres dinámicos (Padding algorítmico evitando que un recorte sea menor que el margen del dispositivo).

---

## 4. Orquestación e Infraestructura (Docker)

ScaleVision está diseñado íntegramente de una forma "Container-Native", asegurando que "Si funciona en dev, funciona en la nube".

**Tecnologías y Estructura:**
- **Docker Engines:** Cada uno de los 3 microservicios mencionados vive en su ecosistema aislado a nivel sistema operativo. 
  - *React* se empaqueta estáticamente dentro del micro servidor de `Node-Alpine`.
  - *Java* se empaca y corre transparentemente empleando `Temurin/JRE-Alpine`.
  - *Python* utiliza una capa enriquecida con `Python 3.10-Slim`, inyectándole librerías críticas de Linux de bajo nivel como `libgl1` y `ffmpeg`.
  
- **Docker Compose:** Administrador de red. Con el simple manifiesto `docker-compose.yml`, los tres repositorios independientes de ScaleVision logran identificarse, entablar red en puente (Bridge Network) y declarar sus políticas de reinicio de forma robusta.

- **Nginx (Reverse Proxy):** Contenedor adicional implementado en el **Puerto 80**. Representa la única puerta de conexión desde y hacia el exterior (Capa DMZ). 
  Mediante enrutamiento inverso de URL:
  - Todo el tráfico base `/` recae en el sistema enrutador dinámico (`React Router`) del Frontend.
  - Toda ruta `/api/` fluye hacia la API estricta por puertos oscuros hacia Java Spring.
  - Toda ruta `/data/` intercepta archivos estáticos renderizados provenientes de FastAPI (IA).

*Esta arquitectura bloquea de tajo vulnerabilidades de CORS, inyecciones externas y estandariza el Despliegue en 1 solo clic ("One-Click Deploy").*
