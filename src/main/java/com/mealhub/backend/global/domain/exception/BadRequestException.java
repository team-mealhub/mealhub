package com.mealhub.backend.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class BadRequestException extends CommonException {

    public BadRequestException() {
        super("BadRequest", HttpStatus.BAD_REQUEST);
        setErrorCode(true);
    }

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.BAD_REQUEST);
    }
}
