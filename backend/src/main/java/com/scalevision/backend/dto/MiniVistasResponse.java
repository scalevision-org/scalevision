package com.scalevision.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MiniVistasResponse {

    private Long id;
    private String estado;
    @JsonProperty("url_mini_vista_01")
    private String urlMiniVista01;
    @JsonProperty("url_mini_vista_02")
    private String urlMiniVista02;
    @JsonProperty("url_mini_vista_03")
    private String urlMiniVista03;
    @JsonProperty("fallback_active")
    private Boolean fallbackActive;
    @JsonProperty("fallback_strategy")
    private String fallbackStrategy;
    @JsonProperty("fallback_reason")
    private String fallbackReason;

    public MiniVistasResponse(
            Long id,
            String estado,
            String urlMiniVista01,
            String urlMiniVista02,
            String urlMiniVista03,
            Boolean fallbackActive,
            String fallbackStrategy,
            String fallbackReason
    ) {
        this.id = id;
        this.estado = estado;
        this.urlMiniVista01 = urlMiniVista01;
        this.urlMiniVista02 = urlMiniVista02;
        this.urlMiniVista03 = urlMiniVista03;
        this.fallbackActive = fallbackActive;
        this.fallbackStrategy = fallbackStrategy;
        this.fallbackReason = fallbackReason;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getUrlMiniVista01() {
        return urlMiniVista01;
    }

    public String getUrlMiniVista02() {
        return urlMiniVista02;
    }

    public String getUrlMiniVista03() {
        return urlMiniVista03;
    }

    public Boolean getFallbackActive() {
        return fallbackActive;
    }

    public String getFallbackStrategy() {
        return fallbackStrategy;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }
}
