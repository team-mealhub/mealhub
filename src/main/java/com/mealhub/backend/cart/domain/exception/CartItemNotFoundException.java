package com.mealhub.backend.cart.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException() {
        super("CartItem.NotFound");
        setErrorCode(true);
    }
}
