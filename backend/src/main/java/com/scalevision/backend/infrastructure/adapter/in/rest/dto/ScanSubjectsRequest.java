package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ScanSubjectsRequest(
        @NotBlank(message = "videoUrl is required")
        @Pattern(regexp = "^https?://.*", message = "videoUrl must start with http:// or https://")
        @JsonAlias("video_url")
        String videoUrl,
        @JsonAlias("min_appearance_ratio")
        Double minAppearanceRatio,
        Config config
) {
    public record Config(
            @JsonAlias("max_subjects")
            Integer maxSubjects
    ) {
    }

    public AIScanSubjectsRequest toPortRequest() {
        Integer maxSubjects = 3;
        if (config != null && config.maxSubjects() != null) {
            maxSubjects = config.maxSubjects();
        }
        Double ratio = minAppearanceRatio != null ? minAppearanceRatio : 0.1;
        return new AIScanSubjectsRequest(videoUrl, ratio, maxSubjects);
    }
}
