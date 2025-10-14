package com.mealhub.backend.restaurant.presentation.dto.response;

import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import java.util.List;
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

    // List 변환 메서드
    public static List<RestaurantCategoryResponse> fromList(
            List<RestaurantCategoryEntity> categories) {
        return categories.stream()
                .map(category -> RestaurantCategoryResponse.from(category.getCategory()))
                .toList();
    }
}