package com.mealhub.backend.order.infrastructure.repository;

import com.mealhub.backend.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}