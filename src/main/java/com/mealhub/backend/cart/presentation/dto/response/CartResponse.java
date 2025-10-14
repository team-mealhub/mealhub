package com.mealhub.backend.cart.presentation.dto.response;

import com.mealhub.backend.global.presentation.dto.PageResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class CartResponse {
    private PageResult<CartItemResponse> items;
    private long totalPrice;

    public CartResponse(Page<CartItemResponse> items, long totalPrice) {
        this.items = PageResult.of(items);
        this.totalPrice = totalPrice;
    }
}
