package com.mealhub.backend.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.product.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {


    @JsonProperty("p_id")
    private UUID id;

    @JsonProperty("r_id")
    private UUID restaurantId;

    @JsonProperty("p_name")
    private String name;

    @JsonProperty("p_description")
    private String description;

    @JsonProperty("p_price")
    private long price;



    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .restaurantId(product.getRestaurant().getRestaurantId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}