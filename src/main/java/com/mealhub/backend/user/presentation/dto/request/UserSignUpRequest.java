package com.mealhub.backend.user.presentation.dto.request;

import com.mealhub.backend.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {
    private String userId;
    private String username;
    private String nickname;
    private String password;
    private UserRole role;
    private String phone;
}
