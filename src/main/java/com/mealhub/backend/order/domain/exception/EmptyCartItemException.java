package com.mealhub.backend.order.domain.exception;

import com.mealhub.backend.global.domain.exception.BadRequestException;

public class EmptyCartItemException extends BadRequestException {

    public EmptyCartItemException() {
        super("Order.BadRequest.EmptyCartItem");
        setErrorCode(true);
    }
}
