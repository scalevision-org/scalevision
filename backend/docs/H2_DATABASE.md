# Modelo H2 - VideoPoc

## Entidad principal

```mermaid
erDiagram
    VIDEOS {
        BIGINT id PK
        VARCHAR nombre
        VARCHAR nickname
        DOUBLE tamano
        VARCHAR formato
        INT duracion
        VARCHAR modo_corte
        VARCHAR estado
        VARCHAR ia_error_code
        VARCHAR error
        DATETIME fecha
        VARCHAR url_video_original
        VARCHAR ruta_archivo_local
        VARCHAR ruta_archivo_local_final
        VARCHAR ia_job_id
        VARCHAR url_mini_vista_01
        VARCHAR url_mini_vista_02
        VARCHAR url_mini_vista_03
        VARCHAR url_vista_seleccionada
        VARCHAR url_video_original_cortado
        BOOLEAN fallback_active
        VARCHAR fallback_strategy
        VARCHAR fallback_reason
        BOOLEAN activo
    }
```

## Notas

- Tabla: `videos`
- BD en memoria: `jdbc:h2:mem:scalevisionmvc`
- `estado` usa enum `VideoStatus`.
- `modo_corte` guarda estrategia activa (`face_tracking` o `center_crop`).
- `ia_job_id` es el id que BE usa para consultar `/scan` y `/process-video`.
- `ia_error_code` + `error` guardan errores propagados desde IA.
- `fallback_*` guarda estrategia sugerida cuando no se detectan sujetos.
