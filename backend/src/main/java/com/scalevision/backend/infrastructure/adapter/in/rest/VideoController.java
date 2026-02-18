package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.in.ProcessVideoUseCase;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ProcessVideoRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ProcessVideoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {

    private final ProcessVideoUseCase processVideoUseCase;

    public VideoController(ProcessVideoUseCase processVideoUseCase) {
        this.processVideoUseCase = processVideoUseCase;
    }

    @PostMapping({"/videos/process", "/svmvp/videos/process"})
    public ResponseEntity<ProcessVideoResponse> processVideo(@Valid @RequestBody ProcessVideoRequest request) {
        ProcessVideoResult result = processVideoUseCase.processVideo(request.toCommand());
        ProcessVideoResponse response = new ProcessVideoResponse(result.jobId(), result.status().name());
        return ResponseEntity.accepted().body(response);
    }
}
