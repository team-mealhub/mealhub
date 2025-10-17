package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartItemUpdateRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Quantity {

        @JsonProperty("ct_quantity")
        @Min(1)
        @Max(1000)
        @Schema(description = "장바구니 수량", example = "2")
        private int quantity;
    }
}
