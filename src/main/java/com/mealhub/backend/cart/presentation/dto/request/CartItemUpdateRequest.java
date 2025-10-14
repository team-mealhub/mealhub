package com.mealhub.backend.cart.presentation.dto.request;

import com.mealhub.backend.cart.domain.enums.CartItemQuantityOperation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class CartItemUpdateRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Quantity {
        private CartItemQuantityOperation operation;
        private int quantity;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Buying {
        List<UUID> ct_ids;
        private boolean buying;
    }
}
