package com.scalevision.backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataJobJpaRepository extends JpaRepository<JobEntity, UUID> {
}
