package com.mealhub.backend.order.infrastructure.event;

import com.mealhub.backend.order.application.event.publisher.OrderEventPublisher;
import com.mealhub.backend.order.domain.event.OrderCreatedEvent;
import com.mealhub.backend.order.domain.event.OrderDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApplicationEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderDeletedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
