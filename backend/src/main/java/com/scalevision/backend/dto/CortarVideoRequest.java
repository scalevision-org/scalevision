package com.scalevision.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CortarVideoRequest {

    @JsonProperty("mini_vista_id")
    private String urlMiniVista;

    public String getUrlMiniVista() {
        return urlMiniVista;
    }

    public void setUrlMiniVista(String urlMiniVista) {
        this.urlMiniVista = urlMiniVista;
    }
}
