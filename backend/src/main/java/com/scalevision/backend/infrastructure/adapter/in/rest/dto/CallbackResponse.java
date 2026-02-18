package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import java.util.UUID;

public record CallbackResponse(
        UUID jobId,
        String status
) {
}
