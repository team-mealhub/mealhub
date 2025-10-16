package com.mealhub.backend.order.application.event.handler;

import com.mealhub.backend.cart.application.service.CartItemService;
import com.mealhub.backend.order.application.service.OrderStatusLogService;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.domain.event.OrderCreatedEvent;
import com.mealhub.backend.order.domain.event.OrderDeletedEvent;
import com.mealhub.backend.order.domain.event.OrderStatusUpdatedEvent;
import com.mealhub.backend.payment.application.service.PaymentService;
import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import com.mealhub.backend.payment.presentation.dto.request.PaymentLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartItemService cartItemService;
    private final PaymentService paymentService;
    private final OrderStatusLogService orderStatusLogService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        cartItemService.updateCartItemsBuyingTrue(event.getUserId(), event.getCartItemIds());

        PaymentLogRequest.Create paymentLogRequest = createPaymentLogRequest(event);
        paymentService.createPaymentLog(paymentLogRequest);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderDeletedEvent(OrderDeletedEvent event) {
        cartItemService.updateCartItemsBuyingFalse(event.getUserId());

        PaymentLogRequest.Create paymentLogRequest = createPaymentLogRequest(event);
        paymentService.createPaymentLog(paymentLogRequest);

        orderStatusLogService.createOrderStatusLog(
                event.getOrderId(),
                event.getUserId(),
                event.getPrevStatus(),
                OrderStatus.CANCELLED,
                event.getReason()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusUpdatedEvent(OrderStatusUpdatedEvent event) {
        orderStatusLogService.createOrderStatusLog(
                event.getOrderId(),
                event.getUserId(),
                event.getPrevStatus(),
                event.getCurrStatus(),
                event.getReason()
        );
    }

    private PaymentLogRequest.Create createPaymentLogRequest(OrderCreatedEvent event) {
        return new PaymentLogRequest.Create(
                event.getOrderId(),
                event.getUserId(),
                event.getAmount(),
                PaymentStatus.PENDING
        );
    }

    private PaymentLogRequest.Create createPaymentLogRequest(OrderDeletedEvent event) {
        return new PaymentLogRequest.Create(
                event.getOrderId(),
                event.getUserId(),
                event.getAmount(),
                PaymentStatus.CANCELLED
        );
    }
}
