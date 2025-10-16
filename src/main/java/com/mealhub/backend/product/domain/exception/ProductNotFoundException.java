package com.mealhub.backend.product.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException() {
        super("Product.NotFound");
        setErrorCode(true);
    }
}
