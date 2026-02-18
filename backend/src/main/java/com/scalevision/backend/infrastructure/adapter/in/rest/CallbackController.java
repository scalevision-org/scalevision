package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.exception.InvalidStatusTransitionException;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.AICallbackRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.CallbackResponse;
<<<<<<< HEAD
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
=======
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
>>>>>>> origin/feature/backend-contract-alignment-v2
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class CallbackController {

    private final JobRepository jobRepository;
<<<<<<< HEAD

    public CallbackController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @PostMapping("/callbacks/ai")
    public ResponseEntity<CallbackResponse> handleAiCallback(@Valid @RequestBody AICallbackRequest request) {
=======
    private final String expectedSchemaVersion;
    private final ObjectMapper objectMapper;

    public CallbackController(
            JobRepository jobRepository,
            @Value("${ai.callback.schema-version:1.0.0}") String expectedSchemaVersion
    ) {
        this.jobRepository = jobRepository;
        this.expectedSchemaVersion = expectedSchemaVersion;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping({"/callbacks/ai", "/svmvp/callbacks/ai"})
    public ResponseEntity<CallbackResponse> handleAiCallback(
            @Valid @RequestBody AICallbackRequest request,
            @RequestHeader(name = "X-AI-Schema-Version", required = false) String schemaVersion,
            @RequestHeader(name = "X-Webhook-Secret", required = false) String webhookSecret
    ) {
>>>>>>> origin/feature/backend-contract-alignment-v2
        UUID jobId = parseJobId(request.jobId());

        ProcessingJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "job not found"));

<<<<<<< HEAD
=======
        if (job.getWebhookSecret() != null && !job.getWebhookSecret().isBlank()) {
            if (webhookSecret == null || !job.getWebhookSecret().equals(webhookSecret)) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "invalid webhook secret");
            }
        }

        if (schemaVersion != null && !expectedSchemaVersion.equals(schemaVersion)) {
            throw new ResponseStatusException(BAD_REQUEST, "schema version is invalid");
        }

>>>>>>> origin/feature/backend-contract-alignment-v2
        JobStatus nextStatus = mapStatus(request.status());

        try {
            job.updateStatus(nextStatus);
        } catch (InvalidStatusTransitionException ex) {
            throw new ResponseStatusException(CONFLICT, ex.getMessage());
        }

        if (nextStatus == JobStatus.COMPLETED) {
            job.setOutputUrl(request.outputUrl());
<<<<<<< HEAD
=======
            job.setAiResultPayload(buildSuccessResultJson(request));
>>>>>>> origin/feature/backend-contract-alignment-v2
        }

        if (nextStatus == JobStatus.FAILED) {
            job.setErrorMessage(request.errorMessage());
<<<<<<< HEAD
=======
            job.setAiResultPayload(buildFailureResultJson(request));
>>>>>>> origin/feature/backend-contract-alignment-v2
        }

        jobRepository.save(job);

        return ResponseEntity.ok(new CallbackResponse(job.getId(), job.getStatus().name()));
    }

    private UUID parseJobId(String rawJobId) {
        try {
            return UUID.fromString(rawJobId);
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, "jobId is invalid");
        }
    }

    private JobStatus mapStatus(String rawStatus) {
        String normalized = rawStatus.trim().toUpperCase();

        if ("COMPLETED".equals(normalized)) {
            return JobStatus.COMPLETED;
        }

        if ("FAILED".equals(normalized)) {
            return JobStatus.FAILED;
        }

        throw new ResponseStatusException(BAD_REQUEST, "status is invalid");
    }
<<<<<<< HEAD
=======

    private String buildSuccessResultJson(AICallbackRequest request) {
        try {
            return objectMapper.writeValueAsString(new SuccessPayload(
                    request.processingStats(),
                    request.cropRecommendations()
            ));
        } catch (Exception ex) {
            return "{\"status\":\"completed\"}";
        }
    }

    private String buildFailureResultJson(AICallbackRequest request) {
        try {
            return objectMapper.writeValueAsString(new FailedPayload(
                    request.errorMessage(),
                    request.retryable()
            ));
        } catch (Exception ex) {
            return "{\"status\":\"failed\"}";
        }
    }

    private record SuccessPayload(Object processingStats, Object cropRecommendations) {
    }

    private record FailedPayload(String errorCode, Boolean retryable) {
    }
>>>>>>> origin/feature/backend-contract-alignment-v2
}
