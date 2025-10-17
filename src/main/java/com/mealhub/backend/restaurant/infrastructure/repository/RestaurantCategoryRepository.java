package com.mealhub.backend.restaurant.infrastructure.repository;

import com.mealhub.backend.restaurant.domain.entity.RestaurantCategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RestaurantCategoryRepository extends
        JpaRepository<RestaurantCategoryEntity, UUID> {

    Optional<RestaurantCategoryEntity> findByCategory(String category);

    // 전체 가게 분류 조회시 삭제된 분류는 제외
    @Query("SELECT c FROM RestaurantCategoryEntity c WHERE c.deletedBy IS NULL")
    List<RestaurantCategoryEntity> findAllCategories();
}