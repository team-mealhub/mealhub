package com.mealhub.backend.user.presentation.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String nickname;
    private String password;
    private String phone;
}