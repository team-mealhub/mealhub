package com.mealhub.backend.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class PaymentLogRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        @JsonProperty("o_info_id")
        private UUID orderId;

        @JsonProperty("u_id")
        private Long userId;

        @JsonProperty("py_amount")
        private long amount;

        @JsonProperty("py_status")
        private PaymentStatus status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Search {

        @JsonProperty("u_id")
        private Long userId;

        @JsonProperty("o_info_id")
        private UUID orderId;

        @JsonProperty("py_status")
        private PaymentStatus status;

        @AssertTrue
        private boolean isUserIdOrOrderIdProvided() {
            return userId != null || orderId != null;
        }
    }
}
