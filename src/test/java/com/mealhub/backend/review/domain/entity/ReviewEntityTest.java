package com.mealhub.backend.review.domain.entity;

import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReviewEntityTest {

    @Test
    @DisplayName("from(): null comment → 빈문자열, trim 적용 / ownerOnly null → false")
    void from_nulls_and_trim() {
        // given
        User user = mock(User.class);
        RestaurantEntity restaurant = mock(RestaurantEntity.class);
        UUID orderId = UUID.randomUUID();

        // when
        ReviewEntity e1 = ReviewEntity.from(user, restaurant, orderId, (short) 5, null, null);
        ReviewEntity e2 = ReviewEntity.from(user, restaurant, orderId, (short) 4, "  hello  ", true);

        // then
        assertThat(e1.getComment()).isEqualTo("");
        assertThat(e1.isOwnerOnly()).isFalse();

        assertThat(e2.getComment()).isEqualTo("hello");
        assertThat(e2.isOwnerOnly()).isTrue();
    }

    @Test
    @DisplayName("update(): 부분 업데이트 - null은 유지, 문자열은 trim")
    void update_partial() {
        // given
        User user = mock(User.class);
        RestaurantEntity restaurant = mock(RestaurantEntity.class);
        UUID orderId = UUID.randomUUID();

        ReviewEntity e = ReviewEntity.from(user, restaurant, orderId, (short) 3, "  old  ", false);

        // when
        e.update(null, "  new body  ", null); // star/ownerOnly는 유지, comment만 변경

        // then
        assertThat(e.getStar()).isEqualTo((short) 3);
        assertThat(e.getComment()).isEqualTo("new body");
        assertThat(e.isOwnerOnly()).isFalse();

        // when2: star/ownerOnly만 변경
        e.update((short) 5, null, true);
        assertThat(e.getStar()).isEqualTo((short) 5);
        assertThat(e.getComment()).isEqualTo("new body");
        assertThat(e.isOwnerOnly()).isTrue();
    }

    @Test
    @DisplayName("softDelete(): 삭제 메타가 설정된다")
    void softDelete_sets_audit() {
        // given
        User user = mock(User.class);
        RestaurantEntity restaurant = mock(RestaurantEntity.class);
        UUID orderId = UUID.randomUUID();
        ReviewEntity e = ReviewEntity.from(user, restaurant, orderId, (short) 4, "c", false);

        // when
        e.softDelete(99L);

        // then
        assertThat(e.getDeletedAt()).isNotNull();
        assertThat(e.getDeletedBy()).isEqualTo(99L);

        // 삭제 시각이 '지금'과 크게 차이 나지 않는 정도 확인(대략성)
        assertThat(e.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
