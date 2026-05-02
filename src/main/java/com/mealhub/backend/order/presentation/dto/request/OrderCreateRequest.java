package com.mealhub.backend.order.presentation.dto.request;

import com.mealhub.backend.global.domain.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "주문 생성 요청")
public class OrderCreateRequest {

    @Schema(description = "레스토랑 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @NotNull
    private UUID rId;

    @Schema(description = "배송 주소 ID", example = "660e8400-e29b-41d4-a716-446655440001", required = true)
    @NotNull
    private UUID aId;

    @NoXss
    @Schema(description = "주문 요청사항", example = "문 앞에 놓아주세요", required = false)
    private String oRequirements;

    @Schema(description = "장바구니 아이템 ID 목록", example = "[\"770e8400-e29b-41d4-a716-446655440002\"]", required = true)
    @NotEmpty
    private List<UUID> cartItemIds;
}