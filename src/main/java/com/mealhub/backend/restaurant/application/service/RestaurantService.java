package com.mealhub.backend.restaurant.application.service;

import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
}
