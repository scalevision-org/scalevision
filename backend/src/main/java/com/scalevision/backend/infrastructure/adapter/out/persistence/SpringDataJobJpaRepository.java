package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.domain.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJobJpaRepository extends JpaRepository<JobEntity, UUID> {
    Optional<JobEntity>findByAiTaskId(String aiTaskId);
    List<JobEntity>findByStatus(JobStatus status);
}
