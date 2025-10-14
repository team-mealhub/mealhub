package com.mealhub.backend.cart.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
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
    private UUID productId;

    @JsonProperty("ct_quantity")
    private int quantity;

    @JsonProperty("ct_status")
    private CartItemStatus status;

    @JsonProperty("ct_buying")
    private boolean buying;
}
