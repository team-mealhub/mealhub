package com.mealhub.backend.restaurant.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantRequest {

    @NotNull
    private UUID addressId;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private UUID categoryId;
}
