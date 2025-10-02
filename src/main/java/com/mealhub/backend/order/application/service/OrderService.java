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

    // 권한 검증: CUSTOMER가 본인 주문인지 확인
    private void validateCustomerOwnership(OrderInfo orderInfo, Long currentUserId) {
        if (!orderInfo.getUserId().equals(currentUserId)) {
            throw new RuntimeException("본인의 주문만 접근할 수 있습니다.");
        }
    }

    // 권한 검증: OWNER가 자신의 가게 주문인지 확인 (TODO: Restaurant 엔티티 구현 후 개선 필요)
    private void validateOwnerRestaurant(OrderInfo orderInfo, UUID ownerRestaurantId) {
        if (!orderInfo.getRestaurantId().equals(ownerRestaurantId)) {
            throw new RuntimeException("본인 가게의 주문만 접근할 수 있습니다.");
        }
    }

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
    public OrderDetailResponse getOrder(UUID orderId, Long currentUserId, String userRole, UUID ownerRestaurantId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // MANAGER는 모든 주문 조회 가능
        if ("MANAGER".equals(userRole)) {
            return OrderDetailResponse.from(orderInfo);
        }

        // CUSTOMER는 본인 주문만 조회 가능
        if ("CUSTOMER".equals(userRole)) {
            validateCustomerOwnership(orderInfo, currentUserId);
            return OrderDetailResponse.from(orderInfo);
        }

        // OWNER는 본인 가게 주문만 조회 가능
        if ("OWNER".equals(userRole)) {
            validateOwnerRestaurant(orderInfo, ownerRestaurantId);
            return OrderDetailResponse.from(orderInfo);
        }

        throw new RuntimeException("권한이 없습니다.");
    }

    // 주문 검색 (역할별 권한 필터 적용)
    public Page<OrderResponse> searchOrders(
            Long userId,
            UUID restaurantId,
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable,
            Long currentUserId,
            String userRole,
            UUID ownerRestaurantId
    ) {
        // 역할별 필터링 적용
        Long filteredUserId = userId;
        UUID filteredRestaurantId = restaurantId;

        // CUSTOMER: 본인 주문만 조회 (userId 강제 설정)
        if ("CUSTOMER".equals(userRole)) {
            filteredUserId = currentUserId;
            filteredRestaurantId = null; // 고객은 레스토랑 필터 무시
        }

        // OWNER: 본인 레스토랑 주문만 조회 (restaurantId 강제 설정)
        if ("OWNER".equals(userRole)) {
            filteredRestaurantId = ownerRestaurantId;
            filteredUserId = null; // 점주는 사용자 필터 무시
        }

        // MANAGER: 모든 파라미터 그대로 사용 (전체 조회 가능)

        Page<OrderInfo> orders = orderInfoRepository.searchOrders(
                filteredUserId, filteredRestaurantId, status, from, to, pageable
        );
        return orders.map(OrderResponse::from);
    }

    // 주문 상태 변경 (OWNER)
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request, UUID ownerRestaurantId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // OWNER는 본인 가게 주문만 상태 변경 가능
        validateOwnerRestaurant(orderInfo, ownerRestaurantId);

        orderInfo.updateStatus(request.getOStatus(), request.getReason());
        return OrderResponse.from(orderInfo);
    }

    // 주문 취소 (CUSTOMER)
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason, Long currentUserId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // CUSTOMER는 본인 주문만 취소 가능
        validateCustomerOwnership(orderInfo, currentUserId);

        orderInfo.cancel(reason);
        return OrderResponse.from(orderInfo);
    }

    // 주문 삭제 (소프트 삭제)
    @Transactional
    public void deleteOrder(UUID orderId, Long deletedBy, String userRole, UUID ownerRestaurantId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // MANAGER는 모든 주문 삭제 가능
        if ("MANAGER".equals(userRole)) {
            orderInfo.delete(deletedBy);
            return;
        }

        // OWNER는 본인 가게 주문만 삭제 가능
        if ("OWNER".equals(userRole)) {
            validateOwnerRestaurant(orderInfo, ownerRestaurantId);
            orderInfo.delete(deletedBy);
            return;
        }

        throw new RuntimeException("권한이 없습니다.");
    }
}
