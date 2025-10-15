package com.mealhub.backend.review.presentation.dto.request;

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
    private short star;

    @Size(max = 500, message = "리뷰는 최대 500자까지 작성할 수 있습니다.")
    private String comment;

    private Boolean ownerOnly;
}