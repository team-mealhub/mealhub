package com.mealhub.backend.review.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
import com.mealhub.backend.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResDto {
    @JsonProperty("rv_id")
    @Schema(description = "리뷰 ID", example = "1a2b3c4d-1111-2222-3333-abcdefabcdef")
    private UUID reviewId;

    @JsonProperty("u_id")
    @Schema(description = "작성자 유저 ID", example = "12")
    private Long userId;

    @JsonProperty("u_nickname")
    @Schema(description = "작성자 닉네임", example = "맛객리뷰어")
    private String userNickname;

    @JsonProperty("rv_star")
    @Schema(description = "별점", example = "4")
    private short star;

    @JsonProperty("rv_comment")
    @Schema(description = "리뷰 내용", example = "양도 많고 따뜻했어요.")
    private String comment;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "등록 시각 (ISO-8601)", example = "2025-10-01T12:34:56")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    @Schema(description = "등록자 ID", example = "12")
    private Long createdBy;

    @JsonProperty("updated_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "수정 시각 (ISO-8601)", example = "2025-10-02T08:10:00")
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "수정자 ID", example = "12")
    private Long updatedBy;

    @JsonProperty("owner_only")
    @Schema(description = "비공개 여부", example = "false")
    private boolean ownerOnly;

    public static ReviewResDto from(ReviewEntity reviewEntity) {
        return new ReviewResDto(
                reviewEntity.getId(),
                reviewEntity.getUser().getId(),
                getNickName(reviewEntity.getUser()),
                reviewEntity.getStar(),
                reviewEntity.getComment(),
                reviewEntity.getCreatedAt(),
                reviewEntity.getCreatedBy(),
                reviewEntity.getUpdatedAt(),
                reviewEntity.getUpdatedBy(),
                reviewEntity.isOwnerOnly()
        );
    }

    private static String getNickName(User user) {
        // 닉네임 없으면 userId
        String nickname = user.getNickname();
        if (nickname != null && !nickname.isBlank()) return nickname;
        return user.getUserId();
    }
}
