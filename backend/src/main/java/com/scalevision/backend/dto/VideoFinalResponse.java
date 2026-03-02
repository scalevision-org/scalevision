package com.scalevision.backend.dto;

public class VideoFinalResponse {

    private Long id;
    private String estado;
    private String urlVideoFinal;

    public VideoFinalResponse(Long id, String estado, String urlVideoFinal) {
        this.id = id;
        this.estado = estado;
        this.urlVideoFinal = urlVideoFinal;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getUrlVideoFinal() {
        return urlVideoFinal;
    }
}
