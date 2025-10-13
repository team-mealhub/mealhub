package com.mealhub.backend.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ForbiddenException extends CommonException {

    public ForbiddenException() {
        super("Forbidden", HttpStatus.FORBIDDEN);
        setErrorCode(true);
    }

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.FORBIDDEN);
    }
}
