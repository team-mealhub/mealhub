package com.mealhub.backend.order.domain.entity;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order_status_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OrderStatusLog {

    @Id
    @Column(name = "o_status_id", columnDefinition = "UUID")
    private UUID oStatusId;

    @Column(name = "o_info_id", nullable = false)
    private UUID orderInfoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_prev_status", length = 100)
    private OrderStatus prevStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_curr_status", length = 100, nullable = false)
    private OrderStatus currStatus;

    @Column(name = "o_reason", length = 255)
    private String reason;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    // 정적 팩토리 메서드
    public static OrderStatusLog createLog(UUID orderInfoId, OrderStatus prevStatus, OrderStatus currStatus, String reason) {
        OrderStatusLog log = new OrderStatusLog();
        log.oStatusId = UUID.randomUUID();
        log.orderInfoId = orderInfoId;
        log.prevStatus = prevStatus;
        log.currStatus = currStatus;
        log.reason = reason;
        return log;
    }
}