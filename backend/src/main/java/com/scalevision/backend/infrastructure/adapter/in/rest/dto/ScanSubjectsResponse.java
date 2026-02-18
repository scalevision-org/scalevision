package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record ScanSubjectsResponse(
        String status,
        List<SubjectData> subjects
) {
    public record SubjectData(
            String tempId,
            String thumbnailBase64,
            Double appearanceRatio,
            String subjectType,
            ReferenceData referenceData
    ) {
    }

    public record ReferenceData(
            Double timestamp,
            Double[] box
    ) {
    }
}
