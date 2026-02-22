package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI Worker", description = "Endpoints para consultar estado y salud del worker de IA")
public class AIWorkerController {

    private final AIServicePort aiServicePort;

    public AIWorkerController(AIServicePort aiServicePort) {
        this.aiServicePort = aiServicePort;
    }

    @Operation(summary = "Consultar estado de job en IA", description = "Fallback para consultar el estado del job directamente en el worker de IA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado retornado correctamente",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "jobId": "ai-task-001",
                          "status": "completed",
                          "progressPercentage": 100
                        }
                        """))),
            @ApiResponse(responseCode = "404", description = "Job no encontrado en el worker",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "code": "HTTP_404",
                          "message": "job not found",
                          "timestamp": "2026-02-21T12:00:00"
                        }
                        """)))
    })
    @GetMapping({"/ai/job/{jobId}", "/svmvp/ai/job/{jobId}"})
    public ResponseEntity<AIWorkerJobStatusResponse> getWorkerJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(aiServicePort.getJobStatus(jobId));
    }


    @Operation(summary = "Health check del worker de IA", description = "Verifica que el worker de IA esté disponible.")
    @ApiResponse(responseCode = "200", description = "Worker disponible",
            content = @Content(examples = @ExampleObject(value = """
                        {
                          "status": "ok",
                          "version": "1.0.0"
                        }
                        """)))
    @GetMapping({"/ai/health", "/svmvp/ai/health"})
    public ResponseEntity<AIWorkerHealthResponse> getWorkerHealth() {
        return ResponseEntity.ok(aiServicePort.getHealth());
    }
}
