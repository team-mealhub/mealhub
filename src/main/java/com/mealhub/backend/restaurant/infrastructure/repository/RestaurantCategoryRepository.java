package com.mealhub.backend.restaurant.infrastructure.repository;

import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCategoryRepository extends
        JpaRepository<RestaurantCategoryEntity, UUID> {

    Optional<RestaurantCategoryEntity> findByCategory(String category);
}