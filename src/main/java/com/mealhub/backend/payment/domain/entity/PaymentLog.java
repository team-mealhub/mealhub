package com.mealhub.backend.payment.domain.entity;

import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import com.mealhub.backend.payment.presentation.dto.request.PaymentLogRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "p_payment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "py_log_id")
    private UUID id;

    @Column(name = "o_info_id", nullable = false)
    private UUID orderId;

    @Column(name= "u_id", nullable = false)
    private Long userId;

    @Column(name = "py_amount", nullable = false)
    private long amount;

    @Column(name = "py_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PaymentLog(UUID orderId, Long userId, long amount, PaymentStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static PaymentLog create(PaymentLogRequest.Create request) {
        return new PaymentLog(
                request.getOrderId(),
                request.getUserId(),
                request.getAmount(),
                request.getStatus()
        );
    }
}
