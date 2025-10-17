package com.mealhub.backend.product.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "가게 ID는 필수입니다.")
    private UUID rId;

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(max = 20, message = "상품 이름은 20자 이내로 입력해야 합니다.")
    private String name;

    @Size(max = 255, message = "상품 설명은 255자 이내로 입력해야 합니다.")
    private String description;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private long price;
}