package com.mealhub.backend.restaurant.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantCategoryRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_restaurant_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCategoryEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rc_id", nullable = false)
    private UUID categoryId;

    @Column(name = "rc_category", nullable = false, length = 20)
    private String category;

    @Builder(access = AccessLevel.PRIVATE)
    private RestaurantCategoryEntity(String category) {
        this.category = category;
    }

    public static RestaurantCategoryEntity of(RestaurantCategoryRequest category) {
        return RestaurantCategoryEntity.builder()
                .category(category.getCategory())
                .build();
    }

    // 가게 분류 수정 메서드
    public void updateCategory(RestaurantCategoryRequest restaurantCategoryRequest) {
        this.category = restaurantCategoryRequest.getUpdatedCategory();
    }
}