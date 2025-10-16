package com.mealhub.backend.address.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "주소 응답 DTO")
@Getter
@AllArgsConstructor
public class AddressResponse {

    @Schema(description = "주소 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "주소 별칭", example = "우리집")
    private String name;

    @JsonProperty("isDefault")
    @Schema(description = "기본 주소 여부", example = "true")
    private boolean defaultAddress;

    @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로")
    private String address;

    @Schema(description = "지번 주소", example = "서울특별시 강남구 역삼동")
    private String oldAddress;

    @Schema(description = "경도", example = "127.0")
    private Double longitude;

    @Schema(description = "위도", example = "37.0")
    private Double latitude;

    @Schema(description = "배송 메모", example = "문 앞에 두고 가주세요")
    private String memo;
}
