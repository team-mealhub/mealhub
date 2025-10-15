package com.mealhub.backend.order.domain.event;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class OrderCreatedEvent {
    private final UUID orderId;
    private final Long userId;
    private final List<UUID> cartItemIds;
    private final long amount;

    public OrderCreatedEvent(UUID orderId, Long userId, List<UUID> cartItemIds, long amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItemIds = cartItemIds;
        this.amount = amount;
    }
}
