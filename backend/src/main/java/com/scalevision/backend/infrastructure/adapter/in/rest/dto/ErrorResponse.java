package com.scalevision.backend.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
}
