package com.mealhub.backend.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ConflictException extends CommonException {

    public ConflictException(String message, HttpStatus status) {
        super("Conflict", HttpStatus.CONFLICT);
        setErrorCode(true);
    }

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.CONFLICT);
    }
}
