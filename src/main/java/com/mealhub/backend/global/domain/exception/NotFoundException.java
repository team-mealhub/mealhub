package com.mealhub.backend.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class NotFoundException extends CommonException {

    public NotFoundException() {
        super("NotFound", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.NOT_FOUND);
    }
}
