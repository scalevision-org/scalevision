package com.scalevision.backend.application.port.in;

import com.scalevision.backend.application.port.in.dto.JobStatusResult;

import java.util.Optional;
import java.util.UUID;

/**
 * Retrieves the latest state of a processing job.
 */
public interface GetJobStatusUseCase {

    /**
     * Finds a job by id.
     *
     * @param jobId backend job id
     * @return current job status if found
     */
    Optional<JobStatusResult> getJobStatus(UUID jobId);
}
