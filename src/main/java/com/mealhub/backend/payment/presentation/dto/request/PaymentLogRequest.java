package com.mealhub.backend.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.payment.domain.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "유저 ID", example = "1")
        private Long userId;

        @JsonProperty("o_info_id")
        @Schema(description = "주문 정보 ID", example = "00000000-0000-0000-0000-000000000001")
        private UUID orderId;

        @JsonProperty("py_status")
        @Schema(description = "결제 상태", example = "COMPLETED")
        private PaymentStatus status;

        @AssertTrue
        private boolean isUserIdOrOrderIdProvided() {
            return userId != null || orderId != null;
        }
    }
}
