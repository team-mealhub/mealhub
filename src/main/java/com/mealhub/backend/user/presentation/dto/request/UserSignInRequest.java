package com.mealhub.backend.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSignInRequest {

    @NotBlank
    @Schema(description = "아이디", example = "test")
    private String userId;

    @NotBlank
    @Schema(description = "비밀번호", example = "test1234!")
    private String password;
}
