package com.scalevision.backend.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AIServiceHttpAdapterTest {
    private MockWebServer mockWebServer;
    private AIServiceHttpAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/svmvp").toString();
        if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        adapter = new AIServiceHttpAdapter(baseUrl, 1000, 1000, new ObjectMapper());
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void deberiaRetornarRespuestaExitosa() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(202)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"ai_task_id\":\"ai-001\",\"status\":\"accepted\"}"));

        AIProcessingResponse response = adapter.processVideo(validRequest());

        assertEquals("ai-001", response.aiTaskId());
        assertEquals("accepted", response.status());
    }

    @Test
    void deberiaLanzarExcepcionCon400() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        assertThrows(VideoProcessingException.class, () -> adapter.processVideo(validRequest()));
    }

    @Test
    void deberiaLanzarExcepcionCon500SinReintentar() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(VideoProcessingException.class, () -> adapter.processVideo(validRequest()));
        assertEquals(3, mockWebServer.getRequestCount());
    }

    @Test
    void deberiaNoReintentarCon400() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        assertThrows(VideoProcessingException.class, () -> adapter.processVideo(validRequest()));
        assertEquals(1, mockWebServer.getRequestCount());
    }

    private AIProcessingRequest validRequest() {
        return new AIProcessingRequest(
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
        );
    }
}
