package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, UUID> {
}
