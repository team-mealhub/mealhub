package com.mealhub.backend.cart.domain.exception;

import com.mealhub.backend.global.domain.exception.BadRequestException;

public class CartItemInvalidQuantityException extends BadRequestException {

    public CartItemInvalidQuantityException(String code) {
        super(code);
        setErrorCode(true);
    }

    public static CartItemInvalidQuantityException tooLow() {
        return new CartItemInvalidQuantityException("CartItem.Quantity.TooLow");
    }

    public static CartItemInvalidQuantityException tooHigh() {
        return new CartItemInvalidQuantityException("CartItem.Quantity.TooHigh");
    }
}
