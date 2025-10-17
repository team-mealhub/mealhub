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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartItemService cartItemService;
    private final PaymentService paymentService;
    private final OrderStatusLogService orderStatusLogService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Processing OrderCreatedEvent - orderId: {}, userId: {}", event.getOrderId(), event.getUserId());

        try {
            cartItemService.updateCartItemsBuyingTrue(event.getUserId(), event.getCartItemIds());
            log.debug("Updated cart items buying status to true for user: {}", event.getUserId());

            PaymentLogRequest.Create paymentLogRequest = createPaymentLogRequest(event);
            paymentService.createPaymentLog(paymentLogRequest);
            log.debug("Created payment log for order: {}", event.getOrderId());

            log.info("Successfully processed OrderCreatedEvent - orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent - orderId: {}, userId: {}, error: {}",
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
            // 비동기 이벤트이므로 예외를 전파하지 않음 (로깅만 수행)
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderDeletedEvent(OrderDeletedEvent event) {
        log.info("Processing OrderDeletedEvent - orderId: {}, userId: {}, reason: {}",
                event.getOrderId(), event.getUserId(), event.getReason());

        try {
            cartItemService.updateCartItemsBuyingFalse(event.getUserId());
            log.debug("Updated cart items buying status to false for user: {}", event.getUserId());

            PaymentLogRequest.Create paymentLogRequest = createPaymentLogRequest(event);
            paymentService.createPaymentLog(paymentLogRequest);
            log.debug("Created payment log with CANCELLED status for order: {}", event.getOrderId());

            orderStatusLogService.createOrderStatusLog(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getPrevStatus(),
                    OrderStatus.CANCELLED,
                    event.getReason()
            );
            log.debug("Created order status log - orderId: {}, prevStatus: {}, currStatus: CANCELLED",
                    event.getOrderId(), event.getPrevStatus());

            log.info("Successfully processed OrderDeletedEvent - orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process OrderDeletedEvent - orderId: {}, userId: {}, error: {}",
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
            // 비동기 이벤트이므로 예외를 전파하지 않음 (로깅만 수행)
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusUpdatedEvent(OrderStatusUpdatedEvent event) {
        log.info("Processing OrderStatusUpdatedEvent - orderId: {}, prevStatus: {}, currStatus: {}, reason: {}",
                event.getOrderId(), event.getPrevStatus(), event.getCurrStatus(), event.getReason());

        try {
            orderStatusLogService.createOrderStatusLog(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getPrevStatus(),
                    event.getCurrStatus(),
                    event.getReason()
            );
            log.debug("Created order status log - orderId: {}, prevStatus: {}, currStatus: {}",
                    event.getOrderId(), event.getPrevStatus(), event.getCurrStatus());

            log.info("Successfully processed OrderStatusUpdatedEvent - orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process OrderStatusUpdatedEvent - orderId: {}, prevStatus: {}, currStatus: {}, error: {}",
                    event.getOrderId(), event.getPrevStatus(), event.getCurrStatus(), e.getMessage(), e);
            // 비동기 이벤트이므로 예외를 전파하지 않음 (로깅만 수행)
        }
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
