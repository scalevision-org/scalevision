package com.scalevision.backend.repository;

import com.scalevision.backend.entity.VideoPoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoPoc, Long> {
}
