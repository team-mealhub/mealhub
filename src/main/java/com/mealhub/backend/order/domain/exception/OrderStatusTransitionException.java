package com.mealhub.backend.order.domain.exception;

import com.mealhub.backend.global.domain.exception.BadRequestException;

import java.util.List;
import java.util.Map;

/**
 * 주문 상태 전이 규칙 위반 예외
 * - 허용되지 않는 상태 전이를 시도할 때
 * - PENDING → IN_PROGRESS, CANCELLED
 * - IN_PROGRESS → OUT_FOR_DELIVERY, CANCELLED
 * - OUT_FOR_DELIVERY → DELIVERED, CANCELLED
 * - DELIVERED, CANCELLED → (변경 불가)
 */
public class OrderStatusTransitionException extends BadRequestException {

    public OrderStatusTransitionException() {
        super("Order Status Transition Failed");
        setErrorCode(true);
    }

    public OrderStatusTransitionException(String message) {
        super(message);
        setErrorCode(true);
    }

    public OrderStatusTransitionException(Map<String, List<String>> errorMessages) {
        super(errorMessages);
        setErrorCode(true);
    }
}