package com.scalevision.backend.application.port.in;

import com.scalevision.backend.application.port.in.dto.ProcessVideoCommand;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;

/**
 * Starts the processing flow for a source video.
 */
public interface ProcessVideoUseCase {

    /**
     * Creates a new processing job and requests AI execution.
     *
     * @param command request data sent by the client
     * @return created job information
     */
    ProcessVideoResult processVideo(ProcessVideoCommand command);
}
