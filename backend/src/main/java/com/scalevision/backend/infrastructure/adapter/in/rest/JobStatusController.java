package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.in.GetJobStatusUseCase;
import com.scalevision.backend.application.port.in.dto.JobStatusResult;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.JobStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class JobStatusController {

    private final GetJobStatusUseCase getJobStatusUseCase;

    public JobStatusController(GetJobStatusUseCase getJobStatusUseCase) {
        this.getJobStatusUseCase = getJobStatusUseCase;
    }

    @GetMapping({"/jobs/{jobId}", "/svmvp/jobs/{jobId}"})
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable UUID jobId) {
        JobStatusResult result = getJobStatusUseCase.getJobStatus(jobId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "job not found"));

        Integer progressPercentage = resolveProgress(result.status());
        JobStatusResponse.ResultData responseResult = null;
        JobStatusResponse.ErrorData responseError = null;

        if (result.status() == JobStatus.COMPLETED) {
            responseResult = new JobStatusResponse.ResultData(result.outputUrl(), result.aiResultPayload());
        }

        if (result.status() == JobStatus.FAILED) {
            String errorCode = "PROCESSING_ERROR";
            if (result.errorMessage() != null && result.errorMessage().matches("^[A-Z0-9_]+$")) {
                errorCode = result.errorMessage();
            }

            responseError = new JobStatusResponse.ErrorData(
                    errorCode,
                    result.errorMessage(),
                    false
            );
        }

        JobStatusResponse response = new JobStatusResponse(
                result.jobId(),
                result.status().name(),
                progressPercentage,
                responseResult,
                responseError
        );

        return ResponseEntity.ok(response);
    }

    private Integer resolveProgress(JobStatus status) {
        if (status == JobStatus.PENDING) {
            return 0;
        }

        if (status == JobStatus.PROCESSING) {
            return 50;
        }

        return 100;
    }
}
