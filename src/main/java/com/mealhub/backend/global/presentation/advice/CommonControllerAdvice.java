package com.mealhub.backend.global.presentation.advice;

import com.mealhub.backend.global.domain.application.libs.MessageUtils;
import com.mealhub.backend.global.domain.exception.CommonException;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice("com.mealhub.backend")
@RequiredArgsConstructor
public class CommonControllerAdvice {

    private final MessageUtils messageUtils;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Object message = e.getMessage();

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status, message));
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> commonErrorHandler(CommonException e) {
        HttpStatus status = e.getStatus();
        Object message;

        if (e.getErrorMessages() != null && !e.getErrorMessages().isEmpty()) {
            message = e.isErrorCode() ? convertErrorMessages(e.getErrorMessages()) : e.getErrorMessages();
        } else {
            message = e.isErrorCode() ? messageUtils.getMessage(e.getMessage()) : e.getMessage();
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> errorMessages = messageUtils.getErrorMessages(e.getBindingResult());

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status, errorMessages));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN, messageUtils.getMessage("Forbidden")));
    }

    private Map<String, List<String>> convertErrorMessages(Map<String, List<String>> errorMessages) {
        return errorMessages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Optional.ofNullable(entry.getValue())
                                .map(messageUtils::getMessages)
                                .orElse(List.of())
                ));
    }
}