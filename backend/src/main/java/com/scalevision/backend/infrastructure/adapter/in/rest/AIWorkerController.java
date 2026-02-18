package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIWorkerController {

    private final AIServicePort aiServicePort;

    public AIWorkerController(AIServicePort aiServicePort) {
        this.aiServicePort = aiServicePort;
    }

    @GetMapping({"/ai/job/{jobId}", "/svmvp/ai/job/{jobId}"})
    public ResponseEntity<AIWorkerJobStatusResponse> getWorkerJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(aiServicePort.getJobStatus(jobId));
    }

    @GetMapping({"/ai/health", "/svmvp/ai/health"})
    public ResponseEntity<AIWorkerHealthResponse> getWorkerHealth() {
        return ResponseEntity.ok(aiServicePort.getHealth());
    }
}
