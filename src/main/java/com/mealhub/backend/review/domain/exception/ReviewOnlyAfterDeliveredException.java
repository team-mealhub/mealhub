package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

public class ReviewOnlyAfterDeliveredException extends ForbiddenException {
    public ReviewOnlyAfterDeliveredException() {
        super("Review.Forbidden.OnlyAfterDelivered");
        setErrorCode(true);
    }
}
