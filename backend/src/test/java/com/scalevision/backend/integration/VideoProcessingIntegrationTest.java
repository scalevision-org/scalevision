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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VideoProcessingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void deberiaCrearJobYTransicionarAProcessing() throws Exception {
        when(aiServicePort.processVideo(any()))
                .thenReturn(new AIProcessingResponse("ai-task-001", "accepted", 45));

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
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andReturn();

        String jobId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("jobId").asText();

        mockMvc.perform(get("/svmvp/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.progressPercentage").value(50));
    }

    @Test
    void deberiaRetornar400CuandoVideoUrlEsInvalida() throws Exception {
        String request = """
                {
                  "video_url": "",
                  "callback_url": "http://localhost:8080/callbacks/ai"
                }
                """;

        mockMvc.perform(post("/svmvp/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar404CuandoJobNoExiste() throws Exception {
        mockMvc.perform(get("/svmvp/jobs/{jobId}",
                        "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
