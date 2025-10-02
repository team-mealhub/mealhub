package com.mealhub.backend.cart.presentation.controller;

import com.mealhub.backend.cart.application.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
}
