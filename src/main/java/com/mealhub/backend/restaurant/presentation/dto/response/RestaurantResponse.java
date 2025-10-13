package com.mealhub.backend.restaurant.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("r_id")
    private final UUID restaurantId;

    @JsonProperty("u_id")
    private final Long userId;

    @JsonProperty("a_id")
    private final UUID addressId;

    @JsonProperty("r_name")
    private final String name;

    @JsonProperty("r_description")
    private final String description;

    @JsonProperty("r_category")
    private final String category;

    @JsonProperty("r_status")
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