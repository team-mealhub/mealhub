package com.mealhub.backend.restaurant.presentation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantCategoryResponse {

    private final String category;

    public static RestaurantCategoryResponse from(String category) {
        return RestaurantCategoryResponse.builder()
                .category(category)
                .build();
    }
}