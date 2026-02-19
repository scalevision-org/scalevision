package com.scalevision.backend.domain.model;

public record VideoMetadata(Integer duration,
                            String resolution,
                            String format,
                            Long fileSize) {
    public VideoMetadata{
        if (duration == null || duration <= 0){
            throw new IllegalArgumentException("La Duración debe ser mayor que 0");
        }
        if (resolution == null || !resolution.matches("\\d+x\\d+")){
            throw new IllegalArgumentException("La Resolución debe estar en formato WxH (por ejemplo, 1920x1080)");
        }
        if (format == null || format.isBlank()){
            throw new IllegalArgumentException("El Formato no puede ser nulo o vacío");
        }
        if (fileSize == null || fileSize <= 0){
            throw new IllegalArgumentException("El Tamaño del Archivo debe ser mayor que 0");
        }
    }
}
