package com.mealhub.backend.address.presentation.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "주소 별칭은 필수값입니다.")
    @Size(max = 50, message = "주소 별칭은 50자까지 입력 가능합니다.")
    private String name; // 주소 별칭

    private boolean isDefault; // 기본 배송지 여부

    @NotBlank(message = "도로명 주소는 필수값입니다.")
    @Size(max = 255, message = "최대 255자까지 입력 가능합니다.")
    private String address; // 도로명 주소

    @Size(max = 255, message = "최대 255자까지 입력 가능합니다.")
    private String oldAddress; // 지번

    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
    private Double longitude; // 경도

    @DecimalMin(value = "-90.0", message = "경도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "경도는 90 이하여야 합니다.")
    private Double latitude; // 위도

    @Size(max = 255, message = "최대 255자까지 입력 가능합니다.")
    private String memo; // 배송메모

}