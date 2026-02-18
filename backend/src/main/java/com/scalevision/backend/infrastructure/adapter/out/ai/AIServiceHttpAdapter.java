package com.scalevision.backend.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
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
        payload.put("target_aspect_ratio", request.targetAspectRatio());
        payload.put("target_duration", request.targetDuration());
        payload.put("focus_area", request.focusArea());

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

            return new AIProcessingResponse(aiTaskId, status);
        } catch (HttpStatusCodeException ex) {
            throw new VideoProcessingException("AI service error: HTTP " + ex.getStatusCode().value(), ex);
        } catch (ResourceAccessException ex) {
            throw new VideoProcessingException("AI service timeout", ex);
        } catch (Exception ex) {
            throw new VideoProcessingException("AI service unexpected error", ex);
        }
    }

    private String readText(JsonNode node, String key, String fallback) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull() || value.asText().isBlank()) {
            return fallback;
        }
        return value.asText();
    }
}
