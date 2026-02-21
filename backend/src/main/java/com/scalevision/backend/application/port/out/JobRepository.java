package com.scalevision.backend.application.port.out;

import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;

import java.util.List;
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

    /**
     * Retorna todos los jobs.
     *
     * @return lista de todos los jobs
     */
    List<ProcessingJob> findAll();

    /**
     * Busca un job por el id de tarea de la IA.
     *
     * @param aiTaskId id de tarea de la IA
     * @return job si existe
     */
    Optional<ProcessingJob> findByAiTaskId(String aiTaskId);

    /**
     * Busca todos los jobs por estado.
     *
     * @param status estado del job
     * @return lista de jobs con el estado dado
     */
    List<ProcessingJob> findByStatus(JobStatus status);
}
