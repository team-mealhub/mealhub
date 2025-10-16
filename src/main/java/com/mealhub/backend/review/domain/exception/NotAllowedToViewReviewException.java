package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

public class NotAllowedToViewReviewException extends ForbiddenException {
    public NotAllowedToViewReviewException() {
        super("Review.Forbidden.NotAllowedToView");
        setErrorCode(true);
    }
}
