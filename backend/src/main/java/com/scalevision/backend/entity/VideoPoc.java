package com.scalevision.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
public class VideoPoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String nickname;

    @Column(nullable = false)
    private Double tamano;

    @Column(nullable = false)
    private String formato;

    @Column(nullable = false)
    private Integer duracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus estado;

    private String error;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "url_video_original")
    private String urlVideoOriginal;

    @Column(name = "ruta_archivo_local")
    private String rutaArchivoLocal;

    @Column(name = "url_mini_vista_01")
    private String urlMiniVista01;

    @Column(name = "url_mini_vista_02")
    private String urlMiniVista02;

    @Column(name = "url_mini_vista_03")
    private String urlMiniVista03;

    @Column(name = "url_vista_seleccionada")
    private String urlVistaSeleccionada;

    @Column(name = "url_video_original_cortado")
    private String urlVideoOriginalCortado;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true;
        }
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getTamano() {
        return tamano;
    }

    public void setTamano(Double tamano) {
        this.tamano = tamano;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public VideoStatus getEstado() {
        return estado;
    }

    public void setEstado(VideoStatus estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getUrlVideoOriginal() {
        return urlVideoOriginal;
    }

    public void setUrlVideoOriginal(String urlVideoOriginal) {
        this.urlVideoOriginal = urlVideoOriginal;
    }

    public String getRutaArchivoLocal() {
        return rutaArchivoLocal;
    }

    public void setRutaArchivoLocal(String rutaArchivoLocal) {
        this.rutaArchivoLocal = rutaArchivoLocal;
    }

    public String getUrlMiniVista01() {
        return urlMiniVista01;
    }

    public void setUrlMiniVista01(String urlMiniVista01) {
        this.urlMiniVista01 = urlMiniVista01;
    }

    public String getUrlMiniVista02() {
        return urlMiniVista02;
    }

    public void setUrlMiniVista02(String urlMiniVista02) {
        this.urlMiniVista02 = urlMiniVista02;
    }

    public String getUrlMiniVista03() {
        return urlMiniVista03;
    }

    public void setUrlMiniVista03(String urlMiniVista03) {
        this.urlMiniVista03 = urlMiniVista03;
    }

    public String getUrlVistaSeleccionada() {
        return urlVistaSeleccionada;
    }

    public void setUrlVistaSeleccionada(String urlVistaSeleccionada) {
        this.urlVistaSeleccionada = urlVistaSeleccionada;
    }

    public String getUrlVideoOriginalCortado() {
        return urlVideoOriginalCortado;
    }

    public void setUrlVideoOriginalCortado(String urlVideoOriginalCortado) {
        this.urlVideoOriginalCortado = urlVideoOriginalCortado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
