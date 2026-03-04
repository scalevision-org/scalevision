package com.scalevision.backend.dto;

public class MiniVistasResponse {

    private Long id;
    private String estado;
    private String urlMiniVista01;
    private String urlMiniVista02;
    private String urlMiniVista03;

    public MiniVistasResponse(Long id, String estado, String urlMiniVista01, String urlMiniVista02, String urlMiniVista03) {
        this.id = id;
        this.estado = estado;
        this.urlMiniVista01 = urlMiniVista01;
        this.urlMiniVista02 = urlMiniVista02;
        this.urlMiniVista03 = urlMiniVista03;
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
}
