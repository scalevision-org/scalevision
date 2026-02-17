package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.exception.InvalidStatusTransitionException;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.AICallbackRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.CallbackResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class CallbackController {

    private final JobRepository jobRepository;

    public CallbackController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @PostMapping("/callbacks/ai")
    public ResponseEntity<CallbackResponse> handleAiCallback(@Valid @RequestBody AICallbackRequest request) {
        UUID jobId = parseJobId(request.jobId());

        ProcessingJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "job not found"));

        JobStatus nextStatus = mapStatus(request.status());

        try {
            job.updateStatus(nextStatus);
        } catch (InvalidStatusTransitionException ex) {
            throw new ResponseStatusException(CONFLICT, ex.getMessage());
        }

        if (nextStatus == JobStatus.COMPLETED) {
            job.setOutputUrl(request.outputUrl());
        }

        if (nextStatus == JobStatus.FAILED) {
            job.setErrorMessage(request.errorMessage());
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
}
