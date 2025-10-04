package com.mealhub.backend.restaurant.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.restaurant.application.service.RestaurantService;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantResponse;
import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Restaurant Controller", description = "가게 도메인 API")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "가게 등록")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        // TODO : 권한 확인 리팩토링
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        UserRole role = principal.getRole();

        return restaurantService.createRestaurant(restaurantRequest, userDetailsImpl.getUserId(),
                role);
    }
}