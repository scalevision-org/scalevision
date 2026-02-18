package com.scalevision.backend.application.port.out.dto;

public record AISubjectCandidate(
        String tempId,
        String thumbnailBase64,
        Double appearanceRatio,
        String subjectType,
        Double referenceTimestamp,
        Double[] referenceBox
) {
}
