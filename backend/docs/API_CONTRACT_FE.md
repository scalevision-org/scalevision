# Contrato API FE - Backend MVC

Base URL:

- `http://localhost:8080/svmvp`

## 1) Subir video

- **POST** `http://localhost:8080/svmvp/videos/subir`

Request (`multipart/form-data`):

- `file`: archivo de video (obligatorio)
- `modo_corte`: texto obligatorio con valores:
  - `face_tracking`
  - `center_crop`
- `nombre`: texto opcional
- `tamano`: numero decimal opcional
- `formato`: texto opcional (ejemplo: `MP4`)

Campos eliminados del contrato:

- `nickname`
- `duracion`

Ejemplo terminal:

```bash
curl -X POST "http://localhost:8080/svmvp/videos/subir" \
  -F "file=@/ruta/demo.mp4" \
  -F "modo_corte=face_tracking" \
  -F "nombre=video_demo_scalevision" \
  -F "tamano=15.7" \
  -F "formato=MP4"
```

Response `201`:

```json
{
  "id": 1,
  "urlVideoOriginal": "http://localhost:8080/svmvp/uploads/originals/1740969300000-uuid.mp4",
  "estado": "SUBIDO"
}
```

## 2) Estado de procesamiento (polling)

- **GET** `http://localhost:8080/svmvp/videos/estado/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "PROCESANDO",
  "error": null
}
```

## 3) Obtener mini-vistas

- **GET** `http://localhost:8080/svmvp/videos/mini-vistas/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "PROCESADO",
  "urlMiniVista01": "http://localhost:8080/svmvp/uploads/thumbnails/1-mini-1.jpg",
  "urlMiniVista02": "http://localhost:8080/svmvp/uploads/thumbnails/1-mini-2.jpg",
  "urlMiniVista03": "http://localhost:8080/svmvp/uploads/thumbnails/1-mini-3.jpg"
}
```

## 4) Cortar video

- **POST** `http://localhost:8080/svmvp/videos/cortar-video/{id}`

Request:

```json
{
  "urlMiniVista": "http://localhost:8080/svmvp/uploads/thumbnails/1-mini-2.jpg"
}
```

Response `200`:

```json
{
  "id": 1,
  "estado": "CORTANDO",
  "urlVistaSeleccionada": "http://localhost:8080/svmvp/uploads/thumbnails/1-mini-2.jpg"
}
```

## 5) Obtener video final (polling)

- **GET** `http://localhost:8080/svmvp/videos/final/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "CORTADO",
  "urlVideoFinal": "http://localhost:8080/svmvp/uploads/finals/1-final-1740969309999.mp4"
}
```

## Estructura de almacenamiento local (BE)

- `uploads/originals/` -> videos subidos por FE.
- `uploads/finals/` -> videos finales procesados/cortados.
- `uploads/thumbnails/` -> mini-vistas.

## Estados (enum)

- `SUBIDO`
- `PROCESANDO`
- `PROCESADO`
- `CORTAR`
- `CORTANDO`
- `CORTADO`
- `ERROR`

## Errores

Formato:

```json
{
  "code": "BAD_REQUEST",
  "status": 400,
  "message": "detalle tecnico del error",
  "userMessage": "mensaje amigable para FE/usuario",
  "details": ["campo: descripcion del error"],
  "suggestion": "accion recomendada",
  "path": "/svmvp/videos/estado/99",
  "requestId": "0f4f0a25-27f7-4f37-8b1e-35ce784dca6e",
  "timestamp": "2026-02-27T10:30:00"
}
```
