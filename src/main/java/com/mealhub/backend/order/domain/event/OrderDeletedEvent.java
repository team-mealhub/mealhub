package com.mealhub.backend.order.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderDeletedEvent extends OrderEvent {

    private final long amount;

    public OrderDeletedEvent(UUID orderId, Long userId, long amount) {
        super(orderId, userId);
        this.amount = amount;
    }
}
