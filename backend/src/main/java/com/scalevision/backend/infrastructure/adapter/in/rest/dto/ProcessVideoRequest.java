package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import com.scalevision.backend.application.port.in.dto.ProcessVideoCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProcessVideoRequest(
        @NotBlank(message = "videoUrl is required")
        @Pattern(regexp = "^https?://.*", message = "videoUrl must start with http:// or https://")
        String videoUrl,
        String targetAspectRatio,
        Integer targetDuration,
        String focusArea
) {
    public ProcessVideoCommand toCommand() {
        return new ProcessVideoCommand(videoUrl, targetAspectRatio, targetDuration, focusArea);
    }
}
