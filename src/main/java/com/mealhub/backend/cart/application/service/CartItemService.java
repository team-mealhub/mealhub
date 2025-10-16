package com.mealhub.backend.cart.application.service;

import com.mealhub.backend.cart.domain.entity.CartItem;
import com.mealhub.backend.cart.domain.enums.CartItemStatus;
import com.mealhub.backend.cart.domain.exception.CartItemNotFoundException;
import com.mealhub.backend.cart.infrastructure.repository.CartItemRepository;
import com.mealhub.backend.cart.presentation.dto.request.CartItemCreateRequest;
import com.mealhub.backend.cart.presentation.dto.request.CartItemUpdateRequest;
import com.mealhub.backend.cart.presentation.dto.response.CartItemResponse;
import com.mealhub.backend.cart.presentation.dto.response.CartResponse;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.domain.exception.ProductNotFoundException;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        CartItem cartItem;

        if (request.getStatus() == CartItemStatus.CART) {
            Optional<CartItem> existingCartItem = cartItemRepository.findActiveCartItem(user.getId(), product.getId(), CartItemStatus.CART, false);

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
        size = List.of(10, 30, 50).contains(size) ? size : 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CartItem> cartItems = cartItemRepository.findActiveCartItems(userId, CartItemStatus.CART, false, pageable);

        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCartItemsBuyingTrue(Long userId, List<UUID> cartItemIds) {
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        cartItems.forEach(cartItem -> {
            cartItem.validateOwnership(userId);
            cartItem.updateBuying(true);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCartItemsBuyingFalse(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdAndBuyingTrueAndDeletedAtIsNull(userId);

        cartItems.forEach(cartItem -> {
            cartItem.validateOwnership(userId);
            if (cartItem.getStatus() == CartItemStatus.CART) {
                cartItem.updateBuying(false);
            } else {
                cartItem.delete(userId);
            }
        });
    }

    @Transactional
    public CartItemResponse deleteCartItem(Long userId, UUID cartItemId) {
        CartItem cartItem = getCartItemById(cartItemId);

        cartItem.delete(userId);

        return new CartItemResponse(cartItem);
    }

    private CartItem getCartItemById(UUID cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(CartItemNotFoundException::new);
    }
}
