package com.mealhub.backend.user.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtUtil;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import com.mealhub.backend.user.application.dto.SignInResult;
import com.mealhub.backend.user.application.service.AuthService;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "회원가입, 로그인 기능 제공")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 유저를 등록합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/signUp")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody UserSignUpRequest request) {
        authService.signUp(request);
    }

    @Operation(
            summary = "로그인",
            description = "유저 인증 후 JWT 토큰을 헤더에 담아 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            headers = {@Header(name = JwtUtil.AUTHORIZATION_HEADER, description = "JWT 토큰", schema = @Schema(type = "string"))}
    )
    @PostMapping("/signIn")
    public ResponseEntity<UserResponse> signIn(@RequestBody UserSignInRequest request) {
        SignInResult result = authService.signIn(request);

        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION_HEADER, result.getToken())
                .body(result.getUserResponse());
    }
}
