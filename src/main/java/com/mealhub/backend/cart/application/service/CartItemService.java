package com.mealhub.backend.cart.application.service;

import com.mealhub.backend.cart.infrastructure.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
}
