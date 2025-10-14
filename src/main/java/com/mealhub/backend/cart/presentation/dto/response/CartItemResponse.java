package com.mealhub.backend.cart.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.cart.domain.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CartItemResponse {

    @JsonProperty("ct_id")
    private UUID cartItemId;

    @JsonProperty("p_id")
    private UUID productId;

    @JsonProperty("ct_quantity")
    private int quantity;

    @JsonProperty("p_price")
    private long price;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getPId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getProduct().getPPrice();
    }
}
