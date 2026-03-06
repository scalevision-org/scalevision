package com.scalevision.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoFinalResponse {

    private Long id;
    private String estado;
    @JsonProperty("url_video_final")
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
