package com.scalevision.backend.application.service;

import com.scalevision.backend.application.port.in.dto.JobStatusResult;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetJobStatusServiceTest {

    @Test
    void shouldReturnJobStatusWhenJobExists() {
        JobRepository repository = mock(JobRepository.class);
        GetJobStatusService service = new GetJobStatusService(repository);

        ProcessingJob job = new ProcessingJob(
                UUID.randomUUID(),
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
        job.updateStatus(JobStatus.PROCESSING);

        when(repository.findById(job.getId())).thenReturn(Optional.of(job));

        Optional<JobStatusResult> result = service.getJobStatus(job.getId());

        assertTrue(result.isPresent());
        assertEquals(JobStatus.PROCESSING, result.get().status());
    }

    @Test
    void shouldReturnEmptyWhenJobDoesNotExist() {
        JobRepository repository = mock(JobRepository.class);
        GetJobStatusService service = new GetJobStatusService(repository);
        UUID jobId = UUID.randomUUID();

        when(repository.findById(jobId)).thenReturn(Optional.empty());

        Optional<JobStatusResult> result = service.getJobStatus(jobId);

        assertFalse(result.isPresent());
    }
}
