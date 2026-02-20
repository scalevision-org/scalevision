package com.scalevision.backend.application.service;

import com.scalevision.backend.application.port.in.GetJobStatusUseCase;
import com.scalevision.backend.application.port.in.dto.JobStatusResult;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetJobStatusService implements GetJobStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetJobStatusService.class);
    private final JobRepository jobRepository;

    public GetJobStatusService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Optional<JobStatusResult> getJobStatus(UUID jobId) {
        log.debug("Consultando estado del job: {}", jobId);
        Optional<ProcessingJob> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            log.warn("job no encontrado {}", jobId);
            return Optional.empty();
        }

        ProcessingJob job = jobOpt.get();
        log.debug("job {} encontrado con estado: {}", jobId, job.getStatus());

        return Optional.of(new JobStatusResult(
                job.getId(),
                job.getStatus(),
                job.getOutputUrl(),
                job.getAiResultPayload(),
                job.getErrorMessage()
        ));
    }
}
