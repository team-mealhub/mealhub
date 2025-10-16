package com.mealhub.backend.review.domain.exception;

import com.mealhub.backend.global.domain.exception.ForbiddenException;

public class NotOrderOwnerException extends ForbiddenException {
    public NotOrderOwnerException() {
        super("Order.Forbidden.NotOwner");
        setErrorCode(true);
    }
}
