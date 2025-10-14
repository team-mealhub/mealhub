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
    private UUID pId;

    @JsonProperty("r_id")
    private UUID restaurantId;

    @JsonProperty("p_name")
    private String pName;

    @JsonProperty("p_description")
    private String pDescription;

    @JsonProperty("p_price")
    private long pPrice;



    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .pId(product.getPId())
                .restaurantId(product.getRestaurant().getRestaurantId())
                .pName(product.getPName())
                .pDescription(product.getPDescription())
                .pPrice(product.getPPrice())
                .build();
    }
}