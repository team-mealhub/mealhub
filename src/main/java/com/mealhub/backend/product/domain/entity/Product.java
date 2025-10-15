package com.mealhub.backend.product.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity; // ⭐️ RestaurantEntity import 추가
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity(name = "p_product")
@Getter
@NoArgsConstructor
public class Product extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "p_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(name = "p_name", length = 20, nullable = false)
    private String name;

    @Column(name = "p_description", length = 255, nullable = true)
    private String description;

    @Column(name = "p_price", nullable = false)
    private long price;

    @Column(name = "p_status", nullable = false)
    private boolean status;

    @Builder
    private Product(RestaurantEntity restaurant, String name, String description, long price, boolean status) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    public static Product createProduct(RestaurantEntity restaurant, String name, String description, long price, boolean status) {
        return Product.builder()
                .restaurant(restaurant)
                .name(name)
                .description(description)
                .price(price)
                .status(status)
                .build();

    }

    /**
     * 음식 정보 수정 (이름, 설명, 가격)
     */
    public void updateInfo(String name, String description, long price) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.description = description;
        this.price = price;
    }

    public void setHidden(boolean isHidden) {
        this.status = !isHidden;
    }
}