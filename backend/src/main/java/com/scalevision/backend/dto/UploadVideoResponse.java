package com.scalevision.backend.dto;

public class UploadVideoResponse {

    private Long id;
    private String urlVideoOriginal;
    private String estado;

    public UploadVideoResponse(Long id, String urlVideoOriginal, String estado) {
        this.id = id;
        this.urlVideoOriginal = urlVideoOriginal;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getUrlVideoOriginal() {
        return urlVideoOriginal;
    }

    public String getEstado() {
        return estado;
    }
}
