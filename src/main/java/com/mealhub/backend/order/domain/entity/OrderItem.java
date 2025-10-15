package com.mealhub.backend.order.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseAuditEntity {

    @Id
    @Column(name = "o_item_id", columnDefinition = "UUID")
    private UUID oItemId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_info_id", nullable = false)
    private OrderInfo orderInfo;

    @Column(name = "p_id", columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "o_item_product", length = 255, nullable = false)
    private String product;

    @Column(name = "o_item_price", nullable = false)
    private Long price;

    @Column(name = "o_item_quantity", nullable = false)
    private Long quantity;

    // 정적 팩토리 메서드
    public static OrderItem createOrderItem(UUID productId, String product, Long price, Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.oItemId = UUID.randomUUID();
        orderItem.productId = productId;
        orderItem.product = product;
        orderItem.price = price;
        orderItem.quantity = quantity;
        return orderItem;
    }

    // 비즈니스 메서드
    public Long getTotalPrice() {
        return this.price * this.quantity;
    }

    /**
     * 주문 상품 수량 변경
     *
     * <p><b>현재 미사용:</b> 주문 생성 후 수량 변경을 허용하지 않는 정책으로 인해 사용되지 않습니다.
     * 향후 주문 수정 기능이 추가될 경우 활용 가능합니다.</p>
     *
     * @param quantity 변경할 수량 (0보다 커야 함)
     * @throws IllegalArgumentException 수량이 0 이하인 경우
     */
    public void updateQuantity(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        this.quantity = quantity;
    }
}