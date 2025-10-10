package com.mealhub.backend.cart.domain.entity;

import com.mealhub.backend.cart.domain.enums.CartItemQuantityOperation;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import com.mealhub.backend.cart.domain.exception.CartItemForbiddenException;
import com.mealhub.backend.cart.domain.exception.CartItemInvalidQuantityException;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "p_cart_item")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartItem extends BaseAuditEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ct_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id")
    private Product product;

    @Column(name = "ct_quantity")
    private int quantity;

    @Column(name = "ct_status")
    private CartItemStatus status;

    @Column(name = "ct_is_buying")
    private boolean buying;

    private CartItem(int quantity, CartItemStatus status, boolean buying) {
        this.quantity = quantity;
        this.status = status;
        this.buying = buying;
    }

    public static CartItem createCartItem(CartItemCreateRequest request, User user, Product product) {
        CartItem cartItem = new CartItem(
                request.getQuantity(),
                request.getStatus(),
                request.isBuying()
        );

        cartItem.user = user;
        cartItem.product = product;

        return cartItem;
    }

    public void updateQuantity(CartItemQuantityOperation operation, int quantity) {
        if (operation == CartItemQuantityOperation.INCREASE) {
            this.quantity += quantity;
        } else if (operation == CartItemQuantityOperation.DECREASE) {
            if ((this.quantity - quantity) < 1) {
                throw CartItemInvalidQuantityException.tooLow();
            }
            this.quantity -= quantity;
        }
    }

    public void updateBuying(boolean buying) {
        this.buying = buying;
    }

    public void validateOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CartItemForbiddenException();
        }
    }
}