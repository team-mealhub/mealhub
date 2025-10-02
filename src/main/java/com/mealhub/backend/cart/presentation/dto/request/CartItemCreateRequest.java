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
    private UUID p_id;
    private int quantity;
    private CartItemStatus status;
    private boolean buying;
}
