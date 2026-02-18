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
        entity.setOutputUrl(job.getOutputUrl());
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
                entity.getOutputUrl(),
                entity.getErrorMessage()
        );

        return job;
    }
}
