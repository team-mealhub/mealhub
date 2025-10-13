package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, UUID> {

    // 특정 주문의 상태 로그 조회
    List<OrderStatusLog> findByOrderInfo_oInfoIdOrderByCreatedAtDesc(UUID oInfoId);
}
