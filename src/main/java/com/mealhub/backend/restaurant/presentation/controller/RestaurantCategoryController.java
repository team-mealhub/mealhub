package com.mealhub.backend.restaurant.presentation.controller;

import com.mealhub.backend.restaurant.application.service.RestaurantCategoryService;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryPatchRequest;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryRequest;
import com.mealhub.backend.restaurant.presentation.dto.response.RestaurantCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @Valid @RequestBody RestaurantCategoryRequest restaurantCategoryRequest
    ) {

        return restaurantCategoryService.createRestaurantCategory(restaurantCategoryRequest);
    }

    @GetMapping
    @Operation(summary = "가게 분류 전체 조회")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantCategoryResponse> getRestaurantCategories() {

        return restaurantCategoryService.getRestaurantCategories();
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "가게 분류 수정")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantCategoryResponse updateRestaurantCategory(
            @Valid @RequestBody RestaurantCategoryPatchRequest restaurantCategoryPatchRequest,
            @PathVariable UUID categoryId
    ) {

        return restaurantCategoryService.updateRestaurantCategory(restaurantCategoryPatchRequest,
                categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "가게 분류 삭제")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRestaurantCategory(@PathVariable UUID categoryId) {

        restaurantCategoryService.deleteRestaurantCategory(categoryId);
    }
}
