package com.mealhub.backend.cart.presentation.controller;

import com.mealhub.backend.cart.application.service.CartItemService;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.cart.presentation.dto.request.CartItemUpdateRequest;
import com.mealhub.backend.cart.presentation.dto.response.CartItemResponse;
import com.mealhub.backend.cart.presentation.dto.response.CartResponse;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public CartItemResponse addCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CartItemCreateRequest request
    ) {
        return cartItemService.addCartItem(userDetails.toUser(), request);
    }

    @GetMapping
    public CartResponse getCartItems(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return cartItemService.getCartItems(userDetails.getId(), pageable);
    }

    @PatchMapping("/{ct_id}/quantity")
    public CartItemResponse updateCartItemQuantity(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("ct_id") UUID cartItemId,
            @Valid @RequestBody CartItemUpdateRequest.Quantity request
    ) {
        return cartItemService.updateCartItemQuantity(userDetails.getId(), cartItemId, request);
    }

    @PatchMapping("/buying")
    public List<CartItemResponse> updateCartItemBuying(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CartItemUpdateRequest.Buying request
    ) {
        return cartItemService.updateCartItemsBuying(userDetails.getId(), request);
    }

    @DeleteMapping("/{ct_id}")
    public CartItemResponse deleteCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("ct_id") UUID cartItemId
    ) {
        return cartItemService.deleteCartItem(userDetails.getId(), cartItemId);
    }
}