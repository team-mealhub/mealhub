package com.mealhub.backend.user.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtUtil;
import com.mealhub.backend.user.application.service.AuthService;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public void signUp(@Valid @RequestBody UserSignUpRequest request) {
        authService.signUp(request);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody UserSignInRequest request) {
        String token = authService.signIn(request);

        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION_HEADER, token)
                .build();
    }
}
