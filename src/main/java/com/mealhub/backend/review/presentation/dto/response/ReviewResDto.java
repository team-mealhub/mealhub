package com.mealhub.backend.review.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealhub.backend.review.domain.entity.ReviewEntity;
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
    private UUID reviewId;

    @JsonProperty("u_id")
    private Long userId;

    @JsonProperty("rv_star")
    private short star;

    @JsonProperty("rv_comment")
    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("updated_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;

    @JsonProperty("updated_by")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long updatedBy;

    public static ReviewResDto from(ReviewEntity reviewEntity) {
        return new ReviewResDto(
                reviewEntity.getId(),
                reviewEntity.getUser().getId(),
                reviewEntity.getStar(),
                reviewEntity.getComment(),
                reviewEntity.getCreatedAt(),
                reviewEntity.getCreatedBy(),
                reviewEntity.getUpdatedAt(),
                reviewEntity.getUpdatedBy()
        );
    }
}
