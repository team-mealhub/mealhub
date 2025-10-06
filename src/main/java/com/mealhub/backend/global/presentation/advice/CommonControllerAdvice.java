package com.mealhub.backend.global.presentation.advice;

import com.mealhub.backend.global.domain.exception.CommonException;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice("com.mealhub.backend")
public class CommonControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Object message = e.getMessage();

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            Map<String, List<String>> errorMessages = commonException.getErrorMessages();

            if (errorMessages != null) { message = errorMessages; }
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status, message));
    }
}