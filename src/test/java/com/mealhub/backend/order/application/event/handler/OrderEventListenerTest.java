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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderEventListener 테스트")
class OrderEventListenerTest {

    @Mock
    private CartItemService cartItemService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderStatusLogService orderStatusLogService;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    @DisplayName("주문 생성 이벤트 처리 - 성공")
    void handleOrderCreatedEvent_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        UUID cartItemId1 = UUID.randomUUID();
        UUID cartItemId2 = UUID.randomUUID();
        List<UUID> cartItemIds = List.of(cartItemId1, cartItemId2);
        Long amount = 25000L;

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cartItemIds, amount);

        // when
        orderEventListener.handleOrderCreatedEvent(event);

        // then
        verify(cartItemService, times(1)).updateCartItemsBuyingTrue(userId, cartItemIds);
        verify(paymentService, times(1)).createPaymentLog(any(PaymentLogRequest.Create.class));
    }

    @Test
    @DisplayName("주문 생성 이벤트 처리 - CartItemService 예외 발생 시 로깅만 수행")
    void handleOrderCreatedEvent_CartItemServiceException() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        List<UUID> cartItemIds = List.of(UUID.randomUUID());
        Long amount = 25000L;

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cartItemIds, amount);

        doThrow(new RuntimeException("CartItem update failed"))
                .when(cartItemService).updateCartItemsBuyingTrue(userId, cartItemIds);

        // when
        orderEventListener.handleOrderCreatedEvent(event);

        // then
        verify(cartItemService, times(1)).updateCartItemsBuyingTrue(userId, cartItemIds);
        // 예외가 발생해도 메서드는 정상 종료되어야 함 (로깅만 수행)
        verify(paymentService, never()).createPaymentLog(any());
    }

    @Test
    @DisplayName("주문 생성 이벤트 처리 - PaymentService 예외 발생 시 로깅만 수행")
    void handleOrderCreatedEvent_PaymentServiceException() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        List<UUID> cartItemIds = List.of(UUID.randomUUID());
        Long amount = 25000L;

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, cartItemIds, amount);

        doThrow(new RuntimeException("Payment log creation failed"))
                .when(paymentService).createPaymentLog(any(PaymentLogRequest.Create.class));

        // when
        orderEventListener.handleOrderCreatedEvent(event);

        // then
        verify(cartItemService, times(1)).updateCartItemsBuyingTrue(userId, cartItemIds);
        verify(paymentService, times(1)).createPaymentLog(any(PaymentLogRequest.Create.class));
        // 예외가 발생해도 메서드는 정상 종료되어야 함 (로깅만 수행)
    }

    @Test
    @DisplayName("주문 삭제 이벤트 처리 - 성공")
    void handleOrderDeletedEvent_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        Long amount = 25000L;
        OrderStatus prevStatus = OrderStatus.PENDING;
        String reason = "고객 취소";

        OrderDeletedEvent event = new OrderDeletedEvent(orderId, userId, amount, prevStatus, reason);

        // when
        orderEventListener.handleOrderDeletedEvent(event);

        // then
        verify(cartItemService, times(1)).updateCartItemsBuyingFalse(userId);
        verify(paymentService, times(1)).createPaymentLog(any(PaymentLogRequest.Create.class));
        verify(orderStatusLogService, times(1)).createOrderStatusLog(
                eq(orderId),
                eq(userId),
                eq(prevStatus),
                eq(OrderStatus.CANCELLED),
                eq(reason)
        );
    }

    @Test
    @DisplayName("주문 삭제 이벤트 처리 - 예외 발생 시 로깅만 수행")
    void handleOrderDeletedEvent_Exception() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        Long amount = 25000L;
        OrderStatus prevStatus = OrderStatus.PENDING;
        String reason = "고객 취소";

        OrderDeletedEvent event = new OrderDeletedEvent(orderId, userId, amount, prevStatus, reason);

        doThrow(new RuntimeException("CartItem restore failed"))
                .when(cartItemService).updateCartItemsBuyingFalse(userId);

        // when
        orderEventListener.handleOrderDeletedEvent(event);

        // then
        verify(cartItemService, times(1)).updateCartItemsBuyingFalse(userId);
        // 예외가 발생해도 메서드는 정상 종료되어야 함
        verify(paymentService, never()).createPaymentLog(any());
        verify(orderStatusLogService, never()).createOrderStatusLog(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("주문 상태 변경 이벤트 처리 - 성공")
    void handleOrderStatusUpdatedEvent_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        OrderStatus prevStatus = OrderStatus.PENDING;
        OrderStatus currStatus = OrderStatus.IN_PROGRESS;
        String reason = "조리 시작";

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(orderId, userId, prevStatus, currStatus, reason);

        // when
        orderEventListener.handleOrderStatusUpdatedEvent(event);

        // then
        verify(orderStatusLogService, times(1)).createOrderStatusLog(
                eq(orderId),
                eq(userId),
                eq(prevStatus),
                eq(currStatus),
                eq(reason)
        );
    }

    @Test
    @DisplayName("주문 상태 변경 이벤트 처리 - 예외 발생 시 로깅만 수행")
    void handleOrderStatusUpdatedEvent_Exception() {
        // given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        OrderStatus prevStatus = OrderStatus.PENDING;
        OrderStatus currStatus = OrderStatus.IN_PROGRESS;
        String reason = "조리 시작";

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(orderId, userId, prevStatus, currStatus, reason);

        doThrow(new RuntimeException("OrderStatusLog creation failed"))
                .when(orderStatusLogService).createOrderStatusLog(any(), any(), any(), any(), any());

        // when
        orderEventListener.handleOrderStatusUpdatedEvent(event);

        // then
        verify(orderStatusLogService, times(1)).createOrderStatusLog(
                eq(orderId),
                eq(userId),
                eq(prevStatus),
                eq(currStatus),
                eq(reason)
        );
        // 예외가 발생해도 메서드는 정상 종료되어야 함 (로깅만 수행)
    }
}
