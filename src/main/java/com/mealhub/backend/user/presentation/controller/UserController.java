package com.mealhub.backend.user.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import com.mealhub.backend.user.application.service.UserService;
import com.mealhub.backend.user.presentation.dto.request.UserUpdateRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 API", description = "유저 정보 조회, 수정, 탈퇴 기능 제공")
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "현재 유저 정보 조회",
            description = "로그인한 유저의 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "현재 유저 정보 조회 성공")
    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUser(userDetails.getId());
    }

    @Operation(
            summary = "유저 정보 조회",
            description = "유저 ID로 유저 정보를 조회합니다."
    )
    @Parameters({
            @Parameter(name = "u_id", description = "조회할 유저의 ID", in = ParameterIn.PATH, required = true)
    })
    @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/{u_id}")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponse getUser(@PathVariable(value = "u_id") Long id) {
        return userService.getUser(id);
    }

    @Operation(
            summary = "유저 정보 수정",
            description = "로그인한 유저의 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "유저 정보 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터" ,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PutMapping
    public UserResponse updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.updateUser(userDetails.getId(), request);
    }

    @Operation(
            summary = "유저 탈퇴",
            description = "로그인한 유저를 탈퇴 처리합니다."
    )
    @ApiResponse(responseCode = "200", description = "유저 탈퇴 성공")
    @DeleteMapping
    public UserResponse deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(userDetails.getId());
    }
}
