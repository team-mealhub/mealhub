package com.mealhub.backend.address.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    private String name; // 주소 별칭
    private boolean isDefault; // 기본 배송지 여부
    private String address; // 도로명 주소
    private String oldAddress; // 지번
    private Double longitude; // 경도
    private Double latitude; // 위도
    private String memo; // 배송메모

}
