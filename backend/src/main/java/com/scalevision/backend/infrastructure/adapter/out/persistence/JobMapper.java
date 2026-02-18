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
        entity.setWebhookSecret(job.getWebhookSecret());
        entity.setOutputUrl(job.getOutputUrl());
        entity.setAiResultPayload(job.getAiResultPayload());
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
                entity.getWebhookSecret(),
                entity.getOutputUrl(),
                entity.getAiResultPayload(),
                entity.getErrorMessage()
        );

        return job;
    }
}
