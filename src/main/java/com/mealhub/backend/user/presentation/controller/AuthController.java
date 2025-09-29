package com.mealhub.backend.user.presentation.controller;

import com.mealhub.backend.user.application.service.AuthService;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public void signUp(@RequestBody UserSignUpRequest request) {
        authService.signUp(request);
    }
}
