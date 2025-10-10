package com.mealhub.backend.user.domain.exception;

import com.mealhub.backend.global.domain.exception.ConflictException;

public class UserDuplicateException extends ConflictException {

    public UserDuplicateException() {
        super("User.Conflict.UserId");
        setErrorCode(true);
    }
}
