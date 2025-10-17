package com.mealhub.backend.cart.infrastructure.repository;

import com.mealhub.backend.cart.domain.entity.CartItem;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import com.mealhub.backend.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query("""
        SELECT c
        FROM CartItem c
        WHERE c.user.id = :userId
            AND c.product.id = :productId
            AND c.status = :status
            AND c.buying = :buying
            AND c.deletedAt IS NULL
        """)
    Optional<CartItem> findActiveCartItem(Long userId, UUID productId, CartItemStatus status, boolean buying);

    @Query("""
        SELECT c
        FROM CartItem c
        WHERE c.user.id = :userId
            AND c.status = :status
            AND c.buying = :buying
            AND c.deletedAt IS NULL
        """)
    Page<CartItem> findActiveCartItems(Long userId, CartItemStatus status, boolean buying, Pageable pageable);

    @Query("""
        SELECT c
        FROM CartItem c
        JOIN FETCH c.product
        WHERE c.id IN :cartItemIds
            AND c.deletedAt IS NULL
        """)
    List<CartItem> findAllWithProductByIdIn(@Param("cartItemIds") List<UUID> cartItemIds);

    List<CartItem> findAllByUserIdAndBuyingTrueAndDeletedAtIsNull(Long userId);
}
