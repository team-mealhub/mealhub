package com.mealhub.backend.user.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.user.application.service.UserService;
import com.mealhub.backend.user.presentation.dto.request.UserUpdateRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{u_id}")
    public UserResponse getUser(@PathVariable(value = "u_id") Long id) {
        return userService.getUser(id);
    }

    @PutMapping
    public UserResponse updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.updateUser(userDetails.getId(), request);
    }

    @DeleteMapping
    public UserResponse deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(userDetails.getId());
    }
}
