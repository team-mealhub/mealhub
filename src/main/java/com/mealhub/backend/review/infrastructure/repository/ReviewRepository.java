package com.mealhub.backend.review.infrastructure.repository;

import com.mealhub.backend.review.domain.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    // 단건 조회 - 미삭제 리뷰
    Optional<ReviewEntity> findByIdAndDeletedAtIsNull(UUID id);

    // 리스트 조회 - 미삭제 + 페이징/정렬
    Page<ReviewEntity> findByRestaurant_RestaurantIdAndDeletedAtIsNull(UUID restaurantId, Pageable pageable);

    Page<ReviewEntity> findByRestaurant_RestaurantIdAndDeletedAtIsNullAndOwnerOnlyFalse(UUID restaurantId, Pageable pageable);

    @Query("""
            select r from ReviewEntity r
            where r.restaurant.restaurantId = :restaurantId
              and r.deletedAt is null
              and (r.ownerOnly = false or r.user.id = :currentUserId)
            """)
    Page<ReviewEntity> findVisibleForUser(@Param("restaurantId") UUID restaurantId,
                                          @Param("currentUserId") Long currentUserId,
                                          Pageable pageable);

    // 주문 중복 여부 확인 + 미삭제건
    boolean existsByOrderIdAndDeletedAtIsNull(UUID orderId);

    Page<ReviewEntity> findByUser_IdAndDeletedAtIsNull(Long userId, Pageable pageable);
}
