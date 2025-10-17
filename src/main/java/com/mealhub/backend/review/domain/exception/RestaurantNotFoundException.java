package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

public class RestaurantNotFoundException extends NotFoundException {
    public RestaurantNotFoundException() {
        super("Restaurant.NotFound");
        setErrorCode(true);
    }
}
