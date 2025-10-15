package com.mealhub.backend.review.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {

    @Min(1)
    @Max(5)
    @Schema(description = "별점(1~5)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private short star;

    @Size(max = 500, message = "리뷰는 최대 500자까지 작성할 수 있습니다.")
    @Schema(description = "리뷰 내용(최대 500자)", example = "배달 빠르고 맛있어요.")
    private String comment;

    @Schema(description = "사장님만 볼 수 있는 비공개 여부", example = "false")
    private Boolean ownerOnly;
}