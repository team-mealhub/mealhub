package com.mealhub.backend.cart.presentation.controller;

import com.mealhub.backend.cart.application.service.CartItemService;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.cart.presentation.dto.request.CartItemUpdateRequest;
import com.mealhub.backend.cart.presentation.dto.response.CartItemResponse;
import com.mealhub.backend.cart.presentation.dto.response.CartResponse;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.global.presentation.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "장바구니 API", description = "장바구니 아이템 추가, 조회, 수량 변경, 구매 상태 변경, 삭제 기능 제공")
@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @Operation(
            summary = "장바구니 아이템 추가",
            description = "로그인한 유저의 장바구니에 아이템을 추가합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 아이템 추가 성공")
    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public CartItemResponse addCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CartItemCreateRequest request
    ) {
        return cartItemService.addCartItem(userDetails.toUser(), request);
    }

    @Operation(
            summary = "장바구니 아이템 조회",
            description = "로그인한 유저의 장바구니 아이템을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 아이템 조회 성공")
    @GetMapping
    public CartResponse getCartItems(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return cartItemService.getCartItems(userDetails.getId(), pageable);
    }

    @Operation(
            summary = "장바구니 아이템 수량 변경",
            description = "장바구니 아이템의 수량을 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 아이템 수량 변경 성공")
    @ApiResponse(responseCode = "403", description = "장바구니 아이템에 대한 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "장바구니 아이템을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{ct_id}/quantity")
    public CartItemResponse updateCartItemQuantity(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("ct_id") UUID cartItemId,
            @Valid @RequestBody CartItemUpdateRequest.Quantity request
    ) {
        return cartItemService.updateCartItemQuantity(userDetails.getId(), cartItemId, request);
    }

    @Operation(
            summary = "장바구니 아이템 구매 상태 변경",
            description = "장바구니 아이템의 구매 상태를 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 아이템 구매 상태 변경 성공")
    @ApiResponse(responseCode = "403", description = "장바구니 아이템에 대한 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/buying")
    public List<CartItemResponse> updateCartItemBuying(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CartItemUpdateRequest.Buying request
    ) {
        return cartItemService.updateCartItemsBuying(userDetails.getId(), request);
    }

    @Operation(
            summary = "장바구니 아이템 삭제",
            description = "장바구니 아이템을 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 아이템 삭제 성공")
    @ApiResponse(responseCode = "403", description = "장바구니 아이템에 대한 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{ct_id}")
    public CartItemResponse deleteCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("ct_id") UUID cartItemId
    ) {
        return cartItemService.deleteCartItem(userDetails.getId(), cartItemId);
    }
}