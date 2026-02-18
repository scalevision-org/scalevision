package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import com.scalevision.backend.application.port.in.dto.ProcessVideoCommand;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProcessVideoRequest(
        @NotBlank(message = "videoUrl is required")
        @Pattern(regexp = "^https?://.*", message = "videoUrl must start with http:// or https://")
        @JsonAlias("video_url")
        String videoUrl,
        @JsonAlias("target_aspect_ratio")
        String targetAspectRatio,
        @JsonAlias("target_duration")
        Integer targetDuration,
        @JsonAlias("focus_area")
        String focusArea,
        @JsonAlias("callback_url")
        String callbackUrl,
        @JsonAlias("webhook_secret")
        String webhookSecret,
        @JsonAlias("tracking_mode")
        String trackingMode,
        @JsonAlias("target_selection")
        TargetSelection targetSelection,
        Config config
) {

    public record TargetSelection(
            @JsonAlias("reference_timestamp")
            Double referenceTimestamp,
            @JsonAlias("reference_box")
            Double[] referenceBox
    ) {
    }

    public record Config(
            @JsonAlias("target_aspect_ratio")
            String targetAspectRatio,
            @JsonAlias("fps_sampled")
            Integer fpsSampled,
            @JsonAlias("include_trajectory_data")
            Boolean includeTrajectoryData
    ) {
    }

    public ProcessVideoCommand toCommand() {
        String aspectRatio = targetAspectRatio;
        Integer fpsSampled = null;
        Boolean includeTrajectoryData = null;

        if (config != null) {
            if (aspectRatio == null || aspectRatio.isBlank()) {
                aspectRatio = config.targetAspectRatio();
            }
            fpsSampled = config.fpsSampled();
            includeTrajectoryData = config.includeTrajectoryData();
        }

        Double referenceTimestamp = null;
        Double[] referenceBox = null;
        if (targetSelection != null) {
            referenceTimestamp = targetSelection.referenceTimestamp();
            referenceBox = targetSelection.referenceBox();
        }

        return new ProcessVideoCommand(
                videoUrl,
                aspectRatio,
                targetDuration,
                focusArea,
                callbackUrl,
                webhookSecret,
                trackingMode,
                referenceTimestamp,
                referenceBox,
                fpsSampled,
                includeTrajectoryData
        );
    }
}
