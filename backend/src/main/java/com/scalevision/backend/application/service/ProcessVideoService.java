package com.scalevision.backend.application.service;

import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.in.ProcessVideoUseCase;
import com.scalevision.backend.application.port.in.dto.ProcessVideoCommand;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;
import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProcessVideoService implements ProcessVideoUseCase {

    private final JobRepository jobRepository;
    private final AIServicePort aiServicePort;
<<<<<<< HEAD
=======
    private static final String DEFAULT_CALLBACK_URL = "http://localhost:8080/svmvp/callbacks/ai";
>>>>>>> origin/feature/backend-contract-alignment-v2

    public ProcessVideoService(JobRepository jobRepository, AIServicePort aiServicePort) {
        this.jobRepository = jobRepository;
        this.aiServicePort = aiServicePort;
    }

    @Override
    public ProcessVideoResult processVideo(ProcessVideoCommand command) {
        validateCommand(command);

        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                command.videoUrl(),
                command.targetAspectRatio(),
                command.targetDuration(),
                command.focusArea()
        );
<<<<<<< HEAD
=======
        String webhookSecret = resolveWebhookSecret(command.webhookSecret());
        job.setWebhookSecret(webhookSecret);
>>>>>>> origin/feature/backend-contract-alignment-v2

        jobRepository.save(job);

        try {
            AIProcessingRequest request = new AIProcessingRequest(
                    job.getId().toString(),
                    job.getVideoUrl(),
                    job.getTargetAspectRatio(),
                    job.getTargetDuration(),
<<<<<<< HEAD
                    job.getFocusArea()
=======
                    job.getFocusArea(),
                    resolveCallbackUrl(command.callbackUrl()),
                    webhookSecret,
                    command.trackingMode(),
                    command.referenceTimestamp(),
                    command.referenceBox(),
                    command.fpsSampled(),
                    command.includeTrajectoryData()
>>>>>>> origin/feature/backend-contract-alignment-v2
            );

            AIProcessingResponse response = aiServicePort.processVideo(request);
            job.setAiTaskId(response.aiTaskId());
            job.updateStatus(JobStatus.PROCESSING);
            jobRepository.save(job);

            return new ProcessVideoResult(job.getId(), job.getStatus());
        } catch (Exception ex) {
            job.setErrorMessage(ex.getMessage());
            if (job.canTransitionTo(JobStatus.FAILED)) {
                job.updateStatus(JobStatus.FAILED);
            }
            jobRepository.save(job);
            throw new VideoProcessingException("Error processing video", ex);
        }
    }

    private void validateCommand(ProcessVideoCommand command) {
        if (command == null) {
            throw new VideoProcessingException("command is required");
        }

        String videoUrl = command.videoUrl();
        if (videoUrl == null || videoUrl.isBlank()) {
            throw new VideoProcessingException("videoUrl is required");
        }

        if (!videoUrl.startsWith("http://") && !videoUrl.startsWith("https://")) {
            throw new VideoProcessingException("videoUrl is invalid");
        }
    }
<<<<<<< HEAD
=======

    private String resolveWebhookSecret(String rawSecret) {
        if (rawSecret != null && !rawSecret.isBlank()) {
            return rawSecret;
        }
        return "sv_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String resolveCallbackUrl(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return DEFAULT_CALLBACK_URL;
        }
        return callbackUrl;
    }
>>>>>>> origin/feature/backend-contract-alignment-v2
}
