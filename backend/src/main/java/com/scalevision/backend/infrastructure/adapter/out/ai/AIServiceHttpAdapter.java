package com.scalevision.backend.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
<<<<<<< HEAD
=======
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsRequest;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AISubjectCandidate;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
>>>>>>> origin/feature/backend-contract-alignment-v2
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> origin/feature/backend-contract-alignment-v2
import java.util.Map;

@Component
public class AIServiceHttpAdapter implements AIServicePort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public AIServiceHttpAdapter(
            @Value("${ai.service.base-url:http://localhost:8000/svmvp}") String baseUrl
    ) {
        this(baseUrl, 2000, 5000, new ObjectMapper());
    }

    public AIServiceHttpAdapter(String baseUrl, int connectTimeoutMs, int readTimeoutMs, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public AIProcessingResponse processVideo(AIProcessingRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("job_id", request.jobId());
        payload.put("video_url", request.videoUrl());
<<<<<<< HEAD
        payload.put("target_aspect_ratio", request.targetAspectRatio());
        payload.put("target_duration", request.targetDuration());
        payload.put("focus_area", request.focusArea());
=======
        payload.put("callback_url", request.callbackUrl());
        payload.put("webhook_secret", request.webhookSecret());
        payload.put("tracking_mode", resolveTrackingMode(request.trackingMode()));

        Map<String, Object> targetSelection = new HashMap<>();
        targetSelection.put("reference_timestamp", request.referenceTimestamp());
        targetSelection.put("reference_box", request.referenceBox());
        payload.put("target_selection", targetSelection);

        Map<String, Object> config = new HashMap<>();
        config.put("target_aspect_ratio", request.targetAspectRatio());
        config.put("target_duration", request.targetDuration());
        config.put("focus_area", request.focusArea());
        config.put("fps_sampled", request.fpsSampled());
        config.put("include_trajectory_data", request.includeTrajectoryData());
        payload.put("config", config);
>>>>>>> origin/feature/backend-contract-alignment-v2

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/ai/process-video",
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            JsonNode body = objectMapper.readTree(response.getBody());
            String aiTaskId = readText(body, "ai_task_id", "job_id");
            String status = readText(body, "status", "accepted");
<<<<<<< HEAD

            return new AIProcessingResponse(aiTaskId, status);
=======
            int estimatedTime = readInt(body, "estimated_processing_time_sec", 45);

            return new AIProcessingResponse(aiTaskId, status, estimatedTime);
>>>>>>> origin/feature/backend-contract-alignment-v2
        } catch (HttpStatusCodeException ex) {
            throw new VideoProcessingException("AI service error: HTTP " + ex.getStatusCode().value(), ex);
        } catch (ResourceAccessException ex) {
            throw new VideoProcessingException("AI service timeout", ex);
        } catch (Exception ex) {
            throw new VideoProcessingException("AI service unexpected error", ex);
        }
    }

<<<<<<< HEAD
=======
    @Override
    public AIScanSubjectsResponse scanSubjects(AIScanSubjectsRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("video_url", request.videoUrl());
        payload.put("min_appearance_ratio", request.minAppearanceRatio());

        Map<String, Object> config = new HashMap<>();
        config.put("max_subjects", request.maxSubjects());
        payload.put("config", config);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/ai/scan-subjects",
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            JsonNode body = objectMapper.readTree(response.getBody());
            String status = readText(body, "status", "success");
            List<AISubjectCandidate> subjects = parseSubjects(body.get("subjects"));
            return new AIScanSubjectsResponse(status, subjects);
        } catch (HttpStatusCodeException ex) {
            throw new VideoProcessingException("AI scan error: HTTP " + ex.getStatusCode().value(), ex);
        } catch (ResourceAccessException ex) {
            throw new VideoProcessingException("AI scan timeout", ex);
        } catch (Exception ex) {
            throw new VideoProcessingException("AI scan unexpected error", ex);
        }
    }

    @Override
    public AIWorkerJobStatusResponse getJobStatus(String jobId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/ai/job/" + jobId,
                    String.class
            );
            JsonNode body = objectMapper.readTree(response.getBody());
            return new AIWorkerJobStatusResponse(
                    readText(body, "job_id", jobId),
                    readText(body, "status", "processing"),
                    readInt(body, "progress_percentage", 0),
                    body.has("result") ? body.get("result").toString() : null
            );
        } catch (Exception ex) {
            throw new VideoProcessingException("AI job polling error", ex);
        }
    }

    @Override
    public AIWorkerHealthResponse getHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/health", String.class);
            JsonNode body = objectMapper.readTree(response.getBody());
            return new AIWorkerHealthResponse(
                    readText(body, "status", "UNKNOWN"),
                    readInt(body, "active_jobs", 0),
                    readBoolean(body, "capacity_available", false)
            );
        } catch (Exception ex) {
            throw new VideoProcessingException("AI healthcheck error", ex);
        }
    }

>>>>>>> origin/feature/backend-contract-alignment-v2
    private String readText(JsonNode node, String key, String fallback) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            return fallback;
        }
        return value.asText();
    }
<<<<<<< HEAD
=======

    private int readInt(JsonNode node, String key, int fallback) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return fallback;
        }
        return value.asInt(fallback);
    }

    private boolean readBoolean(JsonNode node, String key, boolean fallback) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return fallback;
        }
        return value.asBoolean(fallback);
    }

    private String resolveTrackingMode(String rawMode) {
        if (rawMode == null || rawMode.isBlank()) {
            return "auto";
        }
        return rawMode;
    }

    private List<AISubjectCandidate> parseSubjects(JsonNode subjectsNode) {
        if (subjectsNode == null || !subjectsNode.isArray()) {
            return List.of();
        }

        return subjectsNode.findValuesAsText("temp_id").stream()
                .map(tempId -> {
                    JsonNode subjectNode = findSubjectByTempId(subjectsNode, tempId);
                    JsonNode referenceData = subjectNode != null ? subjectNode.get("reference_data") : null;
                    Double referenceTimestamp = referenceData != null && referenceData.has("timestamp")
                            ? referenceData.get("timestamp").asDouble()
                            : null;

                    Double[] referenceBox = null;
                    if (referenceData != null && referenceData.has("box") && referenceData.get("box").isArray()) {
                        JsonNode boxNode = referenceData.get("box");
                        referenceBox = new Double[boxNode.size()];
                        for (int i = 0; i < boxNode.size(); i++) {
                            referenceBox[i] = boxNode.get(i).asDouble();
                        }
                    }

                    return new AISubjectCandidate(
                            tempId,
                            subjectNode != null ? readText(subjectNode, "thumbnail_base64", null) : null,
                            subjectNode != null && subjectNode.has("appearance_ratio")
                                    ? subjectNode.get("appearance_ratio").asDouble()
                                    : null,
                            subjectNode != null ? readText(subjectNode, "subject_type", null) : null,
                            referenceTimestamp,
                            referenceBox
                    );
                })
                .toList();
    }

    private JsonNode findSubjectByTempId(JsonNode subjectsNode, String tempId) {
        for (JsonNode subjectNode : subjectsNode) {
            if (tempId.equals(readText(subjectNode, "temp_id", ""))) {
                return subjectNode;
            }
        }
        return null;
    }
>>>>>>> origin/feature/backend-contract-alignment-v2
}
