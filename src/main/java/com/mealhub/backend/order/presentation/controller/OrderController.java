package com.mealhub.backend.order.presentation.controller;

import com.mealhub.backend.order.application.service.OrderService;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.presentation.dto.request.OrderCreateRequest;
import com.mealhub.backend.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.mealhub.backend.order.presentation.dto.response.OrderDetailResponse;
import com.mealhub.backend.order.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderCreateRequest request
    ) {
        // TODO: UserDetails에서 실제 userId 추출 (현재는 임시)
        Long userId = 1L;
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 단건 조회
    @GetMapping("/{o_id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'OWNER')")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable("o_id") UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // TODO: UserDetails에서 실제 userId, role, restaurantId 추출 (현재는 임시)
        Long currentUserId = 1L;
        String userRole = "CUSTOMER";  // 또는 "OWNER", "MANAGER"
        UUID ownerRestaurantId = null;  // OWNER인 경우 실제 가게 ID 필요

        OrderDetailResponse response = orderService.getOrder(orderId, currentUserId, userRole, ownerRestaurantId);
        return ResponseEntity.ok(response);
    }

    // 주문 검색 (목록)
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'OWNER')")
    public ResponseEntity<Page<OrderResponse>> searchOrders(
            @RequestParam(required = false) Long uId,
            @RequestParam(required = false) UUID rId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderResponse> response = orderService.searchOrders(uId, rId, status, from, to, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경 (점주)
    @PatchMapping("/{o_id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable("o_id") UUID orderId,
            @RequestBody OrderStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // TODO: UserDetails에서 실제 restaurantId 추출 (현재는 임시)
        UUID ownerRestaurantId = null;  // OWNER의 가게 ID 필요

        OrderResponse response = orderService.updateOrderStatus(orderId, request, ownerRestaurantId);
        return ResponseEntity.ok(response);
    }

    // 주문 취소 (고객)
    @PostMapping("/{o_id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable("o_id") UUID orderId,
            @RequestParam(required = false, defaultValue = "변심") String reason,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // TODO: UserDetails에서 실제 userId 추출 (현재는 임시)
        Long currentUserId = 1L;

        OrderResponse response = orderService.cancelOrder(orderId, reason, currentUserId);
        return ResponseEntity.ok(response);
    }

    // 주문 삭제
    @DeleteMapping("/{o_id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable("o_id") UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // TODO: UserDetails에서 실제 userId, role, restaurantId 추출 (현재는 임시)
        Long currentUserId = 1L;
        String userRole = "MANAGER";  // 또는 "OWNER"
        UUID ownerRestaurantId = null;  // OWNER인 경우 실제 가게 ID 필요

        orderService.deleteOrder(orderId, currentUserId, userRole, ownerRestaurantId);
        return ResponseEntity.noContent().build();
    }
}