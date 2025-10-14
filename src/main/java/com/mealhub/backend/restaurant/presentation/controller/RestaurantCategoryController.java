package com.mealhub.backend.restaurant.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.restaurant.application.service.RestaurantCategoryService;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantCategoryResponse;
import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Restaurant Category Controller", description = "가게 카테고리 도메인 API")
@RestController
@RequestMapping("/v1/restaurant/category")
@RequiredArgsConstructor
public class RestaurantCategoryController {

    private final RestaurantCategoryService restaurantCategoryService;

    @PostMapping
    @Operation(summary = "가게 분류 추가")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantCategoryResponse createRestaurantCategory(
            @Valid @RequestBody RestaurantCategoryRequest restaurantCategoryRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {

        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();

        return restaurantCategoryService.createRestaurantCategory(restaurantCategoryRequest, role,
                userId);
    }
}
