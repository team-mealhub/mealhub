package com.mealhub.backend.cart.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CartItemResponse {
    private UUID ct_id;
    private UUID p_id;
    private int quantity;
    private int price;
}
