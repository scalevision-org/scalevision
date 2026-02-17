package com.scalevision.backend.application.port.out;

import com.scalevision.backend.domain.model.ProcessingJob;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for processing jobs.
 */
public interface JobRepository {

    /**
     * Stores or updates a job.
     *
     * @param job domain job
     * @return saved job
     */
    ProcessingJob save(ProcessingJob job);

    /**
     * Finds a job by backend id.
     *
     * @param id backend job id
     * @return job if it exists
     */
    Optional<ProcessingJob> findById(UUID id);
}
