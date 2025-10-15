package com.mealhub.backend.order.domain.event;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderDeletedEvent extends OrderEvent {

    private final long amount;
    private final OrderStatus prevStatus;
    private final String reason;

    public OrderDeletedEvent(UUID orderId, Long userId, long amount, OrderStatus prevStatus, String reason) {
        super(orderId, userId);
        this.amount = amount;
        this.prevStatus = prevStatus;
        this.reason = reason;
    }
}
