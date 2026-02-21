package com.scalevision.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void flujoCompletoExitoso() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-full-001", "accepted", 45));

        // 1. Frontend inicia proceso
        String request = """
                {
                  "video_url": "https://cdn.test/video.mp4",
                  "callback_url": "http://localhost:8080/callbacks/ai",
                  "webhook_secret": "sv_secret",
                  "tracking_mode": "auto",
                  "target_selection": {
                    "reference_timestamp": 1.5,
                    "reference_box": [0.1, 0.2, 0.3, 0.4]
                  },
                  "config": {
                    "target_aspect_ratio": "9:16",
                    "target_duration": 30,
                    "focus_area": "center"
                  }
                }
                """;

        MvcResult processResult = mockMvc.perform(post("/svmvp/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andReturn();

        String jobId = objectMapper.readTree(processResult.getResponse().getContentAsString())
                .get("jobId").asText();

        // 2. Frontend hace polling - estado PROCESSING
        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.progressPercentage").value(50))
                .andExpect(jsonPath("$.result").isEmpty())
                .andExpect(jsonPath("$.error").isEmpty());

        // 3. IA envía callback completed
        String callback = """
                {
                  "job_id": "%s",
                  "status": "completed",
                  "processing_stats": { "duration_sec": 48.3 },
                  "crop_recommendations": []
                }
                """.formatted(jobId);

        mockMvc.perform(post("/svmvp/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "sv_secret")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 4. Frontend hace polling final - estado COMPLETED
        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progressPercentage").value(100));
    }

    @Test
    void flujoCompletoFallido() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-full-002", "accepted", 45));

        String request = """
                {
                  "video_url": "https://cdn.test/video.mp4",
                  "callback_url": "http://localhost:8080/callbacks/ai",
                  "webhook_secret": "sv_secret",
                  "tracking_mode": "auto",
                  "target_selection": {
                    "reference_timestamp": 1.5,
                    "reference_box": [0.1, 0.2, 0.3, 0.4]
                  },
                  "config": {
                    "target_aspect_ratio": "9:16",
                    "target_duration": 30,
                    "focus_area": "center"
                  }
                }
                """;

        MvcResult processResult = mockMvc.perform(post("/svmvp/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andReturn();

        String jobId = objectMapper.readTree(processResult.getResponse().getContentAsString())
                .get("jobId").asText();

        // IA envía callback failed
        String callback = """
                {
                  "job_id": "%s",
                  "status": "failed",
                  "error_code": "MODEL_TIMEOUT",
                  "retryable": true
                }
                """.formatted(jobId);

        mockMvc.perform(post("/svmvp/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "sv_secret")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));

        // Polling final refleja FAILED
        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.progressPercentage").value(100))
                .andExpect(jsonPath("$.error.code").value("MODEL_TIMEOUT"));
    }

    @Test
    void fallbackGetAiJobUsable() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-full-003", "accepted", 45));

        String request = """
                {
                  "video_url": "https://cdn.test/video.mp4",
                  "callback_url": "http://localhost:8080/callbacks/ai",
                  "webhook_secret": "sv_secret",
                  "tracking_mode": "auto",
                  "target_selection": {
                    "reference_timestamp": 1.5,
                    "reference_box": [0.1, 0.2, 0.3, 0.4]
                  },
                  "config": {
                    "target_aspect_ratio": "9:16",
                    "target_duration": 30,
                    "focus_area": "center"
                  }
                }
                """;

        mockMvc.perform(post("/svmvp/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted());

        // Fallback técnico GET /svmvp/ai/job/{jobId} usable
        mockMvc.perform(get("/svmvp/ai/job/{jobId}", "ai-task-full-003"))
                .andExpect(status().isOk());
    }
}
