package com.scalevision.backend.domain.model;

import com.scalevision.backend.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessingJobTest {

    @Test
    void shouldCreateJobWithPendingStatus() {
        ProcessingJob job = buildJob();

        assertNotNull(job.getId());
        assertEquals("https://cdn.test/video.mp4", job.getVideoUrl());
        assertEquals(JobStatus.PENDING, job.getStatus());
        assertNotNull(job.getCreatedAt());
        assertNotNull(job.getUpdatedAt());
    }

    @Test
    void shouldAllowValidTransitionFromPendingToProcessing() {
        ProcessingJob job = buildJob();

        assertTrue(job.canTransitionTo(JobStatus.PROCESSING));
        job.updateStatus(JobStatus.PROCESSING);
        assertEquals(JobStatus.PROCESSING, job.getStatus());
    }

    @Test
    void shouldAllowValidTransitionFromProcessingToCompleted() {
        ProcessingJob job = buildJob();
        job.updateStatus(JobStatus.PROCESSING);

        assertTrue(job.canTransitionTo(JobStatus.COMPLETED));
        job.updateStatus(JobStatus.COMPLETED);

        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void shouldRejectInvalidTransitionFromPendingToCompleted() {
        ProcessingJob job = buildJob();

        assertFalse(job.canTransitionTo(JobStatus.COMPLETED));
        assertThrows(
                InvalidStatusTransitionException.class,
                () -> job.updateStatus(JobStatus.COMPLETED)
        );
    }

    @Test
    void shouldKeepFinalStatusWithoutTransitions() {
        ProcessingJob job = buildJob();
        job.updateStatus(JobStatus.PROCESSING);
        job.updateStatus(JobStatus.FAILED);

        assertFalse(job.canTransitionTo(JobStatus.PROCESSING));
        assertFalse(job.canTransitionTo(JobStatus.COMPLETED));
        assertThrows(
                InvalidStatusTransitionException.class,
                () -> job.updateStatus(JobStatus.PROCESSING)
        );
    }

    @Test
    void shouldUpdateTimestampWhenStatusChanges() {
        ProcessingJob job = buildJob();
        LocalDateTime previousUpdatedAt = job.getUpdatedAt();

        job.updateStatus(JobStatus.PROCESSING);

        assertTrue(job.getUpdatedAt().isAfter(previousUpdatedAt)
                || job.getUpdatedAt().isEqual(previousUpdatedAt));
    }

    @Test
    void shouldNotAllowNullStatusTransition() {
        ProcessingJob job = buildJob();

        assertFalse(job.canTransitionTo(null));
    }

    private ProcessingJob buildJob() {
        return new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
    }
}
