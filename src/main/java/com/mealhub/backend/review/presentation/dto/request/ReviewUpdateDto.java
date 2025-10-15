package com.mealhub.backend.review.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID reviewId;

    @JsonProperty("rv_star")
    @Min(1)
    @Max(5)
    private Short star;

    @JsonProperty("rv_comment")
    @Size(max = 500)
    private String comment;

    @JsonProperty("owner_only")
    private Boolean ownerOnly;
}