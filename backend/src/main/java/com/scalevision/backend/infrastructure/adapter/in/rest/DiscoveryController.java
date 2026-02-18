package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AISubjectCandidate;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ScanSubjectsRequest;
import com.scalevision.backend.infrastructure.adapter.in.rest.dto.ScanSubjectsResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DiscoveryController {

    private final AIServicePort aiServicePort;

    public DiscoveryController(AIServicePort aiServicePort) {
        this.aiServicePort = aiServicePort;
    }

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
