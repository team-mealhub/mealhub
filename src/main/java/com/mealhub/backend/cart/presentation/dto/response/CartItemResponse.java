package com.mealhub.backend.cart.presentation.dto.response;

import com.mealhub.backend.cart.domain.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CartItemResponse {
    private UUID ct_id;
    private UUID p_id;
    private int quantity;
    private long price;

    public CartItemResponse(CartItem cartItem) {
        this.ct_id = cartItem.getId();
        this.p_id = cartItem.getProduct().getPId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getProduct().getPPrice() * cartItem.getQuantity();
    }
}
