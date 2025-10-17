package com.mealhub.backend.order.domain.event;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderStatusUpdatedEvent extends OrderEvent {

    private final OrderStatus prevStatus;
    private final OrderStatus currStatus;
    private final String reason;

    public OrderStatusUpdatedEvent(UUID orderId, Long userId, OrderStatus prevStatus, OrderStatus currStatus, String reason) {
        super(orderId, userId);
        this.prevStatus = prevStatus;
        this.currStatus = currStatus;
        this.reason = reason;
    }
}
