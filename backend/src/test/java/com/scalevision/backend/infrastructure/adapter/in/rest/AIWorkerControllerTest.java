package com.scalevision.backend.infrastructure.adapter.in.rest;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.dto.AIWorkerHealthResponse;
import com.scalevision.backend.application.port.out.dto.AIWorkerJobStatusResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIWorkerController.class)
@Import(GlobalExceptionHandler.class)
class AIWorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void shouldProxyAiJobStatus() throws Exception {
        when(aiServicePort.getJobStatus("job-001"))
                .thenReturn(new AIWorkerJobStatusResponse("job-001", "processing", 45, null));

        mockMvc.perform(get("/svmvp/ai/job/{jobId}", "job-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("processing"))
                .andExpect(jsonPath("$.progressPercentage").value(45));
    }

    @Test
    void shouldProxyAiHealth() throws Exception {
        when(aiServicePort.getHealth())
                .thenReturn(new AIWorkerHealthResponse("OK", 2, true));

        mockMvc.perform(get("/svmvp/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.capacityAvailable").value(true));
    }
}
