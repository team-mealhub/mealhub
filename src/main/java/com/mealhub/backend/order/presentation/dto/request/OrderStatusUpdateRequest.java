package com.mealhub.backend.order.presentation.dto.request;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "주문 상태 변경 요청")
public class OrderStatusUpdateRequest {

    @Schema(description = "변경할 주문 상태", example = "IN_PROGRESS", required = true,
            allowableValues = {"PENDING", "IN_PROGRESS", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"})
    @NotNull
    private OrderStatus oStatus;

    @Schema(description = "상태 변경 사유", example = "조리 시작", required = false)
    private String reason;
}