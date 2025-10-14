package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, UUID> {

    // 사용자별 주문 조회
    Page<OrderInfo> findByUserId(Long userId, Pageable pageable);

    // 가게별 주문 조회
    Page<OrderInfo> findByRestaurantId(UUID restaurantId, Pageable pageable);

    // 상태별 주문 조회
    Page<OrderInfo> findByStatus(OrderStatus status, Pageable pageable);

    // 사용자 + 상태별 주문 조회
    Page<OrderInfo> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    // 가게 + 상태별 주문 조회
    Page<OrderInfo> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);

    // 복합 검색 (사용자, 가게, 상태, 기간)
    // restaurantIds가 null이면 무시, 비어있지 않으면 IN 절 적용
    @Query("SELECT o FROM OrderInfo o WHERE " +
            "(:userId IS NULL OR o.userId = :userId) AND " +
            "(:restaurantIds IS NULL OR o.restaurantId IN :restaurantIds) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:from IS NULL OR o.createdAt >= :from) AND " +
            "(:to IS NULL OR o.createdAt <= :to) AND " +
            "o.deletedAt IS NULL")
    Page<OrderInfo> searchOrders(
            @Param("userId") Long userId,
            @Param("restaurantIds") List<UUID> restaurantIds,
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    // 상태 리스트로 주문 조회
    @Query("SELECT o FROM OrderInfo o WHERE o.status IN :statuses AND o.deletedAt IS NULL")
    Page<OrderInfo> findByStatusIn(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);
}