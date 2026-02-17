package com.scalevision.backend.application.port.out;

import com.scalevision.backend.domain.model.JobStatus;

import java.util.UUID;

/**
 * Notification contract for job updates.
 */
public interface NotificationPort {

    /**
     * Sends status updates to external clients.
     *
     * @param jobId backend job id
     * @param status current job status
     * @param message optional message
     */
    void notifyJobStatus(UUID jobId, JobStatus status, String message);
}
