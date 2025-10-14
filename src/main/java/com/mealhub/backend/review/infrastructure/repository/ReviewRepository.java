package com.mealhub.backend.review.infrastructure.repository;

import com.mealhub.backend.review.domain.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    // 단건 조회 - 미삭제 리뷰
    Optional<ReviewEntity> findByIdAndDeletedAtIsNull(UUID id);

    // 리스트 조회 - 미삭제 + 페이징/정렬
    Page<ReviewEntity> findByRestaurant_RestaurantIdAndDeletedAtIsNull(UUID restaurantId, Pageable pageable);
}
