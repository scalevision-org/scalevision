# Contrato API FE - Backend MVC

Base URL:

- `http://localhost:8080/svmvp`

## 1) Subir video

- **POST** `/videos/subir`

Request:

```json
{
  "nombre": "mi-video-demo",
  "nickname": "cliente1",
  "tamano": 50.0,
  "formato": "mp4",
  "duracion": 60
}
```

Response `201`:

```json
{
  "id": 1,
  "urlVideoOriginal": "https://cdn.scalevision.local/videos/1/mi-video-demo.mp4",
  "estado": "SUBIDO"
}
```

## 2) Estado de procesamiento (polling)

- **GET** `/videos/estado/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "PROCESANDO",
  "error": null
}
```

## 3) Obtener mini-vistas

- **GET** `/videos/mini-vistas/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "PROCESADO",
  "urlMiniVista01": "https://cdn.scalevision.local/videos/1/mini-1.jpg",
  "urlMiniVista02": "https://cdn.scalevision.local/videos/1/mini-2.jpg",
  "urlMiniVista03": "https://cdn.scalevision.local/videos/1/mini-3.jpg"
}
```

## 4) Cortar video

- **POST** `/videos/cortar-video/{id}`

Request:

```json
{
  "urlMiniVista": "https://cdn.scalevision.local/videos/1/mini-2.jpg"
}
```

Response `200`:

```json
{
  "id": 1,
  "estado": "CORTANDO",
  "urlVistaSeleccionada": "https://cdn.scalevision.local/videos/1/mini-2.jpg"
}
```

## 5) Obtener video final (polling)

- **GET** `/videos/final/{id}`

Response `200`:

```json
{
  "id": 1,
  "estado": "CORTADO",
  "urlVideoFinal": "https://cdn.scalevision.local/videos/1/final-vertical.mp4"
}
```

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
  "message": "detalle del error",
  "timestamp": "2026-02-27T10:30:00"
}
```
