package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException() {
        super("Review.NotFound");
        setErrorCode(true);
    }
}
