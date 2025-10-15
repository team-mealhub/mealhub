package com.mealhub.backend.order.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "장바구니에서 주문 생성 요청")
public class OrderCreateFromCartRequest {

    @NotNull(message = "배송지 ID는 필수입니다")
    @Schema(description = "배송지 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID aId;

    @NotNull(message = "결제 ID는 필수입니다")
    @Schema(description = "결제 ID", example = "650e8400-e29b-41d4-a716-446655440000")
    private UUID paymentId;

    @Schema(description = "주문 요청사항", example = "문 앞에 놔주세요")
    private String oRequirements;
}
