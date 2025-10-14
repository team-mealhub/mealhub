package com.mealhub.backend.order.application.service;

import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.order.domain.exception.OrderForbiddenException;
import com.mealhub.backend.order.domain.entity.OrderInfo;
import com.mealhub.backend.order.domain.entity.OrderItem;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.infrastructure.repository.OrderInfoRepository;
import com.mealhub.backend.order.presentation.dto.request.OrderCreateRequest;
import com.mealhub.backend.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.mealhub.backend.order.presentation.dto.response.OrderDetailResponse;
import com.mealhub.backend.order.presentation.dto.response.OrderResponse;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

    @Mock
    private OrderInfoRepository orderInfoRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private com.mealhub.backend.address.infrastructure.repository.AddressRepository addressRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        // given
        Long userId = 1L;
        UUID restaurantId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        OrderCreateRequest.OrderItemRequest itemRequest = new OrderCreateRequest.OrderItemRequest();
        itemRequest.setPId(productId);
        itemRequest.setQuantity(2L);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setRId(restaurantId);
        request.setAId(addressId);
        request.setORequirements("빨리 배달해주세요");
        request.setItems(List.of(itemRequest));

        // Restaurant Mock 설정
        RestaurantEntity restaurant = mock(RestaurantEntity.class);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Address Mock 설정 (본인 주소)
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        com.mealhub.backend.address.domain.entity.Address address = mock(com.mealhub.backend.address.domain.entity.Address.class);
        when(address.getUser()).thenReturn(user);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // Product Mock 설정
        Product product = mock(Product.class);
        when(product.getPName()).thenReturn("치킨");
        when(product.getPPrice()).thenReturn(20000L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        OrderInfo orderInfo = OrderInfo.createOrder(userId, restaurantId, addressId, request.getORequirements());
        when(orderInfoRepository.save(any(OrderInfo.class))).thenReturn(orderInfo);

        // when
        OrderResponse response = orderService.createOrder(userId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOInfoId()).isEqualTo(orderInfo.getOInfoId());
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productRepository, times(1)).findById(productId);
        verify(orderInfoRepository, times(1)).save(any(OrderInfo.class));
    }

    @Test
    @DisplayName("주문 단건 조회 - 성공 (MANAGER)")
    void getOrder_Success_Manager() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        UserRole userRole = UserRole.ROLE_MANAGER;

        OrderInfo orderInfo = OrderInfo.createOrder(2L, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when
        OrderDetailResponse response = orderService.getOrder(orderId, currentUserId, userRole);

        // then
        assertThat(response).isNotNull();
        verify(orderInfoRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("주문 단건 조회 - 성공 (CUSTOMER 본인 주문)")
    void getOrder_Success_CustomerOwn() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        UserRole userRole = UserRole.ROLE_CUSTOMER;

        OrderInfo orderInfo = OrderInfo.createOrder(currentUserId, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when
        OrderDetailResponse response = orderService.getOrder(orderId, currentUserId, userRole);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("주문 단건 조회 - 실패 (CUSTOMER 타인 주문)")
    void getOrder_Fail_CustomerOthers() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        UserRole userRole = UserRole.ROLE_CUSTOMER;

        OrderInfo orderInfo = OrderInfo.createOrder(999L, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId, currentUserId, userRole))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }

    @Test
    @DisplayName("주문 단건 조회 - 실패 (OWNER 타 가게 주문)")
    void getOrder_Fail_OwnerOtherRestaurant() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        Long otherUserId = 999L;
        UserRole userRole = UserRole.ROLE_OWNER;
        UUID otherRestaurantId = UUID.randomUUID();

        OrderInfo orderInfo = OrderInfo.createOrder(2L, otherRestaurantId, UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(otherUserId);

        RestaurantEntity otherRestaurant = mock(RestaurantEntity.class);
        when(otherRestaurant.getUser()).thenReturn(otherUser);
        when(restaurantRepository.findById(otherRestaurantId)).thenReturn(Optional.of(otherRestaurant));

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId, currentUserId, userRole))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }

    @Test
    @DisplayName("주문 단건 조회 - 실패 (주문 없음)")
    void getOrder_Fail_NotFound() {
        // given
        UUID orderId = UUID.randomUUID();
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId, 1L, UserRole.ROLE_MANAGER))
                .isInstanceOf(NotFoundException.class)
                ;
    }

    @Test
    @DisplayName("주문 검색 - 성공 (CUSTOMER 자동 필터링)")
    void searchOrders_Success_CustomerFiltered() {
        // given
        Long currentUserId = 1L;
        UserRole userRole = UserRole.ROLE_CUSTOMER;
        Pageable pageable = PageRequest.of(0, 10);

        OrderInfo orderInfo = OrderInfo.createOrder(currentUserId, UUID.randomUUID(), UUID.randomUUID(), null);
        Page<OrderInfo> page = new PageImpl<>(List.of(orderInfo));

        when(orderInfoRepository.searchOrders(eq(currentUserId), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        // when
        Page<OrderResponse> result = orderService.searchOrders(
                999L, UUID.randomUUID(), null, null, null, pageable, currentUserId, userRole
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(orderInfoRepository).searchOrders(eq(currentUserId), isNull(), isNull(), isNull(), isNull(), eq(pageable));
    }

    @Test
    @DisplayName("주문 검색 - 성공 (OWNER 자동 필터링)")
    void searchOrders_Success_OwnerFiltered() {
        // given
        Long currentUserId = 1L;
        UserRole userRole = UserRole.ROLE_OWNER;
        UUID ownerRestaurantId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        OrderInfo orderInfo = OrderInfo.createOrder(2L, ownerRestaurantId, UUID.randomUUID(), null);
        Page<OrderInfo> page = new PageImpl<>(List.of(orderInfo));

        User ownerUser = mock(User.class);
        when(ownerUser.getId()).thenReturn(currentUserId);

        RestaurantEntity ownerRestaurant = mock(RestaurantEntity.class);
        when(ownerRestaurant.getUser()).thenReturn(ownerUser);
        when(restaurantRepository.findById(ownerRestaurantId)).thenReturn(Optional.of(ownerRestaurant));

        when(orderInfoRepository.searchOrders(isNull(), eq(ownerRestaurantId), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        // when
        Page<OrderResponse> result = orderService.searchOrders(
                null, ownerRestaurantId, null, null, null, pageable, currentUserId, userRole
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        verify(orderInfoRepository).searchOrders(isNull(), eq(ownerRestaurantId), isNull(), isNull(), isNull(), eq(pageable));
    }

    @Test
    @DisplayName("주문 상태 변경 - 성공 (OWNER)")
    void updateOrderStatus_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        UUID ownerRestaurantId = UUID.randomUUID();

        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setOStatus(OrderStatus.IN_PROGRESS);
        request.setReason("조리 시작");

        OrderInfo orderInfo = OrderInfo.createOrder(2L, ownerRestaurantId, UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        User ownerUser = mock(User.class);
        when(ownerUser.getId()).thenReturn(currentUserId);

        RestaurantEntity ownerRestaurant = mock(RestaurantEntity.class);
        when(ownerRestaurant.getUser()).thenReturn(ownerUser);
        when(restaurantRepository.findById(ownerRestaurantId)).thenReturn(Optional.of(ownerRestaurant));

        // when
        OrderResponse response = orderService.updateOrderStatus(orderId, request, currentUserId);

        // then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("주문 상태 변경 - 실패 (타 가게 주문)")
    void updateOrderStatus_Fail_OtherRestaurant() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        Long otherUserId = 999L;
        UUID otherRestaurantId = UUID.randomUUID();

        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setOStatus(OrderStatus.IN_PROGRESS);
        request.setReason("조리 시작");

        OrderInfo orderInfo = OrderInfo.createOrder(2L, otherRestaurantId, UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(otherUserId);

        RestaurantEntity otherRestaurant = mock(RestaurantEntity.class);
        when(otherRestaurant.getUser()).thenReturn(otherUser);
        when(restaurantRepository.findById(otherRestaurantId)).thenReturn(Optional.of(otherRestaurant));

        // when & then
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request, currentUserId))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }

    @Test
    @DisplayName("주문 취소 - 성공 (CUSTOMER)")
    void cancelOrder_Success() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        String reason = "변심";

        OrderInfo orderInfo = OrderInfo.createOrder(currentUserId, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when
        OrderResponse response = orderService.cancelOrder(orderId, reason, currentUserId);

        // then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 - 실패 (타인 주문)")
    void cancelOrder_Fail_OthersOrder() {
        // given
        UUID orderId = UUID.randomUUID();
        Long currentUserId = 1L;
        String reason = "변심";

        OrderInfo orderInfo = OrderInfo.createOrder(999L, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, reason, currentUserId))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }

    @Test
    @DisplayName("주문 삭제 - 성공 (MANAGER)")
    void deleteOrder_Success_Manager() {
        // given
        UUID orderId = UUID.randomUUID();
        Long deletedBy = 1L;
        UserRole userRole = UserRole.ROLE_MANAGER;

        OrderInfo orderInfo = OrderInfo.createOrder(2L, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when
        orderService.deleteOrder(orderId, deletedBy, userRole);

        // then
        assertThat(orderInfo.getDeletedAt()).isNotNull();
        assertThat(orderInfo.getDeletedBy()).isEqualTo(deletedBy);
    }

    @Test
    @DisplayName("주문 삭제 - 성공 (OWNER 본인 가게)")
    void deleteOrder_Success_OwnerOwnRestaurant() {
        // given
        UUID orderId = UUID.randomUUID();
        Long deletedBy = 1L;
        UserRole userRole = UserRole.ROLE_OWNER;
        UUID ownerRestaurantId = UUID.randomUUID();

        OrderInfo orderInfo = OrderInfo.createOrder(2L, ownerRestaurantId, UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        User ownerUser = mock(User.class);
        when(ownerUser.getId()).thenReturn(deletedBy);

        RestaurantEntity ownerRestaurant = mock(RestaurantEntity.class);
        when(ownerRestaurant.getUser()).thenReturn(ownerUser);
        when(restaurantRepository.findById(ownerRestaurantId)).thenReturn(Optional.of(ownerRestaurant));

        // when
        orderService.deleteOrder(orderId, deletedBy, userRole);

        // then
        assertThat(orderInfo.getDeletedAt()).isNotNull();
        assertThat(orderInfo.getDeletedBy()).isEqualTo(deletedBy);
    }

    @Test
    @DisplayName("주문 삭제 - 실패 (OWNER 타 가게)")
    void deleteOrder_Fail_OwnerOtherRestaurant() {
        // given
        UUID orderId = UUID.randomUUID();
        Long deletedBy = 1L;
        Long otherUserId = 999L;
        UserRole userRole = UserRole.ROLE_OWNER;
        UUID otherRestaurantId = UUID.randomUUID();

        OrderInfo orderInfo = OrderInfo.createOrder(2L, otherRestaurantId, UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(otherUserId);

        RestaurantEntity otherRestaurant = mock(RestaurantEntity.class);
        when(otherRestaurant.getUser()).thenReturn(otherUser);
        when(restaurantRepository.findById(otherRestaurantId)).thenReturn(Optional.of(otherRestaurant));

        // when & then
        assertThatThrownBy(() -> orderService.deleteOrder(orderId, deletedBy, userRole))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }

    @Test
    @DisplayName("주문 삭제 - 실패 (CUSTOMER 권한 없음)")
    void deleteOrder_Fail_CustomerNoPermission() {
        // given
        UUID orderId = UUID.randomUUID();
        Long deletedBy = 1L;
        UserRole userRole = UserRole.ROLE_CUSTOMER;

        OrderInfo orderInfo = OrderInfo.createOrder(deletedBy, UUID.randomUUID(), UUID.randomUUID(), null);
        when(orderInfoRepository.findById(orderId)).thenReturn(Optional.of(orderInfo));

        // when & then
        assertThatThrownBy(() -> orderService.deleteOrder(orderId, deletedBy, userRole))
                .isInstanceOf(OrderForbiddenException.class)
                ;
    }
}