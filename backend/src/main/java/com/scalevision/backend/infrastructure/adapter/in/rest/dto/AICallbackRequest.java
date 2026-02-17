package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AICallbackRequest(
        @NotBlank(message = "jobId is required")
        @JsonAlias("job_id")
        String jobId,

        @NotBlank(message = "status is required")
        String status,

        @JsonAlias("output_url")
        String outputUrl,

        @JsonAlias("error_message")
        String errorMessage
) {
}
