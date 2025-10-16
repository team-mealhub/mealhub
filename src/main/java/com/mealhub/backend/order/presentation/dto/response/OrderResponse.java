package com.mealhub.backend.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "주문 응답")
public class OrderResponse {

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

    @Schema(description = "주문 생성일시", example = "2025-01-15T14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static OrderResponse from(OrderInfo orderInfo) {
        return OrderResponse.builder()
                .orderInfoId(orderInfo.getOrderInfoId())
                .userId(orderInfo.getUserId())
                .restaurantId(orderInfo.getRestaurantId())
                .addressId(orderInfo.getAddressId())
                .total(orderInfo.getTotal())
                .status(orderInfo.getStatus())
                .requirements(orderInfo.getRequirements())
                .createdAt(orderInfo.getCreatedAt())
                .build();
    }
}