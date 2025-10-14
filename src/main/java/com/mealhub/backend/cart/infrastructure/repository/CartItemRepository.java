package com.mealhub.backend.cart.infrastructure.repository;

import com.mealhub.backend.cart.domain.entity.CartItem;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Page<CartItem> findByUserIdAndStatusAndBuyingIsFalseAndDeletedAtIsNull(Long userId, CartItemStatus status, Pageable pageable);
}
