package com.mealhub.backend.ai.presentation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiGenerateDescriptionResponse {

    private final String productDescription;

    public static AiGenerateDescriptionResponse from(String productDescription) {
        return AiGenerateDescriptionResponse.builder()
                .productDescription(productDescription)
                .build();
    }
}