package com.scalevision.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsRequest;
import com.scalevision.backend.application.port.out.dto.AIScanSubjectsResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import com.scalevision.backend.infrastructure.adapter.in.rest.CallbackController;
import com.scalevision.backend.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import com.scalevision.backend.infrastructure.adapter.out.ai.AIServiceHttpAdapter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AIServiceContractTest {

    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    private AIServiceHttpAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/svmvp").toString();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        adapter = new AIServiceHttpAdapter(baseUrl, 1000, 1000, objectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void shouldSendRequestWithAllFieldsAndReceiveAccepted() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(202)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"ai_task_id\":\"ai-001\",\"status\":\"accepted\"}"));

        AIProcessingResponse response = adapter.processVideo(new AIProcessingRequest(
                "job-001",
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center",
                "http://localhost:8080/svmvp/callbacks/ai",
                "sv_secret",
                "auto",
                null,
                null,
                30,
                false
        ));

        assertEquals("ai-001", response.aiTaskId());
        assertEquals("accepted", response.status());
        assertEquals(45, response.estimatedProcessingTimeSec());

        RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        assertEquals("/svmvp/ai/process-video", request.getPath());

        JsonNode body = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals("job-001", body.get("job_id").asText());
        assertEquals("https://cdn.test/video.mp4", body.get("video_url").asText());
    }

    @Test
    void shouldHandle400WhenJobIdIsMissing() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        assertThrows(VideoProcessingException.class, () -> adapter.processVideo(new AIProcessingRequest(
                null,
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center",
                "http://localhost:8080/svmvp/callbacks/ai",
                "sv_secret",
                "auto",
                null,
                null,
                30,
                false
        )));
    }

    @Test
    void shouldHandle422WhenVideoUrlIsInvalid() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(422));

        assertThrows(VideoProcessingException.class, () -> adapter.processVideo(new AIProcessingRequest(
                "job-001",
                "invalid-url",
                "9:16",
                30,
                "center",
                "http://localhost:8080/svmvp/callbacks/ai",
                "sv_secret",
                "auto",
                null,
                null,
                30,
                false
        )));
    }

    @Test
    void shouldHandleTimeoutWhenIaTakesTooLong() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(202)
                .setBody("{\"ai_task_id\":\"ai-001\",\"status\":\"accepted\"}")
                .setBodyDelay(2, TimeUnit.SECONDS));

        VideoProcessingException ex = assertThrows(VideoProcessingException.class, () -> adapter.processVideo(new AIProcessingRequest(
                "job-001",
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center",
                "http://localhost:8080/svmvp/callbacks/ai",
                "sv_secret",
                "auto",
                null,
                null,
                30,
                false
        )));

        assertTrue(ex.getMessage().contains("timeout") || ex.getMessage().contains("AI service"));
    }

    @Test
    void shouldReturn200ForCompletedCallback() throws Exception {
        InMemoryJobRepository repository = new InMemoryJobRepository();
        ProcessingJob job = processingJobInProgress();
        repository.save(job);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CallbackController(repository, "1.0.0"))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String callback = """
                {
                  "job_id": "%s",
                  "status": "completed",
                  "output_url": "https://cdn.test/output.mp4"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", job.getWebhookSecret())
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForFailedCallback() throws Exception {
        InMemoryJobRepository repository = new InMemoryJobRepository();
        ProcessingJob job = processingJobInProgress();
        repository.save(job);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CallbackController(repository, "1.0.0"))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String callback = """
                {
                  "job_id": "%s",
                  "status": "failed",
                  "error_message": "MODEL_TIMEOUT"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", job.getWebhookSecret())
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404ForUnknownJobInCallback() throws Exception {
        InMemoryJobRepository repository = new InMemoryJobRepository();
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CallbackController(repository, "1.0.0"))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String callback = """
                {
                  "job_id": "%s",
                  "status": "completed"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "sv_x")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidCallbackPayload() throws Exception {
        InMemoryJobRepository repository = new InMemoryJobRepository();
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CallbackController(repository, "1.0.0"))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String callback = """
                {
                  "job_id": "",
                  "status": ""
                }
                """;

        mockMvc.perform(post("/callbacks/ai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", "sv_x")
                        .header("X-AI-Schema-Version", "1.0.0")
                        .content(callback))
                .andExpect(status().isBadRequest());
    }

    private ProcessingJob processingJobInProgress() {
        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
        job.setWebhookSecret("sv_secret");
        job.updateStatus(JobStatus.PROCESSING);
        return job;
    }

    private static class InMemoryJobRepository implements JobRepository {
        private final Map<UUID, ProcessingJob> storage = new HashMap<>();

        @Override
        public ProcessingJob save(ProcessingJob job) {
            storage.put(job.getId(), job);
            return job;
        }

        @Override
        public Optional<ProcessingJob> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }
    }
}
