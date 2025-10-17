package com.mealhub.backend.order.domain.exception;

import com.mealhub.backend.global.domain.exception.BadRequestException;

import java.util.List;
import java.util.Map;

/**
 * 주문 취소 관련 예외
 * - PENDING 상태가 아닌 주문을 취소하려고 할 때
 */
public class OrderCancelException extends BadRequestException {

    public OrderCancelException() {
        super("Order Cancel Failed");
        setErrorCode(true);
    }

    public OrderCancelException(String message) {
        super(message);
        setErrorCode(true);
    }

    public OrderCancelException(Map<String, List<String>> errorMessages) {
        super(errorMessages);
        setErrorCode(true);
    }
}
