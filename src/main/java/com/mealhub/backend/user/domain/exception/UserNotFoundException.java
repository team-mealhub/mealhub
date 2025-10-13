package com.mealhub.backend.user.domain.exception;

import com.mealhub.backend.global.domain.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super("User.NotFound");
        setErrorCode(true);
    }
}
