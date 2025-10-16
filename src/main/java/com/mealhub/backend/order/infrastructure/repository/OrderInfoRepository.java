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
import java.util.Optional;
import java.util.UUID;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, UUID>, OrderInfoRepositoryCustom {

    /**
     * @deprecated Use {@link #searchOrders(Long, List, OrderStatus, LocalDateTime, LocalDateTime, Pageable)} instead.
     * This method is replaced by a more flexible dynamic query method.
     */
    @Deprecated
    Page<OrderInfo> findByUserId(Long userId, Pageable pageable);

    /**
     * @deprecated Use {@link #searchOrders(Long, List, OrderStatus, LocalDateTime, LocalDateTime, Pageable)} instead.
     * This method is replaced by a more flexible dynamic query method.
     */
    @Deprecated
    Page<OrderInfo> findByRestaurantId(UUID restaurantId, Pageable pageable);

    /**
     * @deprecated Use {@link #searchOrders(Long, List, OrderStatus, LocalDateTime, LocalDateTime, Pageable)} instead.
     * This method is replaced by a more flexible dynamic query method.
     */
    @Deprecated
    Page<OrderInfo> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * @deprecated Use {@link #searchOrders(Long, List, OrderStatus, LocalDateTime, LocalDateTime, Pageable)} instead.
     * This method is replaced by a more flexible dynamic query method.
     */
    @Deprecated
    Page<OrderInfo> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    /**
     * @deprecated Use {@link #searchOrders(Long, List, OrderStatus, LocalDateTime, LocalDateTime, Pageable)} instead.
     * This method is replaced by a more flexible dynamic query method.
     */
    @Deprecated
    Page<OrderInfo> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);

    // 복합 검색 (사용자, 가게, 상태, 기간)
    // QueryDSL 동적 쿼리로 구현 (OrderInfoRepositoryImpl 참조)
    // PostgreSQL null 파라미터 타입 추론 문제 해결을 위해 JPQL에서 QueryDSL로 마이그레이션
    // @Query 메서드는 OrderInfoRepositoryCustom 인터페이스와 OrderInfoRepositoryImpl 구현체로 대체됨

    // 상태 리스트로 주문 조회
    @Query("SELECT o FROM OrderInfo o WHERE o.status IN :statuses AND o.deletedAt IS NULL")
    Page<OrderInfo> findByStatusIn(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);

    /**
     * Find OrderInfo by ID with items (FETCH JOIN to prevent N+1)
     *
     * @param orderId the order ID
     * @return Optional of OrderInfo with items eagerly loaded
     */
    @Query("SELECT o FROM OrderInfo o LEFT JOIN FETCH o.items WHERE o.orderInfoId = :orderId")
    java.util.Optional<OrderInfo> findByIdWithItems(@Param("orderId") UUID orderId);

    // 주문 ID로 삭제되지 않은 주문 조회
    @Query("SELECT o FROM OrderInfo o WHERE o.orderInfoId = :orderInfoId AND o.deletedAt IS NULL")
    Optional<OrderInfo> findByOrderInfoIdAndDeletedAtIsNull(UUID orderInfoId);
}