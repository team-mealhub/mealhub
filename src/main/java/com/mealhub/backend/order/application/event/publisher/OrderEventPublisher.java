package com.mealhub.backend.order.application.event.publisher;

import com.mealhub.backend.order.domain.event.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
