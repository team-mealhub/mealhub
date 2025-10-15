package com.mealhub.backend.order.application.service;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.address.infrastructure.repository.AddressRepository;
import com.mealhub.backend.cart.domain.entity.CartItem;
import com.mealhub.backend.cart.domain.exception.CartItemForbiddenException;
import com.mealhub.backend.cart.infrastructure.repository.CartItemRepository;
import com.mealhub.backend.order.application.event.publisher.OrderEventPublisher;
import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.OrderItem;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.domain.event.OrderCreatedEvent;
import com.mealhub.backend.order.domain.event.OrderDeletedEvent;
import com.mealhub.backend.order.domain.exception.EmptyCartItemException;
import com.mealhub.backend.order.domain.exception.OrderForbiddenException;
import com.mealhub.backend.order.domain.exception.OrderNotFoundException;
import com.mealhub.backend.order.infrastructure.repository.OrderInfoRepository;
import com.mealhub.backend.order.presentation.dto.request.OrderCreateRequest;
import com.mealhub.backend.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.mealhub.backend.order.presentation.dto.response.OrderDetailResponse;
import com.mealhub.backend.order.presentation.dto.response.OrderResponse;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.user.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderInfoRepository orderInfoRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final OrderEventPublisher orderEventPublisher;

    // 권한 검증: CUSTOMER가 본인 주문인지 확인
    private void validateCustomerOwnership(OrderInfo orderInfo, Long currentUserId) {
        if (!orderInfo.getUserId().equals(currentUserId)) {
            throw new OrderForbiddenException("Order.Forbidden.Customer");
        }
    }

    // 권한 검증: OWNER가 자신의 가게 주문인지 확인
    private void validateOwnerRestaurant(OrderInfo orderInfo, Long currentUserId) {
        RestaurantEntity restaurant = restaurantRepository.findByIdWithUser(orderInfo.getRestaurantId())
                .orElseThrow(() -> new OrderNotFoundException("Restaurant.NotFound"));

        if (!restaurant.getUser().getId().equals(currentUserId)) {
            throw new OrderForbiddenException("Order.Forbidden.Owner");
        }
    }

    // 주문 생성
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        // 1. Restaurant 검증 (존재 여부)
        RestaurantEntity restaurant = restaurantRepository.findById(request.getRId())
                .orElseThrow(() -> new OrderNotFoundException("Restaurant.NotFound"));

        // 2. Address 검증 (존재 여부 및 소유권)
        Address address = addressRepository.findById(request.getAId())
                .orElseThrow(() -> new OrderNotFoundException("Address.NotFound"));

        if (!address.getUser().getId().equals(userId)) {
            throw new OrderForbiddenException("Address.Forbidden.NotOwned");
        }

        // 3. 장바구니 아이템 조회 및 검증
        List<CartItem> cartItems = cartItemRepository.findAllWithProductByIdIn(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new EmptyCartItemException();
        }

        validateCartItemsOwnership(cartItems, userId);

        // 4. 주문 정보 생성
        OrderInfo orderInfo = OrderInfo.createOrder(
                userId,
                request.getRId(),
                request.getAId(),
                request.getORequirements()
        );

        // 5. 주문 상품 추가 (Product 엔티티에서 실제 가격과 상품명 조회)
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = OrderItem.createOrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    (long) cartItem.getQuantity()
            );

            orderInfo.addOrderItem(orderItem);
        }

        OrderInfo savedOrder = orderInfoRepository.save(orderInfo);

        List<UUID> cartItemIds = cartItems.stream().map(CartItem::getId).toList();
        orderEventPublisher.publish(new OrderCreatedEvent(savedOrder.getOInfoId(), userId, cartItemIds, savedOrder.getTotal()));

        return OrderResponse.from(savedOrder);
    }

    // 주문 단건 조회
    public OrderDetailResponse getOrder(UUID orderId, Long currentUserId, UserRole userRole) {
        OrderInfo orderInfo = orderInfoRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order.NotFound"));

        // MANAGER는 모든 주문 조회 가능
        if (UserRole.ROLE_MANAGER.equals(userRole)) {
            return OrderDetailResponse.from(orderInfo);
        }

        // CUSTOMER는 본인 주문만 조회 가능
        if (UserRole.ROLE_CUSTOMER.equals(userRole)) {
            validateCustomerOwnership(orderInfo, currentUserId);
            return OrderDetailResponse.from(orderInfo);
        }

        // OWNER는 본인 가게 주문만 조회 가능
        if (UserRole.ROLE_OWNER.equals(userRole)) {
            validateOwnerRestaurant(orderInfo, currentUserId);
            return OrderDetailResponse.from(orderInfo);
        }

        throw new OrderForbiddenException("Order.Forbidden.NoAuth");
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
            UserRole userRole
    ) {
        // 역할별 필터링 적용
        Long filteredUserId = userId;
        List<UUID> filteredRestaurantIds = restaurantId != null ? List.of(restaurantId) : null;

        // CUSTOMER: 본인 주문만 조회 (userId 강제 설정)
        if (UserRole.ROLE_CUSTOMER.equals(userRole)) {
            filteredUserId = currentUserId;
            filteredRestaurantIds = null; // 고객은 레스토랑 필터 무시
        }

        // OWNER: 본인 레스토랑 주문만 조회
        if (UserRole.ROLE_OWNER.equals(userRole)) {
            filteredUserId = null; // 점주는 사용자 필터 무시

            // restaurantId가 지정된 경우, 해당 레스토랑의 소유자인지 확인
            if (restaurantId != null) {
                RestaurantEntity restaurant = restaurantRepository.findByIdWithUser(restaurantId)
                        .orElseThrow(() -> new OrderNotFoundException("Restaurant.NotFound"));

                if (!restaurant.getUser().getId().equals(currentUserId)) {
                    throw new OrderForbiddenException("Order.Forbidden.Owner");
                }

                filteredRestaurantIds = List.of(restaurantId);
            } else {
                // restaurantId가 없으면 본인 소유 모든 레스토랑의 주문 조회
                List<RestaurantEntity> ownedRestaurants = restaurantRepository.findByUser_Id(currentUserId);

                if (ownedRestaurants.isEmpty()) {
                    // 소유한 레스토랑이 없으면 빈 결과 반환
                    filteredRestaurantIds = List.of();
                } else {
                    // 소유한 모든 레스토랑 ID 추출
                    filteredRestaurantIds = ownedRestaurants.stream()
                            .map(RestaurantEntity::getRestaurantId)
                            .toList();
                }
            }
        }

        // MANAGER: 모든 파라미터 그대로 사용 (전체 조회 가능)

        Page<OrderInfo> orders = orderInfoRepository.searchOrders(
                filteredUserId, filteredRestaurantIds, status, from, to, pageable
        );
        return orders.map(OrderResponse::from);
    }

    // 주문 상태 변경 (OWNER)
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request, Long currentUserId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order.NotFound"));

        // OWNER는 본인 가게 주문만 상태 변경 가능
        validateOwnerRestaurant(orderInfo, currentUserId);

        orderInfo.updateStatus(request.getOStatus(), request.getReason());
        return OrderResponse.from(orderInfo);
    }

    // 주문 취소 (CUSTOMER)
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason, Long currentUserId) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order.NotFound"));

        // CUSTOMER는 본인 주문만 취소 가능
        validateCustomerOwnership(orderInfo, currentUserId);

        orderInfo.cancel(reason);

        orderEventPublisher.publish(new OrderDeletedEvent(orderId, currentUserId, orderInfo.getTotal()));
        return OrderResponse.from(orderInfo);
    }

    // 주문 삭제 (소프트 삭제)
    @Transactional
    public void deleteOrder(UUID orderId, Long deletedBy, UserRole userRole) {
        OrderInfo orderInfo = orderInfoRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order.NotFound"));

        // MANAGER는 모든 주문 삭제 가능
        if (UserRole.ROLE_MANAGER.equals(userRole)) {
            orderInfo.delete(deletedBy);
            orderEventPublisher.publish(new OrderDeletedEvent(orderInfo.getOInfoId(), orderInfo.getUserId(), orderInfo.getTotal()));
            return;
        }

        // OWNER는 본인 가게 주문만 삭제 가능
        if (UserRole.ROLE_OWNER.equals(userRole)) {
            validateOwnerRestaurant(orderInfo, deletedBy);
            orderInfo.delete(deletedBy);
            orderEventPublisher.publish(new OrderDeletedEvent(orderInfo.getOInfoId(), orderInfo.getUserId(), orderInfo.getTotal()));
            return;
        }

        throw new OrderForbiddenException("Order.Forbidden.NoAuth");
    }

    private void validateCartItemsOwnership(List<CartItem> cartItems, Long userId) {
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getUser().getId().equals(userId)) {
                throw new CartItemForbiddenException();
            }
        }
    }
}
