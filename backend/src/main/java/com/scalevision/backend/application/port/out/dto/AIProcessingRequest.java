package com.scalevision.backend.application.port.out.dto;

/**
 * Payload sent from backend to AI service.
 */
public record AIProcessingRequest(
        String jobId,
        String videoUrl,
        String targetAspectRatio,
        Integer targetDuration,
<<<<<<< HEAD
        String focusArea
=======
        String focusArea,
        String callbackUrl,
        String webhookSecret,
        String trackingMode,
        Double referenceTimestamp,
        Double[] referenceBox,
        Integer fpsSampled,
        Boolean includeTrajectoryData
>>>>>>> origin/feature/backend-contract-alignment-v2
) {
}
