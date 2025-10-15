package com.mealhub.backend.order.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderDeletedEvent {

    private final UUID orderId;
    private final Long userId;
    private final long amount;

    public OrderDeletedEvent(UUID orderId, Long userId, long amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }
}
