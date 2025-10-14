package com.mealhub.backend.order.domain.entity;

import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.domain.exception.OrderCancelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrderInfo Entity 테스트")
class OrderInfoTest {

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        // given
        Long userId = 1L;
        UUID restaurantId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        String requirements = "빨리 배달해주세요";

        // when
        OrderInfo orderInfo = OrderInfo.createOrder(userId, restaurantId, addressId, requirements);

        // then
        assertThat(orderInfo.getOInfoId()).isNotNull();
        assertThat(orderInfo.getUserId()).isEqualTo(userId);
        assertThat(orderInfo.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(orderInfo.getAddressId()).isEqualTo(addressId);
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderInfo.getRequirements()).isEqualTo(requirements);
        assertThat(orderInfo.getTotal()).isZero();
    }

    @Test
    @DisplayName("주문 상품 추가 및 총액 계산 - 성공")
    void addOrderItem_CalculatesTotal_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        OrderItem item1 = OrderItem.createOrderItem("치킨", 20000L, 1L);
        OrderItem item2 = OrderItem.createOrderItem("콜라", 2000L, 2L);

        // when
        orderInfo.addOrderItem(item1);
        orderInfo.addOrderItem(item2);

        // then
        assertThat(orderInfo.getItems()).hasSize(2);
        assertThat(orderInfo.getTotal()).isEqualTo(24000L); // 20000 + 2000*2
        assertThat(item1.getOrderInfo()).isEqualTo(orderInfo);
        assertThat(item2.getOrderInfo()).isEqualTo(orderInfo);
    }

    @Test
    @DisplayName("주문 상태 변경 - 성공")
    void updateStatus_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        OrderStatus newStatus = OrderStatus.IN_PROGRESS;
        String reason = "조리 시작";

        // when
        orderInfo.updateStatus(newStatus, reason);

        // then
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(orderInfo.getStatusLogs()).hasSize(1);

        OrderStatusLog log = orderInfo.getStatusLogs().get(0);
        assertThat(log.getPrevStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(log.getCurrStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(log.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("주문 취소 - 성공 (PENDING 상태)")
    void cancelOrder_Success_WhenPending() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        String cancelReason = "변심";

        // when
        orderInfo.cancel(cancelReason);

        // then
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderInfo.getStatusLogs()).hasSize(1);

        OrderStatusLog log = orderInfo.getStatusLogs().get(0);
        assertThat(log.getPrevStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(log.getCurrStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(log.getReason()).isEqualTo(cancelReason);
    }

    @Test
    @DisplayName("주문 취소 - 실패 (PENDING 상태가 아님)")
    void cancelOrder_Fail_WhenNotPending() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작");

        // when & then
        assertThatThrownBy(() -> orderInfo.cancel("변심"))
                .isInstanceOf(OrderCancelException.class);
    }

    @Test
    @DisplayName("주문 삭제 (Soft Delete) - 성공")
    void deleteOrder_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        Long deletedBy = 100L;

        // when
        orderInfo.delete(deletedBy);

        // then
        assertThat(orderInfo.getDeletedAt()).isNotNull();
        assertThat(orderInfo.getDeletedBy()).isEqualTo(deletedBy);
    }

    @Test
    @DisplayName("총액 재계산 - 성공")
    void calculateTotal_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        OrderItem item1 = OrderItem.createOrderItem("상품1", 10000L, 2L);
        OrderItem item2 = OrderItem.createOrderItem("상품2", 5000L, 3L);

        orderInfo.addOrderItem(item1);
        orderInfo.addOrderItem(item2);

        // when
        orderInfo.calculateTotal();

        // then
        assertThat(orderInfo.getTotal()).isEqualTo(35000L); // 10000*2 + 5000*3
    }

    @Test
    @DisplayName("상태 전이 검증 - PENDING → IN_PROGRESS 성공")
    void validateStatusTransition_PendingToInProgress_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);

        // when & then
        assertThatCode(() -> orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작"))
                .doesNotThrowAnyException();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("상태 전이 검증 - PENDING → CANCELLED 성공")
    void validateStatusTransition_PendingToCancelled_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);

        // when & then
        assertThatCode(() -> orderInfo.updateStatus(OrderStatus.CANCELLED, "고객 요청"))
                .doesNotThrowAnyException();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("상태 전이 검증 - PENDING → DELIVERED 실패")
    void validateStatusTransition_PendingToDelivered_Fail() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);

        // when & then
        assertThatThrownBy(() -> orderInfo.updateStatus(OrderStatus.DELIVERED, "배송 완료"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING 상태에서는");
    }

    @Test
    @DisplayName("상태 전이 검증 - IN_PROGRESS → OUT_FOR_DELIVERY 성공")
    void validateStatusTransition_InProgressToOutForDelivery_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작");

        // when & then
        assertThatCode(() -> orderInfo.updateStatus(OrderStatus.OUT_FOR_DELIVERY, "배송 시작"))
                .doesNotThrowAnyException();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.OUT_FOR_DELIVERY);
    }

    @Test
    @DisplayName("상태 전이 검증 - IN_PROGRESS → PENDING 실패")
    void validateStatusTransition_InProgressToPending_Fail() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작");

        // when & then
        assertThatThrownBy(() -> orderInfo.updateStatus(OrderStatus.PENDING, "대기로 복귀"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IN_PROGRESS 상태에서는");
    }

    @Test
    @DisplayName("상태 전이 검증 - OUT_FOR_DELIVERY → DELIVERED 성공")
    void validateStatusTransition_OutForDeliveryToDelivered_Success() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작");
        orderInfo.updateStatus(OrderStatus.OUT_FOR_DELIVERY, "배송 시작");

        // when & then
        assertThatCode(() -> orderInfo.updateStatus(OrderStatus.DELIVERED, "배송 완료"))
                .doesNotThrowAnyException();
        assertThat(orderInfo.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("상태 전이 검증 - DELIVERED → CANCELLED 실패 (종료 상태)")
    void validateStatusTransition_DeliveredToCancelled_Fail() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "조리 시작");
        orderInfo.updateStatus(OrderStatus.OUT_FOR_DELIVERY, "배송 시작");
        orderInfo.updateStatus(OrderStatus.DELIVERED, "배송 완료");

        // when & then
        assertThatThrownBy(() -> orderInfo.updateStatus(OrderStatus.CANCELLED, "취소 시도"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("더 이상 변경할 수 없습니다");
    }

    @Test
    @DisplayName("상태 전이 검증 - CANCELLED → IN_PROGRESS 실패 (종료 상태)")
    void validateStatusTransition_CancelledToInProgress_Fail() {
        // given
        OrderInfo orderInfo = OrderInfo.createOrder(1L, UUID.randomUUID(), UUID.randomUUID(), null);
        orderInfo.updateStatus(OrderStatus.CANCELLED, "고객 취소");

        // when & then
        assertThatThrownBy(() -> orderInfo.updateStatus(OrderStatus.IN_PROGRESS, "재시작 시도"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("더 이상 변경할 수 없습니다");
    }
}