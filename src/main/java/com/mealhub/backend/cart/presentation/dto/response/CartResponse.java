package com.mealhub.backend.cart.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class CartResponse {
    private Page<CartItemResponse> items;
    private long totalPrice;
}
