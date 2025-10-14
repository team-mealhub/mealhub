package com.mealhub.backend.global.infrastructure.config.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealhub.backend.global.domain.application.libs.MessageUtils;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;
    private final String UTF_8 = "UTF-8";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, messageUtils.getMessage("Forbidden"));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
