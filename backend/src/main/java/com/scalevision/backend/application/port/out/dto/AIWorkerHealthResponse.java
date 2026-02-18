package com.scalevision.backend.application.port.out.dto;

public record AIWorkerHealthResponse(
        String status,
        Integer activeJobs,
        Boolean capacityAvailable
) {
}
