package com.mealhub.backend.restaurant.infrastructure.repository;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    /**
     * Find all restaurants owned by a specific user
     *
     * @param userId the user ID
     * @return list of restaurants owned by the user
     */
    List<RestaurantEntity> findByUser_Id(Long userId);
}