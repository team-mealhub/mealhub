package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class CartItemUpdateRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Quantity {

        @JsonProperty("ct_quantity")
        @Min(1)
        @Max(1000)
        private int quantity;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Buying {

        @JsonProperty("ct_ids")
        @NotEmpty
        private List<UUID> cartItemIds;

        @JsonProperty("ct_buying")
        private boolean buying;
    }
}
