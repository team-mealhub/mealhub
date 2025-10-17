package com.mealhub.backend.review.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {

    @JsonProperty("rv_id")
    @Schema(description = "리뷰 ID", example = "1a2b3c4d-1111-2222-3333-abcdefabcdef")
    private UUID reviewId;

    @JsonProperty("rv_star")
    @Min(1)
    @Max(5)
    @Schema(description = "별점(1~5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short star;

    @JsonProperty("rv_comment")
    @Size(max = 500)
    @Schema(description = "리뷰 내용(최대 500자)", example = "배달 빠르고 맛있어요.")
    private String comment;

    @JsonProperty("owner_only")
    @Schema(description = "사장님만 볼 수 있는 비공개 여부", example = "false")
    private Boolean ownerOnly;
}