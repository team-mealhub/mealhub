package com.mealhub.backend.review.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rv_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "u_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "r_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_restaurant")
    )
    private RestaurantEntity restaurant;

    @Column(name = "rv_star", nullable = false)
    @Min(1)
    @Max(5)
    private short star;

    @Column(name = "rv_comment", length = 500, nullable = false)
    @Size(max = 500)
    private String comment = "";

    @Column(name = "owner_only", nullable = false)
    private boolean ownerOnly = false;

    private ReviewEntity(User user, RestaurantEntity restaurant, short star, String comment, boolean ownerOnly) {
        this.user = user;
        this.restaurant = restaurant;
        this.star = star;
        this.comment = (comment == null ? "" : comment);
        this.ownerOnly = ownerOnly;
    }

    public static ReviewEntity from(User user, RestaurantEntity restaurant, short star, String comment, Boolean ownerOnly) {
        String safeComment = (comment == null) ? "" : comment.trim(); // null -> "", 공백 제거
        boolean safeOwnerOnly = Boolean.TRUE.equals(ownerOnly); // false 또는 null인 경우 -> false로 저장
        return new ReviewEntity(user, restaurant, star, safeComment, safeOwnerOnly);
    }

    public void update(Short star, String comment, Boolean ownerOnly) {
        if (star != null) {
            this.star = star;
        }
        if (comment != null) {
            this.comment = comment.trim();
        }
        if (ownerOnly != null) {
            this.ownerOnly = ownerOnly;
        }
    }

    public void softDelete(Long deletedBy) {
        this.setDeletedAt(java.time.LocalDateTime.now());
        this.setDeletedBy(deletedBy);
    }


}

