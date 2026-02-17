package com.scalevision.backend.application.port.in.dto;

import com.scalevision.backend.domain.model.JobStatus;

import java.util.UUID;

/**
 * Result returned after requesting a new job.
 */
public record ProcessVideoResult(
        UUID jobId,
        JobStatus status
) {
}
