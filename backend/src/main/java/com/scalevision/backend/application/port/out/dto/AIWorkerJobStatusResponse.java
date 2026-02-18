package com.scalevision.backend.application.port.out.dto;

public record AIWorkerJobStatusResponse(
        String jobId,
        String status,
        Integer progressPercentage,
        String rawResult
) {
}
