package com.mealhub.backend.review.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
import com.mealhub.backend.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewListItemDto {

    @JsonProperty("rv_id")
    private UUID reviewId;

    @JsonProperty("u_id")
    private Long userId;

    @JsonProperty("u_nickname")
    private String userNickname;

    @JsonProperty("rv_star")
    private short star;

    @JsonProperty("rv_comment")
    private String comment;

    public static ReviewListItemDto from(ReviewEntity reviewEntity) {
        return new ReviewListItemDto(
                reviewEntity.getId(),
                reviewEntity.getUser().getId(),
                getNickName(reviewEntity.getUser()),
                reviewEntity.getStar(),
                reviewEntity.getComment()
        );
    }

    private static String getNickName(User user) {
        // 닉네임 없으면 userId
        String nickname = user.getNickname();
        if (nickname != null && !nickname.isBlank()) return nickname;
        return user.getUserId();
    }
}
