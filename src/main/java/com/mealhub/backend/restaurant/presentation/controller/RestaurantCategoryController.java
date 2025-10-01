package com.mealhub.backend.restaurant.presentation.controller;

import com.mealhub.backend.restaurant.application.service.RestaurantCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/restaurant/category")
@RequiredArgsConstructor
public class RestaurantCategoryController {

    private final RestaurantCategoryService restaurantCategoryService;
}
