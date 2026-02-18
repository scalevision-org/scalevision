package com.scalevision.backend.infrastructure.adapter.out.persistence;

import com.scalevision.backend.application.port.out.JobRepository;
import com.scalevision.backend.domain.model.ProcessingJob;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JpaJobRepositoryAdapter implements JobRepository {

    private final SpringDataJobJpaRepository jpaRepository;
    private final JobMapper jobMapper;

    public JpaJobRepositoryAdapter(SpringDataJobJpaRepository jpaRepository, JobMapper jobMapper) {
        this.jpaRepository = jpaRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public ProcessingJob save(ProcessingJob job) {
        JobEntity saved = jpaRepository.save(jobMapper.toEntity(job));
        return jobMapper.toDomain(saved);
    }

    @Override
    public Optional<ProcessingJob> findById(UUID id) {
        return jpaRepository.findById(id).map(jobMapper::toDomain);
    }
}
