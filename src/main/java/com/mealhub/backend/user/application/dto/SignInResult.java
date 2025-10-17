package com.mealhub.backend.user.application.dto;

import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResult {
    private String token;
    private UserResponse userResponse;
}
