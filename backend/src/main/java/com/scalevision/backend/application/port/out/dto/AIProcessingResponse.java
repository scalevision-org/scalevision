package com.scalevision.backend.application.port.out.dto;

/**
 * Response returned by AI service after accepting a job.
 */
public record AIProcessingResponse(
        String aiTaskId,
<<<<<<< HEAD
        String status
=======
        String status,
        Integer estimatedProcessingTimeSec
>>>>>>> origin/feature/backend-contract-alignment-v2
) {
}
