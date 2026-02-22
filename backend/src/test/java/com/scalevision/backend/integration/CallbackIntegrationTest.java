package com.scalevision.backend.integration;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class CallbackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void deberiaActualizarJobACompletedConCallback() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-001", "accepted", 45));

        String jobId = crearJob();

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

        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progressPercentage").value(100));
    }

    @Test
    void deberiaActualizarJobAFailedConCallback() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-002", "accepted", 45));

        String jobId = crearJob();

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

        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.error.code").value("MODEL_TIMEOUT"));
    }

    @Test
    void deberiaRechazarCallbackConSecretInvalido() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-003", "accepted", 45));

        String jobId = crearJob();

        String callback = """
                {
                  "job_id": "%s",
                  "status": "completed"
                }
                """.formatted(jobId);

        mockMvc.perform(post("/svmvp/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "secret_incorrecto")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isUnauthorized());
    }

    private String crearJob() throws Exception {
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

        MvcResult result = mockMvc.perform(post("/svmvp/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("jobId").asText();
    }
}