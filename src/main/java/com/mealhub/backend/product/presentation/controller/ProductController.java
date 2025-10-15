package com.mealhub.backend.product.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.product.application.service.ProductService;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import com.mealhub.backend.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")

public class ProductController {
    // 음식 생성
    private final ProductService productservice;

    @PostMapping

    public ResponseEntity<ProductResponse> create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ProductRequest productRequest
    ) {
        // @Valid를 사용하여 DTO의 제약 조건(예: @NotNull)을 검증합니다.
        ProductResponse productResponse = productservice.createProduct(productRequest,userDetails.getId());

        // 생성 성공 시 HTTP 201 Created 응답과 함께 생성된 리소스를 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    // 2. 음식 조회
    @GetMapping("/{pId}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID pId) {
        ProductResponse productResponse = productservice.getProduct(pId);
        return ResponseEntity.ok(productResponse);
    }

    // 음식 검색
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) UUID restaurantId, // ⭐️ 이것이 올바른 타입입니다.
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productPage = productservice.searchProducts(restaurantId, keyword, pageable);
        return ResponseEntity.ok(productPage);
    }



    // 4. 음식 수정 (Update) - 반환 타입: ResponseEntity<ProductResponse>
    // PUT /v1/product
    @PutMapping("/{pId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID pId, // ⭐️ 수정할 상품 ID를 경로 변수로 받습니다.
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ProductRequest productRequest
    ) {

        ProductResponse productResponse = productservice.updateProduct(
                pId,
                productRequest,
                userDetails.getId()
        );
        return ResponseEntity.ok(productResponse);
    }


    @PatchMapping("/{pId}/hide")
    public ResponseEntity<ProductResponse> hideProduct(
            @RequestParam UUID pId,
            @RequestParam boolean status,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductResponse productResponse = productservice.hideProduct(pId, userDetails.getId(),status);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{pId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID pId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productservice.deleteProduct(pId,userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
