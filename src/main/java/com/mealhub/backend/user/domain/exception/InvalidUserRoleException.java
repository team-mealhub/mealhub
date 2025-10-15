package com.mealhub.backend.user.domain.exception;

import com.mealhub.backend.global.domain.exception.BadRequestException;

public class InvalidUserRoleException extends BadRequestException {

    public InvalidUserRoleException() {
        super("Auth.Invalid.UserRole");
        setErrorCode(true);
    }
}
