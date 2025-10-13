package com.mealhub.backend.review.infrastructure.repository;

import com.mealhub.backend.review.domain.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {
}
