package com.mealhub.backend.order.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 * 주문 도메인 특화 NotFound 예외
 * - 주문을 찾을 수 없을 때
 * - 레스토랑을 찾을 수 없을 때
 */
public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException() {
        super("Order Not Found");
        setErrorCode(true);
    }

    public OrderNotFoundException(String message) {
        super(message);
        setErrorCode(true);
    }

    public OrderNotFoundException(Map<String, List<String>> errorMessages) {
        super(errorMessages);
        setErrorCode(true);
    }
}