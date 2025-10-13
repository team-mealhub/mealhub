package com.mealhub.backend.restaurant.domain.entity;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.restaurant.presentation.dto.request.RestaurantRequest;
import com.mealhub.backend.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_restaurant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "r_id", nullable = false)
    private UUID restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_id", nullable = false)
    private Address address;

    @Column(name = "r_name", nullable = false, length = 20)
    private String name;

    @Column(name = "r_description", length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rc_id", nullable = false)
    private RestaurantCategoryEntity category;

    @Column(name = "r_status")
    private Boolean status;

    @Builder(access = AccessLevel.PRIVATE)
    private RestaurantEntity(User user, Address address, String name, String description,
            RestaurantCategoryEntity category, Boolean status) {
        this.user = user;
        this.address = address;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
    }

    public static RestaurantEntity of(RestaurantRequest restaurantRequest, User user,
            Address address, RestaurantCategoryEntity category) {
        return RestaurantEntity.builder()
                .user(user)
                .address(address)
                .name(restaurantRequest.getName())
                .description(restaurantRequest.getDescription())
                .category(category)
                .status(false)
                .build();
    }

    // 가게 정보 수정 메서드
    public void updateRestaurant(RestaurantRequest restaurantRequest, Address address,
            RestaurantCategoryEntity category) {
        this.address = address;
        this.name = restaurantRequest.getName();
        this.description = restaurantRequest.getDescription();
        this.category = category;
    }
}