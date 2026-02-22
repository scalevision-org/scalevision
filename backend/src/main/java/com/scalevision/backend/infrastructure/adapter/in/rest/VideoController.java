package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.in.ProcessVideoUseCase;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ProcessVideoRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ProcessVideoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Video Processing", description = "Endpoints para iniciar el procesamiento de video con IA")
public class VideoController {

    private final ProcessVideoUseCase processVideoUseCase;

    public VideoController(ProcessVideoUseCase processVideoUseCase) {
        this.processVideoUseCase = processVideoUseCase;
    }

    @Operation(
            summary = "Iniciar procesamiento de video",
            description = "Envía un video a la IA para su procesamiento. Retorna un jobId para hacer polling del estado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Job creado y en procesamiento",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "jobId": "550e8400-e29b-41d4-a716-446655440000",
                          "status": "PROCESSING"
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "Request inválido",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "code": "BAD_REQUEST",
                          "message": "video_url must not be blank",
                          "timestamp": "2026-02-21T12:00:00"
                        }
                        """))),
            @ApiResponse(responseCode = "422", description = "Error al enviar video a la IA",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "code": "VIDEO_PROCESSING_ERROR",
                          "message": "AI service unavailable",
                          "timestamp": "2026-02-21T12:00:00"
                        }
                        """)))
    })
    @PostMapping({"/videos/process", "/svmvp/videos/process"})
    public ResponseEntity<ProcessVideoResponse> processVideo(@Valid @RequestBody ProcessVideoRequest request) {
        ProcessVideoResult result = processVideoUseCase.processVideo(request.toCommand());
        ProcessVideoResponse response = new ProcessVideoResponse(result.jobId(), result.status().name());
        return ResponseEntity.accepted().body(response);
    }
}
