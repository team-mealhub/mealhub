package com.mealhub.backend.order.application.event.publisher;

import com.mealhub.backend.order.domain.event.OrderCreatedEvent;
import com.mealhub.backend.order.domain.event.OrderDeletedEvent;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
    void publish(OrderDeletedEvent event);
}
