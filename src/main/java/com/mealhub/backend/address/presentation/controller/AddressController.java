package com.mealhub.backend.address.presentation.controller;

import com.mealhub.backend.address.application.service.AddressService;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "주소 API", description = "주소 등록, 조회, 수정, 삭제, 검색 등의 기능 제공")
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "주소 등록", description = "새로운 주소 등록")
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody AddressRequest addressRequest) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.create(currentUser, addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }

    @Operation(summary = "주소 단일 조회", description = "주소 ID로 특정 주소 조회")
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.getAddress(currentUser, id);
        return ResponseEntity.ok(addressResponse);
    }

    @Operation(summary = "주소 전체 조회", description = "검색어를 포함해 전체 주소 페이징 조회")
    @GetMapping
    public ResponseEntity<Page<AddressResponse>> getAllAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String keyword, Pageable pageable) {
        User currentUser = userDetails.toUser();
        Page<AddressResponse> addressResponses = addressService.getAllAddresses(currentUser, keyword, pageable);
        return ResponseEntity.ok(addressResponses);
    }

    @Operation(summary = "주소 수정", description = "기존 주소 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id, @Valid @RequestBody AddressRequest addressRequest) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.updateAddress(currentUser, id, addressRequest);
        return ResponseEntity.ok(addressResponse);
    }

    @Operation(summary = "주소 삭제", description = "주소 ID로 주소 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        addressService.deleteAddress(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 주소 변경", description = "특정 주소를 기본 주소로 변경")
    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> changeDefaultAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        User currentUser = userDetails.toUser();
        AddressResponse addressResponse = addressService.changeDefault(currentUser, id);
        return ResponseEntity.ok(addressResponse);
    }

}

