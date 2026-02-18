package com.scalevision.backend.application.port.out.dto;

public record AIScanSubjectsRequest(
        String videoUrl,
        Double minAppearanceRatio,
        Integer maxSubjects
) {
}
