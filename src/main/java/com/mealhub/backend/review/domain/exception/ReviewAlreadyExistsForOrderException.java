package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.ConflictException;

public class ReviewAlreadyExistsForOrderException extends ConflictException {
    public ReviewAlreadyExistsForOrderException() {
        super("Review.Conflict.AlreadyExistsForOrder");
        setErrorCode(true);
    }
}
