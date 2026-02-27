package com.scalevision.backend.dto;

public class EstadoVideoResponse {

    private Long id;
    private String estado;
    private String error;

    public EstadoVideoResponse(Long id, String estado, String error) {
        this.id = id;
        this.estado = estado;
        this.error = error;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getError() {
        return error;
    }
}
