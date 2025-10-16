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
    @Size(max = 20, message = "가게명은 20자 이내로 입력해주세요.")
    private String name;

    @NotBlank
    @Size(max = 200, message = "가게 설명은 200자 이내로 입력해주세요.")
    private String description;

    @NotNull
    private UUID categoryId;

    @NotNull
    private Boolean isOpen;
}