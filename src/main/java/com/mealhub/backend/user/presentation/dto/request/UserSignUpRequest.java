package com.mealhub.backend.user.presentation.dto.request;

import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    @Schema(description = "아이디", example = "test")
    private String userId;

    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[가-힣a-zA-Z]+$")
    @Schema(description = "이름", example = "김테스트")
    private String username;

    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$")
    @Schema(description = "닉네임", example = "테스트")
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).+$")
    @Schema(description = "비밀번호", example = "test1234!")
    private String password;

    @NotNull
    @Schema(description = "유저 역할", example = "ROLE_CUSTOMER")
    private UserRole role;

    @NotBlank
    @Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
}
