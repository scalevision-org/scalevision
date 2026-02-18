package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.domain.model.JobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "processing_job",
        indexes = {
                @Index(name = "idx_processing_job_status", columnList = "status"),
                @Index(name = "idx_processing_job_created_at", columnList = "created_at")
        }
)
public class JobEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "video_url", nullable = false, length = 1024)
    private String videoUrl;

    @Column(name = "target_aspect_ratio", length = 10)
    private String targetAspectRatio;

    @Column(name = "target_duration")
    private Integer targetDuration;

    @Column(name = "focus_area", length = 50)
    private String focusArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "ai_task_id", length = 255)
    private String aiTaskId;

    @Column(name = "webhook_secret", length = 255)
    private String webhookSecret;

    @Column(name = "output_url", length = 500)
    private String outputUrl;

    @Column(name = "ai_result_payload", length = 8000)
    private String aiResultPayload;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected JobEntity() {
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public void setTargetAspectRatio(String targetAspectRatio) {
        this.targetAspectRatio = targetAspectRatio;
    }

    public Integer getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Integer targetDuration) {
        this.targetDuration = targetDuration;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getAiTaskId() {
        return aiTaskId;
    }

    public void setAiTaskId(String aiTaskId) {
        this.aiTaskId = aiTaskId;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public String getAiResultPayload() {
        return aiResultPayload;
    }

    public void setAiResultPayload(String aiResultPayload) {
        this.aiResultPayload = aiResultPayload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
