package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

public class NotReviewOwnerOrManagerException extends ForbiddenException {
    public NotReviewOwnerOrManagerException() {
        super("Review.Forbidden.NotOwnerOrManager");
        setErrorCode(true);
    }
}
