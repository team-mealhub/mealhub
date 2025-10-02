package com.mealhub.backend.cart.presentation.dto.request;

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
    // TODO: product 연관관계 매핑
    private UUID productId;
    private int quantity;
    private CartItemStatus status;
}
