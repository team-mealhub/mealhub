package com.mealhub.backend.order.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.domain.exception.OrderCancelException;
import com.mealhub.backend.order.domain.exception.OrderStatusTransitionException;
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

    @Column(name = "py_log_id", columnDefinition = "UUID")
    private UUID paymentId;

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
    public static OrderInfo createOrder(Long userId, UUID restaurantId, UUID addressId, String requirements, UUID paymentId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.oInfoId = UUID.randomUUID();
        orderInfo.userId = userId;
        orderInfo.restaurantId = restaurantId;
        orderInfo.addressId = addressId;
        orderInfo.paymentId = paymentId;
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
        validateStatusTransition(this.status, newStatus);

        OrderStatus oldStatus = this.status;
        this.status = newStatus;

        // 상태 로그 생성
        OrderStatusLog log = OrderStatusLog.createLog(this, oldStatus, newStatus, reason);
        this.statusLogs.add(log);
    }

    /**
     * 주문 상태 전이 규칙 검증
     * - PENDING → IN_PROGRESS, CANCELLED
     * - IN_PROGRESS → OUT_FOR_DELIVERY, CANCELLED
     * - OUT_FOR_DELIVERY → DELIVERED, CANCELLED
     * - DELIVERED, CANCELLED → (변경 불가)
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // 동일한 상태로의 전이는 허용
        }

        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.IN_PROGRESS && newStatus != OrderStatus.CANCELLED) {
                    throw new OrderStatusTransitionException("Order.Status.Transition.FromPending");
                }
                break;
            case IN_PROGRESS:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY && newStatus != OrderStatus.CANCELLED) {
                    throw new OrderStatusTransitionException("Order.Status.Transition.FromInProgress");
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new OrderStatusTransitionException("Order.Status.Transition.FromOutForDelivery");
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new OrderStatusTransitionException("Order.Status.Transition.FinalState");
            default:
                throw new OrderStatusTransitionException("Order.Status.Transition.Unknown");
        }
    }

    public void cancel(String reason) {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderCancelException("Order.Cancel.OnlyPending");
        }
        updateStatus(OrderStatus.CANCELLED, reason);
    }

    public void delete(Long deletedBy) {
        this.deletedAt = java.time.LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}