package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.domain.model.ProcessingJob;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobEntity toEntity(ProcessingJob job) {
        JobEntity entity = new JobEntity();
        entity.setId(job.getId());
        entity.setVideoUrl(job.getVideoUrl());
        entity.setTargetAspectRatio(job.getTargetAspectRatio());
        entity.setTargetDuration(job.getTargetDuration());
        entity.setFocusArea(job.getFocusArea());
        entity.setStatus(job.getStatus());
        entity.setAiTaskId(job.getAiTaskId());
<<<<<<< HEAD
        entity.setOutputUrl(job.getOutputUrl());
=======
        entity.setWebhookSecret(job.getWebhookSecret());
        entity.setOutputUrl(job.getOutputUrl());
        entity.setAiResultPayload(job.getAiResultPayload());
>>>>>>> origin/feature/backend-contract-alignment-v2
        entity.setErrorMessage(job.getErrorMessage());
        entity.setCreatedAt(job.getCreatedAt());
        entity.setUpdatedAt(job.getUpdatedAt());
        return entity;
    }

    public ProcessingJob toDomain(JobEntity entity) {
        ProcessingJob job = new ProcessingJob(
                entity.getId(),
                entity.getVideoUrl(),
                entity.getTargetAspectRatio(),
                entity.getTargetDuration(),
                entity.getFocusArea()
        );

        job.applyPersistenceState(
                entity.getStatus(),
                entity.getAiTaskId(),
<<<<<<< HEAD
                entity.getOutputUrl(),
=======
                entity.getWebhookSecret(),
                entity.getOutputUrl(),
                entity.getAiResultPayload(),
>>>>>>> origin/feature/backend-contract-alignment-v2
                entity.getErrorMessage()
        );

        return job;
    }
}
