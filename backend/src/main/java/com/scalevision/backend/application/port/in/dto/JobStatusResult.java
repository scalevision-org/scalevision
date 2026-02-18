package com.scalevision.backend.application.port.in.dto;

import com.scalevision.backend.domain.model.JobStatus;

import java.util.UUID;

/**
 * Current state of a processing job.
 */
public record JobStatusResult(
        UUID jobId,
        JobStatus status,
        String outputUrl,
        String aiResultPayload,
        String errorMessage
) {
}
