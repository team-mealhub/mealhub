package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
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
    private UUID productId;

    @JsonProperty("ct_quantity")
    @Min(1)
    @Max(1000)
    private int quantity;

    @JsonProperty("ct_status")
    @NotNull
    private CartItemStatus status;

    @JsonProperty("ct_buying")
    private boolean buying;
}
