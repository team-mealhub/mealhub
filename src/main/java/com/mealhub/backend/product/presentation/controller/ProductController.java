package com.mealhub.backend.product.presentation.controller;

import com.mealhub.backend.product.application.service.ProductService;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")

public class ProductController {
    private final ProductService productservice;

    // 1. 음식 생성 (Create) - 반환 타입: ResponseEntity<ProductResponse> (201 Created)
    // POST /v1/product
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest productRequest) {
        // @Valid를 사용하여 DTO의 제약 조건(예: @NotNull)을 검증합니다.
        ProductResponse productResponse = productservice.createProduct(productRequest);

        // 생성 성공 시 HTTP 201 Created 응답과 함께 생성된 리소스를 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }


    // 2. 음식 조회 (Read - by ID) - 반환 타입: ResponseEntity<ProductResponse>
    // GET /v1/product/{pId}
    @GetMapping("/{pId}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID pId) {
        ProductResponse productResponse = productservice.getProduct(pId);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) UUID rId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page, // 페이지 인덱스는 1부터 시작하도록 수정
            @RequestParam(defaultValue = "10") int size
    ) {
        // Pageable 객체 생성 (page는 1부터 시작)
        Pageable pageable = PageRequest.of(page, size);

        // searchProducts 메서드를 호출하여 Page<ProductResponse>를 받음
        Page<ProductResponse> productPage = productservice.searchProducts(rId, keyword, pageable);

        return ResponseEntity.ok(productPage);
    }


    // 4. 음식 수정 (Update) - 반환 타입: ResponseEntity<ProductResponse>
    // PUT /v1/product
    @PutMapping
    public ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productRequest) {
        // ProductRequest에서 ID를 추출하여 Service의 updateProduct(UUID, ProductRequest) 호출
        UUID productIdFromRequest = productRequest.getRId(); // ProductRequest에 pId 필드가 있다고 가정
        if (productIdFromRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        ProductResponse productResponse = productservice.updateProduct(productIdFromRequest, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    // 5. 음식 숨김 처리 (Hide) - 반환 타입: ResponseEntity<Void> (204 No Content)
    // PATCH /v1/product/{pId}/hide
    @PatchMapping("/{pId}/hide")
    public ResponseEntity<ProductResponse> hideProduct(@PathVariable UUID pId) {
        ProductResponse productResponse = productservice.hideProduct(pId);
        return ResponseEntity.ok(productResponse);
    }

    // 6. 음식 삭제 (Delete) - 반환 타입: ResponseEntity<Void> (204 No Content)
    // DELETE /v1/product/{id}
    @DeleteMapping("/{pId}")
    public ResponseEntity<Void> delete(@PathVariable UUID pId) {
        productservice.deleteProduct(pId);
        return ResponseEntity.noContent().build();
    }

}


