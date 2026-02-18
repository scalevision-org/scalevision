package com.scalevision.backend.domain.model;

import com.scalevision.backend.domain.exception.InvalidStatusTransitionException;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessingJob {

    private final UUID id;
    private final String videoUrl;
    private final String targetAspectRatio;
    private final Integer targetDuration;
    private final String focusArea;
    private JobStatus status;
    private String aiTaskId;
    private String webhookSecret;
    private String outputUrl;
    private String aiResultPayload;
    private String errorMessage;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProcessingJob(
            UUID id,
            String videoUrl,
            String targetAspectRatio,
            Integer targetDuration,
            String focusArea
    ) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (videoUrl == null || videoUrl.isBlank()) {
            throw new IllegalArgumentException("videoUrl is required");
        }

        this.id = id;
        this.videoUrl = videoUrl;
        this.targetAspectRatio = targetAspectRatio;
        this.targetDuration = targetDuration;
        this.focusArea = focusArea;
        this.status = JobStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public boolean canTransitionTo(JobStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        if (this.status == JobStatus.PENDING) {
            return nextStatus == JobStatus.PROCESSING || nextStatus == JobStatus.FAILED;
        }
        if (this.status == JobStatus.PROCESSING) {
            return nextStatus == JobStatus.COMPLETED || nextStatus == JobStatus.FAILED;
        }
        return false;
    }

    public void updateStatus(JobStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            throw new InvalidStatusTransitionException(this.status, nextStatus);
        }
        this.status = nextStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public Integer getTargetDuration() {
        return targetDuration;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getAiTaskId() {
        return aiTaskId;
    }

    public void setAiTaskId(String aiTaskId) {
        this.aiTaskId = aiTaskId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAiResultPayload() {
        return aiResultPayload;
    }

    public void setAiResultPayload(String aiResultPayload) {
        this.aiResultPayload = aiResultPayload;
        this.updatedAt = LocalDateTime.now();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public void applyPersistenceState(
            JobStatus persistedStatus,
            String persistedAiTaskId,
            String persistedWebhookSecret,
            String persistedOutputUrl,
            String persistedAiResultPayload,
            String persistedErrorMessage
    ) {
        if (persistedStatus != null && persistedStatus != JobStatus.PENDING) {
            if (persistedStatus == JobStatus.PROCESSING) {
                this.updateStatus(JobStatus.PROCESSING);
            } else if (persistedStatus == JobStatus.FAILED) {
                this.updateStatus(JobStatus.FAILED);
            } else if (persistedStatus == JobStatus.COMPLETED) {
                this.updateStatus(JobStatus.PROCESSING);
                this.updateStatus(JobStatus.COMPLETED);
            }
        }

        this.aiTaskId = persistedAiTaskId;
        this.webhookSecret = persistedWebhookSecret;
        this.outputUrl = persistedOutputUrl;
        this.aiResultPayload = persistedAiResultPayload;
        this.errorMessage = persistedErrorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
