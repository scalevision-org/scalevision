package com.scalevision.backend.application.service;

import com.scalevision.backend.application.exception.VideoProcessingException;
import com.scalevision.backend.application.port.in.dto.ProcessVideoCommand;
import com.scalevision.backend.application.port.in.dto.ProcessVideoResult;
import com.scalevision.backend.application.port.out.AIServicePort;
import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.application.port.out.dto.AIProcessingRequest;
import com.scalevision.backend.application.port.out.dto.AIProcessingResponse;
import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessVideoServiceTest {

    @Test
    void shouldCreateJobAndMoveToProcessingWhenAiAccepts() {
        FakeJobRepository jobRepository = new FakeJobRepository();
        FakeAIServicePort aiServicePort = new FakeAIServicePort(false);
        ProcessVideoService service = new ProcessVideoService(jobRepository, aiServicePort);

        ProcessVideoResult result = service.processVideo(validCommand());

        assertNotNull(result.jobId());
        assertEquals(JobStatus.PROCESSING, result.status());
        assertEquals(2, jobRepository.savedJobs.size());
        assertEquals(JobStatus.PENDING, jobRepository.savedJobs.get(0).getStatus());
        assertEquals(JobStatus.PROCESSING, jobRepository.savedJobs.get(1).getStatus());
        assertEquals("ai-task-001", jobRepository.savedJobs.get(1).getAiTaskId());
    }

    @Test
    void shouldMarkJobAsFailedWhenAiThrowsError() {
        FakeJobRepository jobRepository = new FakeJobRepository();
        FakeAIServicePort aiServicePort = new FakeAIServicePort(true);
        ProcessVideoService service = new ProcessVideoService(jobRepository, aiServicePort);

        assertThrows(VideoProcessingException.class, () -> service.processVideo(validCommand()));
        assertEquals(2, jobRepository.savedJobs.size());
        assertEquals(JobStatus.FAILED, jobRepository.savedJobs.get(1).getStatus());
        assertEquals("timeout from AI", jobRepository.savedJobs.get(1).getErrorMessage());
    }

    @Test
    void shouldFailWhenVideoUrlIsInvalid() {
        FakeJobRepository jobRepository = new FakeJobRepository();
        FakeAIServicePort aiServicePort = new FakeAIServicePort(false);
        ProcessVideoService service = new ProcessVideoService(jobRepository, aiServicePort);

        ProcessVideoCommand command = new ProcessVideoCommand(
                "ftp://video.mp4",
                "9:16",
                30,
                "center"
        );

        assertThrows(VideoProcessingException.class, () -> service.processVideo(command));
        assertEquals(0, jobRepository.savedJobs.size());
    }

    private ProcessVideoCommand validCommand() {
        return new ProcessVideoCommand(
                "https://cdn.test/video.mp4",
                "9:16",
                30,
                "center"
        );
    }

    private static class FakeJobRepository implements JobRepository {

        private final List<ProcessingJob> savedJobs = new ArrayList<>();

        @Override
        public ProcessingJob save(ProcessingJob job) {
            savedJobs.add(cloneJob(job));
            return job;
        }

        @Override
        public Optional<ProcessingJob> findById(UUID id) {
            return savedJobs.stream()
                    .filter(job -> job.getId().equals(id))
                    .findFirst();
        }

        private ProcessingJob cloneJob(ProcessingJob source) {
            ProcessingJob copy = new ProcessingJob(
                    source.getId(),
                    source.getVideoUrl(),
                    source.getTargetAspectRatio(),
                    source.getTargetDuration(),
                    source.getFocusArea()
            );

            if (source.getStatus() == JobStatus.PROCESSING) {
                copy.updateStatus(JobStatus.PROCESSING);
            }
            if (source.getStatus() == JobStatus.FAILED) {
                copy.updateStatus(JobStatus.FAILED);
            }
            if (source.getStatus() == JobStatus.COMPLETED) {
                copy.updateStatus(JobStatus.PROCESSING);
                copy.updateStatus(JobStatus.COMPLETED);
            }

            copy.setAiTaskId(source.getAiTaskId());
            copy.setOutputUrl(source.getOutputUrl());
            copy.setErrorMessage(source.getErrorMessage());

            return copy;
        }
    }

    private static class FakeAIServicePort implements AIServicePort {

        private final boolean shouldThrow;

        private FakeAIServicePort(boolean shouldThrow) {
            this.shouldThrow = shouldThrow;
        }

        @Override
        public AIProcessingResponse processVideo(AIProcessingRequest request) {
            if (shouldThrow) {
                throw new RuntimeException("timeout from AI");
            }
            return new AIProcessingResponse("ai-task-001", "accepted");
        }
    }
}
