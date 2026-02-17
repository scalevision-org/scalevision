package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.in.ProcessVideoUseCase;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;
import com.scalevision.backend.domain.model.JobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@Import(GlobalExceptionHandler.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessVideoUseCase processVideoUseCase;

    @Test
    void shouldReturn202WhenRequestIsValid() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(processVideoUseCase.processVideo(any()))
                .thenReturn(new ProcessVideoResult(jobId, JobStatus.PROCESSING));

        String requestJson = """
                {
                  "videoUrl": "https://cdn.test/video.mp4",
                  "targetAspectRatio": "9:16",
                  "targetDuration": 30,
                  "focusArea": "center"
                }
                """;

        mockMvc.perform(post("/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").value(jobId.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        String requestJson = """
                {
                  "videoUrl": "",
                  "targetAspectRatio": "9:16"
                }
                """;

        mockMvc.perform(post("/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturn422WhenUseCaseThrowsVideoProcessingException() throws Exception {
        when(processVideoUseCase.processVideo(any()))
                .thenThrow(new VideoProcessingException("videoUrl is invalid"));

        String requestJson = """
                {
                  "videoUrl": "https://cdn.test/video.mp4"
                }
                """;

        mockMvc.perform(post("/videos/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("VIDEO_PROCESSING_ERROR"));
    }
}
