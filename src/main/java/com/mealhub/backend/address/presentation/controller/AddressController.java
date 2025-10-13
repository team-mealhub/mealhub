package com.mealhub.backend.address.presentation.controller;

import com.mealhub.backend.address.application.service.AddressService;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<AddressResponse> createAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody AddressRequest addressRequest) {
        // 현재는 임시 mock 사용
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.create(currentUser, addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }

    // 주소 조회(단일)
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.getAddress(currentUser, id);
        return ResponseEntity.ok(addressResponse);
    }

    // 전체 주소 조회
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User currentUser = userDetails.toUser();
        List<AddressResponse> addressResponses = addressService.getAllAddresses(currentUser);
        return ResponseEntity.ok(addressResponses);
    }

    // 주소 수정
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id, @Valid @RequestBody AddressRequest addressRequest) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.updateAddress(currentUser, id, addressRequest);
        return ResponseEntity.ok(addressResponse);
    }

    // 주소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        addressService.deleteAddress(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    // 기본 주소 변경
    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> changeDefaultAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.changeDefault(currentUser, id);
        return ResponseEntity.ok(addressResponse);
    }
}
