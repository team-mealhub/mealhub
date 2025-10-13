package com.mealhub.backend.address.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddressResponse {

    private UUID id;
    private String name;
    private boolean isDefault;
    private String address;
    private String oldAddress;
    private Double longitude;
    private Double latitude;
    private String memo;
}
