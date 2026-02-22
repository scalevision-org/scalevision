package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AISubjectCandidate;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ScanSubjectsRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ScanSubjectsResponse;
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

import java.util.List;

@RestController
@Tag(name = "Discovery", description = "Endpoints para escanear sujetos en un video antes del procesamiento")
public class DiscoveryController {

    private final AIServicePort aiServicePort;

    public DiscoveryController(AIServicePort aiServicePort) {
        this.aiServicePort = aiServicePort;
    }


    @Operation(
            summary = "Escanear sujetos en video",
            description = "Analiza el video y retorna los sujetos detectados con su ubicación y tipo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sujetos detectados correctamente",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "status": "ok",
                          "subjects": [
                            {
                              "tempId": "subject-001",
                              "thumbnailBase64": "base64string",
                              "appearanceRatio": 0.85,
                              "subjectType": "person",
                              "reference": {
                                "referenceTimestamp": 1.5,
                                "referenceBox": [0.1, 0.2, 0.3, 0.4]
                              }
                            }
                          ]
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "Request inválido",
                    content = @Content(examples = @ExampleObject(value = """
                        {
                          "code": "BAD_REQUEST",
                          "message": "video_url must not be blank",
                          "timestamp": "2026-02-21T12:00:00"
                        }
                        """)))
    })
    @PostMapping({"/ai/scan-subjects", "/svmvp/ai/scan-subjects"})
    public ResponseEntity<ScanSubjectsResponse> scanSubjects(@Valid @RequestBody ScanSubjectsRequest request) {
        AIScanSubjectsResponse result = aiServicePort.scanSubjects(request.toPortRequest());
        List<ScanSubjectsResponse.SubjectData> subjects = result.subjects().stream()
                .map(this::toSubjectData)
                .toList();
        return ResponseEntity.ok(new ScanSubjectsResponse(result.status(), subjects));
    }

    private ScanSubjectsResponse.SubjectData toSubjectData(AISubjectCandidate subject) {
        return new ScanSubjectsResponse.SubjectData(
                subject.tempId(),
                subject.thumbnailBase64(),
                subject.appearanceRatio(),
                subject.subjectType(),
                new ScanSubjectsResponse.ReferenceData(subject.referenceTimestamp(), subject.referenceBox())
        );
    }
}
