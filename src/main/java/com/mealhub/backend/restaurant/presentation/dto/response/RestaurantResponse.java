package com.mealhub.backend.restaurant.presentation.dto.response;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantResponse {

    private final UUID restaurantId;
    private final Long userId;
    private final UUID addressId;
    private final String name;
    private final String description;
    private final String category;
    private final Boolean status;

    public static RestaurantResponse from(RestaurantEntity restaurantEntity) {
        return RestaurantResponse.builder()
                .restaurantId(restaurantEntity.getRestaurantId())
                .userId(restaurantEntity.getUser().getId())
                .addressId(restaurantEntity.getAddress().getId())
                .name(restaurantEntity.getName())
                .description(restaurantEntity.getDescription())
                .category(restaurantEntity.getCategory().getCategory())
                .status(restaurantEntity.getStatus())
                .build();
    }
}
