package com.mealhub.backend.global.infrastructure.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealhub.backend.global.domain.application.libs.MessageUtils;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper, MessageUtils messageUtils) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.messageUtils = messageUtils;
        setFilterProcessesUrl("/v1/auth/signIn");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserSignInRequest requestDto = objectMapper.readValue(request.getInputStream(), UserSignInRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUserId(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String userId = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getRole();

        String token = jwtUtil.generateAccessToken(userId, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, messageUtils.getMessage("Auth.Unauthorized.Login"));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
