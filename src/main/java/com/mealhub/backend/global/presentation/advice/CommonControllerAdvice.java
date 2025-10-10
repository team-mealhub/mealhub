package com.mealhub.backend.global.presentation.advice;

import com.mealhub.backend.global.domain.application.libs.MessageUtils;
import com.mealhub.backend.global.domain.exception.CommonException;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice("com.mealhub.backend")
@RequiredArgsConstructor
public class CommonControllerAdvice {

    private final MessageUtils messageUtils;

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

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN, messageUtils.getMessage("Forbidden")));
    }
}