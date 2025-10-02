package com.mealhub.backend.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class UnAuthorizedException extends CommonException {

    public UnAuthorizedException() {
        super("UnAuthorized", HttpStatus.UNAUTHORIZED);
        setErrorCode(true);
    }

    public UnAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnAuthorizedException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.UNAUTHORIZED);
    }
}
