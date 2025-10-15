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

    /**
     * 주문할 장바구니 아이템 조회 (buying=true, 삭제되지 않음, Product FETCH JOIN)
     *
     * @param userId 사용자 ID
     * @return 주문 대상 장바구니 아이템 리스트
     */
    @org.springframework.data.jpa.repository.Query("SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user.id = :userId AND c.buying = true AND c.deletedAt IS NULL")
    java.util.List<CartItem> findByUserIdAndBuyingIsTrueAndDeletedAtIsNull(@org.springframework.data.repository.query.Param("userId") Long userId);
}
