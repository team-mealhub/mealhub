package com.mealhub.backend.payment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.payment.domain.entity.PaymentLog;
import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentLogResponse {

    @JsonProperty("py_log_id")
    private UUID id;

    @JsonProperty("o_info_id")
    private UUID orderId;

    @JsonProperty("u_id")
    private Long userId;

    @JsonProperty("py_amount")
    private long amount;

    @JsonProperty("py_status")
    private PaymentStatus status;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public PaymentLogResponse(PaymentLog paymentLog) {
        this.id = paymentLog.getId();
        this.orderId = paymentLog.getOrderId();
        this.userId = paymentLog.getUserId();
        this.amount = paymentLog.getAmount();
        this.status = paymentLog.getStatus();
        this.createdAt = paymentLog.getCreatedAt();
    }
}
