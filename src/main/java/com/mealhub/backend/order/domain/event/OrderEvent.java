package com.mealhub.backend.order.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class OrderEvent {
    private final UUID orderId;
    private final Long userId;

    public OrderEvent(UUID orderId, Long userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
}
