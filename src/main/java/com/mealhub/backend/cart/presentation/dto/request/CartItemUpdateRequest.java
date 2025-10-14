package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "장바구니 수량", example = "2")
        private int quantity;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Buying {

        @JsonProperty("ct_ids")
        @NotEmpty
        @Schema(description = "장바구니 아이템 ID 리스트",
                example = "[\"00000000-0000-0000-0000-000000000001\", \"00000000-0000-0000-0000-000000000002\"]")
        private List<UUID> cartItemIds;

        @JsonProperty("ct_buying")
        @Schema(description = "장바구니 구매 상태", example = "true")
        private boolean buying;
    }
}
