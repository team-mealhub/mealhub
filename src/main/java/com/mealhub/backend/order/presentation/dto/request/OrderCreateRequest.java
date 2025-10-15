package com.mealhub.backend.order.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCreateRequest {

    @NotNull
    private UUID rId;  // 가게 ID

    @NotNull
    private UUID aId;  // 배송 주소 ID

    private String oRequirements;  // 추가 요청사항

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;  // 주문 상품 목록

    @Data
    public static class OrderItemRequest {
        @NotNull
        private UUID pId;  // 상품 ID

        @NotNull
        @Min(1)
        private Long quantity;  // 수량
    }
}