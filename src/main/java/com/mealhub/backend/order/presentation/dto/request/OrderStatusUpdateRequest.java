package com.mealhub.backend.order.presentation.dto.request;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    private OrderStatus oStatus;  // 변경할 주문 상태
    private String reason;  // 상태 변경 사유
}