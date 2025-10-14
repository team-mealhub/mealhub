package com.mealhub.backend.order.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

import java.util.List;
import java.util.Map;

/**
 * 주문 도메인 특화 Forbidden 예외
 * - CUSTOMER가 타인의 주문에 접근하려고 할 때
 * - OWNER가 타 가게의 주문에 접근하려고 할 때
 * - 권한이 없는 작업을 수행하려고 할 때
 */
public class OrderForbiddenException extends ForbiddenException {

    public OrderForbiddenException() {
        super("Order Forbidden");
        setErrorCode(true);
    }

    public OrderForbiddenException(String message) {
        super(message);
        setErrorCode(true);
    }

    public OrderForbiddenException(Map<String, List<String>> errorMessages) {
        super(errorMessages);
        setErrorCode(true);
    }
}