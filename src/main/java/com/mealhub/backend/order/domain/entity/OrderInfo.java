package com.mealhub.backend.order.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo extends BaseAuditEntity {

    @Id
    @Column(name = "o_info_id", columnDefinition = "UUID")
    private UUID oInfoId;

    @Column(name = "u_id", nullable = false)
    private Long userId;

    @Column(name = "r_id", columnDefinition = "UUID", nullable = false)
    private UUID restaurantId;

    @Column(name = "a_id", columnDefinition = "UUID", nullable = false)
    private UUID addressId;

    @Column(name = "o_total", nullable = false)
    private Long total;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_status", length = 50, nullable = false)
    private OrderStatus status;

    @Column(name = "o_requirements", length = 255)
    private String requirements;

    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL)
    private List<OrderStatusLog> statusLogs = new ArrayList<>();

    // 정적 팩토리 메서드
    public static OrderInfo createOrder(Long userId, UUID restaurantId, UUID addressId, String requirements) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.oInfoId = UUID.randomUUID();
        orderInfo.userId = userId;
        orderInfo.restaurantId = restaurantId;
        orderInfo.addressId = addressId;
        orderInfo.status = OrderStatus.PENDING;
        orderInfo.requirements = requirements;
        orderInfo.total = 0L;
        return orderInfo;
    }

    // 비즈니스 메서드
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.setOrderInfo(this);
        calculateTotal();
    }

    public void calculateTotal() {
        this.total = items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void updateStatus(OrderStatus newStatus, String reason) {
        OrderStatus oldStatus = this.status;
        this.status = newStatus;

        // 상태 로그 생성
        OrderStatusLog log = OrderStatusLog.createLog(this, oldStatus, newStatus, reason);
        this.statusLogs.add(log);
    }

    public void cancel(String reason) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 주문만 취소할 수 있습니다.");
        }
        updateStatus(OrderStatus.CANCELLED, reason);
    }

    public void delete(Long deletedBy) {
        this.deletedAt = java.time.LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}