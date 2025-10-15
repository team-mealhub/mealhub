package com.mealhub.backend.order.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCreateRequest {

    @NotNull
    private UUID rId;  // 가게 ID

    @NotNull
    private UUID aId;  // 배송 주소 ID

    private String oRequirements;  // 추가 요청사항

    @NotEmpty
    private List<UUID> cartItemIds;
}