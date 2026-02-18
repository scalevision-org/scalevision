package com.scalevision.backend.application.port.in.dto;

/**
 * Data required to create a new video processing job.
 */
public record ProcessVideoCommand(
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
