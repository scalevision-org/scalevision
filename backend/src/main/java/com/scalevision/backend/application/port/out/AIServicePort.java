package com.scalevision.backend.application.port.out;

import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
<<<<<<< HEAD
=======
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsRequest;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
>>>>>>> origin/feature/backend-contract-alignment-v2

/**
 * Backend contract to communicate with AI service.
 */
public interface AIServicePort {

    /**
     * Sends a processing request to AI service.
     *
     * @param request processing payload
     * @return AI acknowledgment data
     */
    AIProcessingResponse processVideo(AIProcessingRequest request);
<<<<<<< HEAD
=======

    AIScanSubjectsResponse scanSubjects(AIScanSubjectsRequest request);

    AIWorkerJobStatusResponse getJobStatus(String jobId);

    AIWorkerHealthResponse getHealth();
>>>>>>> origin/feature/backend-contract-alignment-v2
}
