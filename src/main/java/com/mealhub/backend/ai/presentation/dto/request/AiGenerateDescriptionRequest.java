package com.mealhub.backend.ai.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiGenerateDescriptionRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(max = 20, message = "상품 이름은 20자 이내로 입력해야 합니다.")
    private String productName;

    @NotBlank(message = "상품 카테고리는 필수입니다.")
    @Size(max = 255, message = "상품 카테고리는 20자 이내로 입력해야 합니다.")
    private String productCategory;

}