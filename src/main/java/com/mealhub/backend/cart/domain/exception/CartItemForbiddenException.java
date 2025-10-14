package com.mealhub.backend.cart.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

public class CartItemForbiddenException extends ForbiddenException {

    public CartItemForbiddenException() {
        super("CartItem.Forbidden");
        setErrorCode(true);
    }
}
