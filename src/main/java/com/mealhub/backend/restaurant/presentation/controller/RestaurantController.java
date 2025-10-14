package com.mealhub.backend.restaurant.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.restaurant.application.service.RestaurantService;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantResponse;
import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Restaurant Controller", description = "가게 도메인 API")
@RestController
@RequestMapping("/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @Operation(summary = "가게 등록")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();

        return restaurantService.createRestaurant(restaurantRequest, userId, role);
    }

    @GetMapping("/{restaurantId}")
    @Operation(summary = "가게 단건 조회")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantResponse getRestaurant(@PathVariable UUID restaurantId) {

        return restaurantService.getRestaurant(restaurantId);
    }

    @PatchMapping("/{restaurantId}")
    @Operation(summary = "가게 수정")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantResponse updateRestaurant(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();

        return restaurantService.updateRestaurant(restaurantId, restaurantRequest, userId, role);
    }

    // 가게 삭제
    @DeleteMapping("/{restaurantId}")
    @Operation(summary = "가게 삭제")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRestaurant(
            @PathVariable UUID restaurantId,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();

        restaurantService.deleteRestaurant(restaurantId, userId, role);
    }

    @GetMapping("/search")
    @Operation(summary = "가게 검색")
    @ResponseStatus(HttpStatus.OK)
    public Page<RestaurantResponse> searchRestaurants(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("page") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc
    ) {
        return restaurantService.searchRestaurants(keyword, page - 1, size, sortBy, isAsc);
    }

    // 가게 상태 변경
    @PostMapping("/status/{restaurantId}")
    @Operation(summary = "가게 상태 변경 (영업중 / 준비중)")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantResponse changeRestaurantStatus(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody RestaurantRequest restaurantRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    ) {
        UserRole role = userDetailsImpl.getRole();
        Long userId = userDetailsImpl.getId();

        return restaurantService.changeRestaurantStatus(restaurantId, restaurantRequest, userId,
                role);
    }
}