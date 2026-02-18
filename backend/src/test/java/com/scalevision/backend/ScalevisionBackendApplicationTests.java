package com.scalevision.backend;

import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ScalevisionBackendApplicationTests {

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private AIServicePort aiServicePort;

    @Test
    void contextLoads() {
    }

}
