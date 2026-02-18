package com.scalevision.backend.application.service;

import com.scalevision.backend.application.port.in.GetJobStatusUseCase;
import com.scalevision.backend.application.port.in.dto.JobStatusResult;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetJobStatusService implements GetJobStatusUseCase {

    private final JobRepository jobRepository;

    public GetJobStatusService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Optional<JobStatusResult> getJobStatus(UUID jobId) {
        Optional<ProcessingJob> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return Optional.empty();
        }

        ProcessingJob job = jobOpt.get();

        return Optional.of(new JobStatusResult(
                job.getId(),
                job.getStatus(),
                job.getOutputUrl(),
                job.getAiResultPayload(),
                job.getErrorMessage()
        ));
    }
}
