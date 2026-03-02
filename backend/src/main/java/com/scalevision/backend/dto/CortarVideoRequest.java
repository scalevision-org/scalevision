package com.scalevision.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CortarVideoRequest {

    @NotBlank(message = "Debe enviar la mini-vista seleccionada")
    private String urlMiniVista;

    public String getUrlMiniVista() {
        return urlMiniVista;
    }

    public void setUrlMiniVista(String urlMiniVista) {
        this.urlMiniVista = urlMiniVista;
    }
}
