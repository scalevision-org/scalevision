package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import java.util.UUID;

public record ProcessVideoResponse(
        UUID jobId,
        String status
) {
}
