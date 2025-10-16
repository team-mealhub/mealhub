package com.mealhub.backend.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID oInfoId;
    private Long userId;
    private UUID restaurantId;
    private UUID addressId;
    private Long total;
    private OrderStatus status;
    private String requirements;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static OrderResponse from(OrderInfo orderInfo) {
        return OrderResponse.builder()
                .oInfoId(orderInfo.getOInfoId())
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