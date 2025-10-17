package com.mealhub.backend.restaurant.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCategoryRequest {

    @NotBlank
    @Size(max = 20, message = "카테고리명은 20자 이내로 입력해주세요.")
    private String category;
}
