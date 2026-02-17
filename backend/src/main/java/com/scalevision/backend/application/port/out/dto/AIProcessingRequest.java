package com.scalevision.backend.application.port.out.dto;

/**
 * Payload sent from backend to AI service.
 */
public record AIProcessingRequest(
        String jobId,
        String videoUrl,
        String targetAspectRatio,
        Integer targetDuration,
        String focusArea
) {
}
