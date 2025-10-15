package com.mealhub.backend.product.presentation.controller;

import com.mealhub.backend.global.infrastructure.config.security.UserDetailsImpl;
import com.mealhub.backend.product.application.service.ProductService;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import com.mealhub.backend.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "상품 관리 API", description = "상품 등록, 조회, 검색, 수정, 삭제 및 숨김 처리 기능.") //
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")

public class ProductController {
    private final ProductService productservice;


     //1. 상품 생성 (Create)

    @Operation(
            summary = "상품 신규 등록",
            description = "새로운 상품 정보를 시스템에 등록합니다. 레스토랑 소유자만 가능합니다."
    )
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ProductRequest productRequest
    ) {
        ProductResponse productResponse = productservice.createProduct(productRequest,userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }


     //2. 상품 조회 (Read - by ID)

    @Operation(
            summary = "단일 상품 상세 조회",
            description = "특정 상품 ID를 이용해 해당 상품의 상세 정보를 조회합니다."
    )
    @GetMapping("/{pId}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID pId) {
        ProductResponse productResponse = productservice.getProduct(pId);
        return ResponseEntity.ok(productResponse);
    }


     //3. 상품 검색 (Search)
    @Operation(
            summary = "상품 목록 검색 및 페이징",
            description = "키워드 또는 가게 ID로 상품 목록을 조회합니다. "
    )
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productPage = productservice.searchProducts(restaurantId, keyword, pageable);
        return ResponseEntity.ok(productPage);
    }


     // 4. 상품 수정 (Update)

    @Operation(
            summary = "상품 정보 수정",
            description = "특정 상품 ID의 정보(이름, 설명, 가격)를 수정합니다."
    )
    @PutMapping("/{pId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID pId,
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


     //5. 상품 숨김 처리 (Hide)

    @Operation(
            summary = "상품 숨김/판매 중지 처리",
            description = "특정 상품을 숨김 상태로 변경하여 음식을 보여주거나 숨김처리를 합니다."
    )
    @PatchMapping("/{pId}/hide")
    public ResponseEntity<ProductResponse> hideProduct(
            @PathVariable UUID pId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductResponse productResponse = productservice.hideProduct(pId, userDetails.getId());
        return ResponseEntity.ok(productResponse);
    }


     // 6. 상품 삭제 (Delete)

    @Operation(
            summary = "상품 영구 삭제",
            description = "특정 상품을 데이터베이스에서 영구적으로 삭제합니다."
    )
    @DeleteMapping("/{pId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID pId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productservice.deleteProduct(pId,userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
