package com.scalevision.backend.application.port.out.dto;

import java.util.List;

public record AIScanSubjectsResponse(
        String status,
        List<AISubjectCandidate> subjects
) {
}
