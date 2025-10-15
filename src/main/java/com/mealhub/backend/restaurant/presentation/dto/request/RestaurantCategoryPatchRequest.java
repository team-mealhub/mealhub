package com.mealhub.backend.restaurant.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCategoryPatchRequest {

    @NotBlank
    @Size(max = 20)
    private String category;

    @Size(max = 20)
    private String updatedCategory;
}
