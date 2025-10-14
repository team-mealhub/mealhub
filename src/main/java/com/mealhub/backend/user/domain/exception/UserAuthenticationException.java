package com.mealhub.backend.user.domain.exception;

import com.mealhub.backend.global.domain.exception.UnAuthorizedException;

public class UserAuthenticationException extends UnAuthorizedException {

    public UserAuthenticationException() {
        super("Auth.Unauthorized.SignIn");
        setErrorCode(true);
    }
}
