package com.scalevision.backend.application.port.out.dto;

/**
 * Response returned by AI service after accepting a job.
 */
public record AIProcessingResponse(
        String aiTaskId,
        String status
) {
}
