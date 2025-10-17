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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;
    private final String UTF_8 = "UTF-8";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, messageUtils.getMessage("UnAuthorized"));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
