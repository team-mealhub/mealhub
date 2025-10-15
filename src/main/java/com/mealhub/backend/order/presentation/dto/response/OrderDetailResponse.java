package com.mealhub.backend.order.presentation.dto.response;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.OrderItem;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDetailResponse {

    private UUID oInfoId;
    private Long userId;
    private UUID restaurantId;
    private UUID addressId;
    private UUID paymentId;
    private Long total;
    private OrderStatus status;
    private String requirements;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class OrderItemResponse {
        private UUID oItemId;
        private String product;
        private Long price;
        private Long quantity;
        private Long totalPrice;

        public static OrderItemResponse from(OrderItem item) {
            return OrderItemResponse.builder()
                    .oItemId(item.getOItemId())
                    .product(item.getProduct())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getTotalPrice())
                    .build();
        }
    }

    public static OrderDetailResponse from(OrderInfo orderInfo) {
        return OrderDetailResponse.builder()
                .oInfoId(orderInfo.getOInfoId())
                .userId(orderInfo.getUserId())
                .restaurantId(orderInfo.getRestaurantId())
                .addressId(orderInfo.getAddressId())
                .paymentId(orderInfo.getPaymentId())
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