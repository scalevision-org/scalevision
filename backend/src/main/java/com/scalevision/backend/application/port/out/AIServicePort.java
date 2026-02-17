package com.scalevision.backend.application.port.out;

import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;

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
}
