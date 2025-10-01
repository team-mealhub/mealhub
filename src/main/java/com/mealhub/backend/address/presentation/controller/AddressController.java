package com.mealhub.backend.address.presentation.controller;

import com.mealhub.backend.address.application.service.AddressService;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // 주소 등록
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) {
        // 현재는 임시 mock 사용
        User mockUser = new User();
        AddressResponse addressResponse = addressService.create(mockUser, addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }

    // 주소 조회(1개)
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable UUID id) {
        User mockUser = new User();
        AddressResponse addressResponse = addressService.getAddress(mockUser, id);
        return ResponseEntity.ok(addressResponse);
    }

    // 전체 주소 조회
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        User mockUser = new User();
        List<AddressResponse> addressResponses = addressService.getAllAddresses(mockUser);
        return ResponseEntity.ok(addressResponses);
    }

    // 주소 수정
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable UUID id, @RequestBody AddressRequest addressRequest) {
        User mockUser = new User();
        AddressResponse addressResponse = addressService.updateAddress(mockUser, id, addressRequest);
        return ResponseEntity.ok(addressResponse);
    }

    // 주소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        User mockUser = new User();
        addressService.deleteAddress(mockUser, id);
        return ResponseEntity.noContent().build();
    }
}
