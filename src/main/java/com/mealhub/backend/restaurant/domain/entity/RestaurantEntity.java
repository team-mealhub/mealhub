package com.mealhub.backend.restaurant.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
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
    private User userId;

//    ToDo: Address Entity 생성 후 주석 해제
//    @OneToOne
//    @JoinColumn(name = "a_id", nullable = false)
//    private Address addressId;

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
    private RestaurantEntity(User userId, String name, String description,
            RestaurantCategoryEntity category, Boolean status) {
        this.userId = userId;
//        this.aId = Address addressId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
    }
}