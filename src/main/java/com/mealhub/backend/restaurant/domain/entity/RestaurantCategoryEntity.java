package com.mealhub.backend.restaurant.domain.entity;

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
public class RestaurantCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rc_id", nullable = false)
    private UUID rcId;

    @Column(name = "rc_category", nullable = false, length = 20)
    private String category;

    @Builder(access = AccessLevel.PRIVATE)
    private RestaurantCategoryEntity(String category) {
        this.category = category;
    }
}