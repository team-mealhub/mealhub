package com.mealhub.backend.restaurant.infrastructure.repository;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

}