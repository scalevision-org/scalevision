package com.scalevision.backend.application.port.in.dto;

/**
 * Data required to create a new video processing job.
 */
public record ProcessVideoCommand(
        String videoUrl,
        String targetAspectRatio,
        Integer targetDuration,
        String focusArea
) {
}
