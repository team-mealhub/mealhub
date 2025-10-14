package com.mealhub.backend.user.presentation.dto.request;

import com.mealhub.backend.user.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSignUpRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    private String userId;

    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[가-힣a-zA-Z]+$")
    private String username;

    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$")
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).+$")
    private String password;

    @NotNull
    private UserRole role;

    @NotBlank
    @Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
    private String phone;
}
