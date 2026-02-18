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
<<<<<<< HEAD
            String outputVideoUrl
=======
            String outputVideoUrl,
            String aiResultPayload
>>>>>>> origin/feature/backend-contract-alignment-v2
    ) {
    }

    public record ErrorData(
            String code,
            String message,
            Boolean retryable
    ) {
    }
}
