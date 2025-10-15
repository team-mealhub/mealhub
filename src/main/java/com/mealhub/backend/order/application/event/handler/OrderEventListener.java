package com.mealhub.backend.order.application.event.handler;

import com.mealhub.backend.cart.application.service.CartItemService;
import com.mealhub.backend.cart.presentation.dto.request.CartItemUpdateRequest;
import com.mealhub.backend.order.domain.event.OrderCreatedEvent;
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

    private final PaymentService paymentService;
    private final CartItemService cartItemService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        PaymentLogRequest.Create paymentLogRequest = createPaymentLogRequest(event);
        paymentService.createPaymentLog(paymentLogRequest);

        CartItemUpdateRequest.Buying cartItemUpdateRequest = createCartItemUpdateRequest(event);
        cartItemService.updateCartItemsBuying(event.getUserId(), cartItemUpdateRequest);
    }

    private PaymentLogRequest.Create createPaymentLogRequest(OrderCreatedEvent event) {
        return new PaymentLogRequest.Create(
                event.getOrderId(),
                event.getUserId(),
                event.getAmount(),
                PaymentStatus.PENDING
        );
    }

    private CartItemUpdateRequest.Buying createCartItemUpdateRequest(OrderCreatedEvent event) {
        return new CartItemUpdateRequest.Buying(
                event.getCartItemIds(),
                true
        );
    }
}
