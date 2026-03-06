# Instrucciones de Despliegue - ScaleVision MVP

Este documento contiene las instrucciones precisas para desplegar la arquitectura completa de ScaleVision (Frontend, Backend e IA) en un entorno de producción o pruebas utilizando Docker.

## Prerrequisitos

El servidor de destino debe contar con:
1. **Docker Engine** instalado y corriendo.
2. **Docker Compose** (V2 recomendado).
3. Conexión a internet para descargar las imágenes base oficiales.
4. **Puerto 80 libre**, ya que Nginx actuará como Reverse Proxy para toda la infraestructura en ese puerto.

---

## 1. Clonar los Repositorios

ScaleVision maneja sus microservicios en tres repositorios (o carpetas) independientes.
Asegúrate de clonar/posicionar los 3 repositorios **en el mismo directorio padre** para que los contextos de construcción en el `docker-compose.yml` funcionen correctamente por rutas relativas.

La estructura esperada en el servidor es:
```text
/ruta_padre/
 ├── scalevision/                  <-- Repositorio IA (Contiene este archivo y el docker-compose)
 ├── scalevision-sv-be-mvc/        <-- Repositorio Java/Spring Boot (Backend)
 └── scalevision-frontend/         <-- Repositorio React/Vite (Frontend)
```

Asegúrate de estar posicionado en las ramas correctas de integración:
- IA: `integration-ia-jorge-castro`
- Backend: `integration-backend-jorge-castro`
- Frontend: `integration-front-jorge-castro`

---

## 2. Iniciar la Interfaz de Despliegue

Sitúate en la raíz del repositorio de Inteligencia Artificial (donde vive el orquestador):

```bash
cd /ruta_padre/scalevision
```

Ejecuta el comando maestro de compilación y despliegue en segundo plano (`-d`):

```bash
docker-compose up -d --build
```

### ¿Qué hará exactamente este comando?
1. Descargará `nginx:alpine`, armando las reglas para interceptar en el puerto `80`.
2. Compilará el Frontend con Node y servirá el estático con Nginx `/` (Puerto nativo oculto).
3. Descargará Maven, compilará el `.jar` de Spring Boot y lo ejecutará levantando el backend en `http://backend:8080` (Aislado de la red pública, consumible por `http://x.x.x.x/api/`).
4. Empaquetará las dependencias de Python (OpenCV, FFmpeg, YOLO) y expondrá FastAPI a la red interna (consumible por `http://x.x.x.x/data/`).

---

## 3. Comprobación y Logs

Una vez que la línea de comandos finalice con el mensaje `Started scalevision_proxy`, abre tu navegador dirigiéndote a la IP pública o dominio de tu servidor en el puerto 80. Ejemplo:

`http://ip_del_servidor/`

Si necesitas revisar el estado en tiempo real, puedes engancharte a los logs unificados de los 4 servidores con:

```bash
docker-compose logs -f
```

O ver el de un contenedor en específico si notas lentitud en el backend o en el renderizado IA:

```bash
docker logs -f scalevision_backend
docker logs -f scalevision_ai
docker logs -f scalevision_frontend
```

## 4. Detener y Limpiar

Para apagar de forma segura toda la maquinaria liberando los puertos (sin borrar los videos generados almacenados localmente en `./scalevision_data`):

```bash
docker-compose down
```

Para destruirlos junto a las imágenes cacheadas y empezar totalmente desde cero:

```bash
docker-compose down --rmi all
```
