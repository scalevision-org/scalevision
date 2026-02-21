package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.domain.model.JobStatus;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaJobRepositoryAdapter.class, JobMapper.class})
public class JobRepositoryAdapterTest {

    @Autowired
    private JpaJobRepositoryAdapter repository;

    @Test
    void deberiaSaveYFindById() {
        ProcessingJob job = new ProcessingJob(UUID.randomUUID(), "https://video.com/test.mp4", "9:16", 30, "center");
        repository.save(job);

        Optional<ProcessingJob> found = repository.findById(job.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(job.getId());
    }

    @Test
    void deberiaRetornarTodosLosJobs() {
        repository.save(new ProcessingJob(UUID.randomUUID(), "https://video.com/1.mp4", "9:16", 30, "center"));
        repository.save(new ProcessingJob(UUID.randomUUID(), "https://video.com/2.mp4", "9:16", 30, "center"));

        List<ProcessingJob> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deberiaBuscarPorAiTaskId() {
        ProcessingJob job = new ProcessingJob(UUID.randomUUID(), "https://video.com/test.mp4", "9:16", 30, "center");
        job.setAiTaskId("ai-task-123");
        repository.save(job);

        Optional<ProcessingJob> found = repository.findByAiTaskId("ai-task-123");
        assertThat(found).isPresent();
        assertThat(found.get().getAiTaskId()).isEqualTo("ai-task-123");
    }

    @Test
    void deberiaBuscarPorStatus() {
        ProcessingJob job = new ProcessingJob(UUID.randomUUID(), "https://video.com/test.mp4", "9:16", 30, "center");
        repository.save(job);

        List<ProcessingJob> pending = repository.findByStatus(JobStatus.PENDING);
        assertThat(pending).isNotEmpty();
        assertThat(pending.get(0).getStatus()).isEqualTo(JobStatus.PENDING);
    }

    @Test
    void deberiaRetornarVacioCuandoAiTaskIdNoExiste() {
        Optional<ProcessingJob> found = repository.findByAiTaskId("no-existe");
        assertThat(found).isEmpty();
    }
}
