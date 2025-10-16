package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartItemCreateRequest {

    @JsonProperty("p_id")
    @NotNull
    @Schema(description = "상품 ID", example = "00000000-0000-0000-0000-000000000001")
    private UUID productId;

    @JsonProperty("ct_quantity")
    @Min(1)
    @Max(1000)
    @Schema(description = "장바구니 수량", example = "1")
    private int quantity;

    @JsonProperty("ct_status")
    @NotNull
    @Schema(description = "장바구니 상태", example = "CART")
    private CartItemStatus status;
}
