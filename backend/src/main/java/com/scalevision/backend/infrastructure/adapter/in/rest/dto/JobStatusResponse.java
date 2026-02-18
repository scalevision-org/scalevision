package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import java.util.UUID;

public record JobStatusResponse(
        UUID jobId,
        String status,
        Integer progressPercentage,
        ResultData result,
        ErrorData error
) {
    public record ResultData(
            String outputVideoUrl,
            String aiResultPayload
    ) {
    }

    public record ErrorData(
            String code,
            String message,
            Boolean retryable
    ) {
    }
}
