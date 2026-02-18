package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CallbackController.class)
@Import(GlobalExceptionHandler.class)
class CallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobRepository jobRepository;

    @Test
    void shouldUpdateJobToCompleted() throws Exception {
        ProcessingJob job = processingJobInProgress();
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String request = """
                {
                  "job_id": "%s",
                  "status": "completed",
                  "output_url": "https://cdn.test/output.mp4"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
=======
                        .header("X-Webhook-Secret", job.getWebhookSecret())
                        .header("X-AI-Schema-Version", "1.0.0")
>>>>>>> origin/feature/backend-contract-alignment-v2
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(job.getId().toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldUpdateJobToFailed() throws Exception {
        ProcessingJob job = processingJobInProgress();
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String request = """
                {
                  "job_id": "%s",
                  "status": "failed",
                  "error_message": "MODEL_TIMEOUT"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
=======
                        .header("X-Webhook-Secret", job.getWebhookSecret())
                        .header("X-AI-Schema-Version", "1.0.0")
>>>>>>> origin/feature/backend-contract-alignment-v2
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void shouldReturn404WhenJobDoesNotExist() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        String request = """
                {
                  "job_id": "%s",
                  "status": "completed"
                }
                """.formatted(jobId);

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
=======
                        .header("X-Webhook-Secret", "sv_x")
                        .header("X-AI-Schema-Version", "1.0.0")
>>>>>>> origin/feature/backend-contract-alignment-v2
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenTransitionIsInvalid() throws Exception {
        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );

        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        String request = """
                {
                  "job_id": "%s",
                  "status": "completed"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
=======
                        .header("X-Webhook-Secret", "sv_x")
                        .header("X-AI-Schema-Version", "1.0.0")
>>>>>>> origin/feature/backend-contract-alignment-v2
                        .content(request))
                .andExpect(status().isConflict());
    }

<<<<<<< HEAD
=======
    @Test
    void shouldReturn401WhenWebhookSecretDoesNotMatch() throws Exception {
        ProcessingJob job = processingJobInProgress();
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        String request = """
                {
                  "job_id": "%s",
                  "status": "completed"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "wrong")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

>>>>>>> origin/feature/backend-contract-alignment-v2
    private ProcessingJob processingJobInProgress() {
        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
<<<<<<< HEAD
=======
        job.setWebhookSecret("sv_secret");
>>>>>>> origin/feature/backend-contract-alignment-v2
        job.updateStatus(JobStatus.PROCESSING);
        return job;
    }
}
