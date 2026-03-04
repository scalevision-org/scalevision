package com.scalevision.backend.entity;

import com.scalevision.backend.exception.BadRequestException;

public enum ModoCorte {
    FACE_TRACKING("face_tracking"),
    CENTER_CROP("center_crop");

    private final String value;

    ModoCorte(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ModoCorte fromValue(String value) {
        if (value == null) {
            throw new BadRequestException("Debe enviar el campo modo_corte");
        }

        for (ModoCorte mode : ModoCorte.values()) {
            if (mode.value.equalsIgnoreCase(value.trim())) {
                return mode;
            }
        }

        throw new BadRequestException("modo_corte invalido. Use: face_tracking o center_crop");
    }
}
