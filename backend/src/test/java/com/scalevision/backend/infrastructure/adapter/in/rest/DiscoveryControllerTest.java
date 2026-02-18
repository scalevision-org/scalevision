package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AISubjectCandidate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiscoveryController.class)
@Import(GlobalExceptionHandler.class)
class DiscoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void shouldReturnSubjectsFromAiService() throws Exception {
        AISubjectCandidate subject = new AISubjectCandidate(
                "subject_01",
                "data:image/jpeg;base64,xxx",
                0.92,
                "person",
                12.5,
                new Double[]{0.40, 0.20, 0.60, 0.50}
        );

        when(aiServicePort.scanSubjects(any()))
                .thenReturn(new AIScanSubjectsResponse("success", List.of(subject)));

        String request = """
                {
                  "video_url": "https://cdn.test/video.mp4",
                  "min_appearance_ratio": 0.8,
                  "config": { "max_subjects": 3 }
                }
                """;

        mockMvc.perform(post("/svmvp/ai/scan-subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.subjects[0].tempId").value("subject_01"));
    }
}
