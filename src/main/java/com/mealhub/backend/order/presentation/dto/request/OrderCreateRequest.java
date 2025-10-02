package com.mealhub.backend.order.presentation.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCreateRequest {

    private UUID rId;  // 가게 ID
    private UUID aId;  // 배송 주소 ID
    private String oRequirements;  // 추가 요청사항
    private List<OrderItemRequest> items;  // 주문 상품 목록

    @Data
    public static class OrderItemRequest {
        private UUID pId;  // 상품 ID
        private Long quantity;  // 수량
    }
}