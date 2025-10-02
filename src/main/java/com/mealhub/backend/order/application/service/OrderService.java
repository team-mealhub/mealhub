package com.mealhub.backend.order.application.service;

import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.OrderItem;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.infrastructure.repository.OrderInfoRepository;
import com.mealhub.backend.order.presentation.dto.request.OrderCreateRequest;
import com.mealhub.backend.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.mealhub.backend.order.presentation.dto.response.OrderDetailResponse;
import com.mealhub.backend.order.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderInfoRepository orderInfoRepository;

    // 주문 생성
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        // 주문 정보 생성
        OrderInfo orderInfo = OrderInfo.createOrder(
                userId,
                request.getRId(),
                request.getAId(),
                request.getORequirements()
        );

        // 주문 상품 추가 (실제로는 Product 조회 필요)
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            // TODO: Product 엔티티에서 실제 가격과 상품명 조회
            OrderItem orderItem = OrderItem.createOrderItem(
                    "상품명",  // 실제로는 Product에서 조회
                    10000L,  // 실제로는 Product에서 조회
                    itemRequest.getQuantity()
            );
            orderInfo.addOrderItem(orderItem);
        }

        OrderInfo savedOrder = orderInfoRepository.save(orderInfo);
        return OrderResponse.from(savedOrder);
    }

    // 주문 단건 조회
    public OrderDetailResponse getOrder(UUID orderId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderDetailResponse.from(orderInfo);
    }

    // 주문 검색
    public Page<OrderResponse> searchOrders(
            Long userId,
            UUID restaurantId,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        Page<OrderInfo> orders = orderInfoRepository.searchOrders(
                userId, restaurantId, status, from, to, pageable
        );
        return orders.map(OrderResponse::from);
    }

    // 주문 상태 변경 (OWNER)
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        orderInfo.updateStatus(request.getOStatus(), request.getReason());
        return OrderResponse.from(orderInfo);
    }

    // 주문 취소 (CUSTOMER)
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        orderInfo.cancel(reason);
        return OrderResponse.from(orderInfo);
    }

    // 주문 삭제 (소프트 삭제)
    @Transactional
    public void deleteOrder(UUID orderId, Long deletedBy) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        orderInfo.delete(deletedBy);
    }
}