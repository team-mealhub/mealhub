package com.mealhub.backend.order.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import com.mealhub.backend.order.application.service.OrderService;
import com.mealhub.backend.order.domain.enums.OrderStatus;
import com.mealhub.backend.order.presentation.dto.request.OrderCreateRequest;
import com.mealhub.backend.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.mealhub.backend.order.presentation.dto.response.OrderDetailResponse;
import com.mealhub.backend.order.presentation.dto.response.OrderResponse;
import com.mealhub.backend.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "주문 API", description = "주문 생성, 조회, 상태 변경, 취소, 삭제 기능 제공")
@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "새로운 주문을 생성합니다. 고객(CUSTOMER) 또는 관리자(MANAGER)만 주문 생성 가능합니다."
    )
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "상품 또는 레스토랑을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid OrderCreateRequest request
    ) {
        Long userId = userDetails.getId();
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "주문 단건 조회",
            description = "특정 주문의 상세 정보를 조회합니다. 고객은 본인 주문만, 점주는 본인 가게 주문만, 관리자는 모든 주문 조회 가능합니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{o_id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'OWNER')")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable("o_id") UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long currentUserId = userDetails.getId();
        UserRole userRole = userDetails.getRole();

        OrderDetailResponse response = orderService.getOrder(orderId, currentUserId, userRole);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 검색",
            description = "조건에 따라 주문 목록을 검색합니다. 고객은 본인 주문만, 점주는 본인 가게 주문만, 관리자는 모든 주문 검색 가능합니다."
    )
    @Parameters({
            @Parameter(name = "uId", description = "사용자 ID", in = ParameterIn.QUERY),
            @Parameter(name = "rId", description = "레스토랑 ID", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "주문 상태", in = ParameterIn.QUERY),
            @Parameter(name = "from", description = "검색 시작 일시 (ISO DateTime)", in = ParameterIn.QUERY),
            @Parameter(name = "to", description = "검색 종료 일시 (ISO DateTime)", in = ParameterIn.QUERY),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이지 크기 (기본값: 20)", in = ParameterIn.QUERY),
            @Parameter(name = "sort", description = "정렬 기준 (기본값: createdAt,desc)", in = ParameterIn.QUERY)
    })
    @ApiResponse(responseCode = "200", description = "주문 검색 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'OWNER')")
    public ResponseEntity<Page<OrderResponse>> searchOrders(
            @RequestParam(required = false) Long uId,
            @RequestParam(required = false) UUID rId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long currentUserId = userDetails.getId();
        UserRole userRole = userDetails.getRole();

        Page<OrderResponse> response = orderService.searchOrders(
                uId, rId, status, from, to, pageable, currentUserId, userRole
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 상태 변경",
            description = "주문 상태를 변경합니다. 점주만 본인 가게 주문의 상태를 변경할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상태 변경 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 상태 전이 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{o_id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable("o_id") UUID orderId,
            @RequestBody @Valid OrderStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long currentUserId = userDetails.getId();

        OrderResponse response = orderService.updateOrderStatus(orderId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 취소",
            description = "주문을 취소합니다. 고객만 본인의 PENDING 상태 주문을 취소할 수 있습니다."
    )
    @Parameters({
            @Parameter(name = "reason", description = "취소 사유 (기본값: 변심)", in = ParameterIn.QUERY)
    })
    @ApiResponse(responseCode = "200", description = "주문 취소 성공")
    @ApiResponse(responseCode = "400", description = "취소 불가능한 주문 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{o_id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable("o_id") UUID orderId,
            @RequestParam(required = false, defaultValue = "변심") String reason,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long currentUserId = userDetails.getId();
        OrderResponse response = orderService.cancelOrder(orderId, reason, currentUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 삭제",
            description = "주문을 삭제(소프트 삭제)합니다. 관리자는 모든 주문을, 점주는 본인 가게의 주문만 삭제할 수 있습니다."
    )
    @ApiResponse(responseCode = "204", description = "주문 삭제 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{o_id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable("o_id") UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long currentUserId = userDetails.getId();
        UserRole userRole = userDetails.getRole();

        orderService.deleteOrder(orderId, currentUserId, userRole);
        return ResponseEntity.noContent().build();
    }
}