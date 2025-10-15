package com.mealhub.backend.order.application.event.publisher;

import com.mealhub.backend.order.domain.event.OrderCreatedEvent;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
}
