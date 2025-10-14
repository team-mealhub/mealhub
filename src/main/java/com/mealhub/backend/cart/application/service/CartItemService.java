package com.mealhub.backend.cart.application.service;

import com.mealhub.backend.cart.domain.entity.CartItem;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import com.mealhub.backend.cart.domain.exception.CartItemNotFoundException;
import com.mealhub.backend.cart.infrastructure.repository.CartItemRepository;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.cart.presentation.dto.request.CartItemUpdateRequest;
import com.mealhub.backend.cart.presentation.dto.response.CartItemResponse;
import com.mealhub.backend.cart.presentation.dto.response.CartResponse;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartItemResponse addCartItem(User user, CartItemCreateRequest request) {
        // TODO: Product 예외로 변경
        Product product = productRepository.findById(request.getProductId())
                        .orElseThrow(NotFoundException::new);

        CartItem cartItem;

        if (request.getStatus() == CartItemStatus.CART && !request.isBuying()) {
            Optional<CartItem> existingCartItem = cartItemRepository.findActiveCartItem(user.getId(), product.getPId(), CartItemStatus.CART, false);

            if (existingCartItem.isPresent()) {
                cartItem = existingCartItem.get();
                cartItem.addQuantity(request.getQuantity());
            } else {
                cartItem = cartItemRepository.save(CartItem.createCartItem(request, user, product));
            }
        } else {
            cartItem = cartItemRepository.save(CartItem.createCartItem(request, user, product));
        }

        return new CartItemResponse(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getCartItems(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CartItem> cartItems = cartItemRepository.findActiveCartItems(userId, CartItemStatus.CART, false, pageable);

        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getProduct().getPPrice() * cartItem.getQuantity())
                .sum();

        return new CartResponse(cartItems.map(CartItemResponse::new), totalPrice);
    }

    @Transactional
    public CartItemResponse updateCartItemQuantity(Long userId, UUID cartItemId, CartItemUpdateRequest.Quantity request) {
        CartItem cartItem = getCartItemById(cartItemId);
        cartItem.validateOwnership(userId);

        cartItem.updateQuantity(request.getQuantity());
        return new CartItemResponse(cartItem);
    }

    @Transactional
    public List<CartItemResponse> updateCartItemsBuying(Long userId, CartItemUpdateRequest.Buying request) {
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

        cartItems.forEach(cartItem -> {
            cartItem.validateOwnership(userId);
            cartItem.updateBuying(request.isBuying());
        });

        return cartItems.stream().map(CartItemResponse::new).toList();
    }

    @Transactional
    public CartItemResponse deleteCartItem(Long userId, UUID cartItemId) {
        CartItem cartItem = getCartItemById(cartItemId);
        cartItem.validateOwnership(userId);

        cartItem.setDeletedAt(LocalDateTime.now());
        cartItem.setDeletedBy(userId);

        return new CartItemResponse(cartItem);
    }

    private CartItem getCartItemById(UUID cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(CartItemNotFoundException::new);
    }
}
