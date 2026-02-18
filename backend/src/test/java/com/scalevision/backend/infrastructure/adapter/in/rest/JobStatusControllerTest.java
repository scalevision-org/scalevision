package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.in.GetJobStatusUseCase;
import com.scalevision.backend.application.port.in.dto.JobStatusResult;
import com.scalevision.backend.domain.model.JobStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobStatusController.class)
@Import(GlobalExceptionHandler.class)
class JobStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetJobStatusUseCase getJobStatusUseCase;

    @Test
    void shouldReturnProcessingStatus() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(getJobStatusUseCase.getJobStatus(jobId))
<<<<<<< HEAD
                .thenReturn(Optional.of(new JobStatusResult(jobId, JobStatus.PROCESSING, null, null)));
=======
                .thenReturn(Optional.of(new JobStatusResult(jobId, JobStatus.PROCESSING, null, null, null)));
>>>>>>> origin/feature/backend-contract-alignment-v2

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.progressPercentage").value(50));
    }

    @Test
    void shouldReturnCompletedStatusWithResult() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(getJobStatusUseCase.getJobStatus(jobId))
                .thenReturn(Optional.of(new JobStatusResult(
                        jobId,
                        JobStatus.COMPLETED,
                        "https://cdn.test/output.mp4",
<<<<<<< HEAD
=======
                        "{\"crop_recommendations\":[]}",
>>>>>>> origin/feature/backend-contract-alignment-v2
                        null
                )));

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
<<<<<<< HEAD
                .andExpect(jsonPath("$.result.outputVideoUrl").value("https://cdn.test/output.mp4"));
=======
                .andExpect(jsonPath("$.result.outputVideoUrl").value("https://cdn.test/output.mp4"))
                .andExpect(jsonPath("$.result.aiResultPayload").value("{\"crop_recommendations\":[]}"));
>>>>>>> origin/feature/backend-contract-alignment-v2
    }

    @Test
    void shouldReturnFailedStatusWithError() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(getJobStatusUseCase.getJobStatus(jobId))
                .thenReturn(Optional.of(new JobStatusResult(
                        jobId,
                        JobStatus.FAILED,
                        null,
<<<<<<< HEAD
=======
                        null,
>>>>>>> origin/feature/backend-contract-alignment-v2
                        "MODEL_TIMEOUT"
                )));

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.error.code").value("MODEL_TIMEOUT"));
    }

    @Test
    void shouldReturn404WhenJobNotFound() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(getJobStatusUseCase.getJobStatus(jobId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/jobs/{jobId}", jobId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenJobIdIsInvalid() throws Exception {
        mockMvc.perform(get("/jobs/{jobId}", "abc"))
                .andExpect(status().isBadRequest());
    }
}
