package com.scalevision.backend.dto;

public class CortarVideoResponse {

    private Long id;
    private String estado;
    private String urlVistaSeleccionada;

    public CortarVideoResponse(Long id, String estado, String urlVistaSeleccionada) {
        this.id = id;
        this.estado = estado;
        this.urlVistaSeleccionada = urlVistaSeleccionada;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getUrlVistaSeleccionada() {
        return urlVistaSeleccionada;
    }
}
