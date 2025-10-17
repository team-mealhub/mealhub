package com.mealhub.backend.order.presentation.dto.response;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.OrderItem;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "주문 상세 응답")
public class OrderDetailResponse {

    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID orderInfoId;

    @Schema(description = "주문자 ID", example = "1")
    private Long userId;

    @Schema(description = "레스토랑 ID", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID restaurantId;

    @Schema(description = "배송 주소 ID", example = "770e8400-e29b-41d4-a716-446655440002")
    private UUID addressId;

    @Schema(description = "주문 총액", example = "25000")
    private Long total;

    @Schema(description = "주문 상태", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "주문 요청사항", example = "문 앞에 놓아주세요")
    private String requirements;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemResponse> items;

    @Schema(description = "주문 생성일시", example = "2025-01-15T14:30:00")
    private LocalDateTime createdAt;

    @Data
    @Builder
    @Schema(description = "주문 상품 정보")
    public static class OrderItemResponse {
        @Schema(description = "주문 상품 ID", example = "880e8400-e29b-41d4-a716-446655440003")
        private UUID orderItemId;

        @Schema(description = "상품명", example = "치킨")
        private String product;

        @Schema(description = "상품 가격", example = "20000")
        private Long price;

        @Schema(description = "주문 수량", example = "2")
        private Long quantity;

        @Schema(description = "총 가격", example = "40000")
        private Long totalPrice;

        public static OrderItemResponse from(OrderItem item) {
            return OrderItemResponse.builder()
                    .orderItemId(item.getOrderItemId())
                    .product(item.getProduct())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getTotalPrice())
                    .build();
        }
    }

    public static OrderDetailResponse from(OrderInfo orderInfo) {
        return OrderDetailResponse.builder()
                .orderInfoId(orderInfo.getOrderInfoId())
                .userId(orderInfo.getUserId())
                .restaurantId(orderInfo.getRestaurantId())
                .addressId(orderInfo.getAddressId())
                .total(orderInfo.getTotal())
                .status(orderInfo.getStatus())
                .requirements(orderInfo.getRequirements())
                .items(orderInfo.getItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(orderInfo.getCreatedAt())
                .build();
    }
}