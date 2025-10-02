package com.mealhub.backend.cart.domain.entity;

import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
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

    @Column(name = "p_id")
    private UUID productId;

    @Column(name = "ct_quantity")
    private int quantity;

    @Column(name = "ct_status")
    private CartItemStatus status;

    @Column(name = "ct_is_buying")
    private boolean buying;

    private CartItem(int quantity, CartItemStatus status) {
        this.quantity = quantity;
        this.status = status;
        this.buying = false;
    }

    // TODO: product 연관관계 매핑
    public static CartItem createCartItem(CartItemCreateRequest request, User user, UUID productId) {
        CartItem cartItem = new CartItem(
                request.getQuantity(),
                request.getStatus()
        );

        cartItem.user = user;
        cartItem.productId = productId;

        return cartItem;
    }
}