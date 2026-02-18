package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobMapperTest {

    private final JobMapper mapper = new JobMapper();

    @Test
    void shouldMapDomainToEntity() {
        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
        job.setAiTaskId("ai-task-1");
        job.updateStatus(JobStatus.PROCESSING);

        JobEntity entity = mapper.toEntity(job);

        assertEquals(job.getId(), entity.getId());
        assertEquals(job.getVideoUrl(), entity.getVideoUrl());
        assertEquals(JobStatus.PROCESSING, entity.getStatus());
        assertEquals("ai-task-1", entity.getAiTaskId());
    }

    @Test
    void shouldMapEntityToDomain() {
        JobEntity entity = new JobEntity();
        entity.setId(UUID.randomUUID());
        entity.setVideoUrl("https://cdn.test/video.mp4");
        entity.setTargetAspectRatio("9:16");
        entity.setTargetDuration(30);
        entity.setFocusArea("center");
        entity.setStatus(JobStatus.FAILED);
        entity.setErrorMessage("MODEL_TIMEOUT");

        ProcessingJob job = mapper.toDomain(entity);

        assertEquals(entity.getId(), job.getId());
        assertEquals(entity.getVideoUrl(), job.getVideoUrl());
        assertEquals(JobStatus.FAILED, job.getStatus());
        assertEquals("MODEL_TIMEOUT", job.getErrorMessage());
    }
}
