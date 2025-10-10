package com.mealhub.backend.product.presentation.controller;

import com.mealhub.backend.product.application.service.ProductService;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
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


    // 2. 음식 조회 (Read - by ID) - 반환 타입: ResponseEntity<ProductResponse>
    // GET /v1/product/{pId}
    @GetMapping("/{pId}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID pId) {
        ProductResponse productResponse = productservice.getProduct(pId);
        return ResponseEntity.ok(productResponse);
    }

    // 3. 음식 검색 (Search) - 반환 타입: ResponseEntity<List<ProductResponse>>
    // GET /v1/product?rId={rId}&keyword={keyword}&page={page}&size={size}
    @GetMapping
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam(required = false) UUID rId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 현재 Service에는 searchProducts가 없으므로 임시로 getAllProducts를 사용합니다.
        List<ProductResponse> allProducts = productservice.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    // 4. 음식 수정 (Update) - 반환 타입: ResponseEntity<ProductResponse>
    // PUT /v1/product
    @PutMapping
    public ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productRequest) {
        // ProductRequest에서 ID를 추출하여 Service의 updateProduct(UUID, ProductRequest) 호출
        UUID productIdFromRequest = productRequest.getpId(); // ProductRequest에 pId 필드가 있다고 가정
        if (productIdFromRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        ProductResponse productResponse = productservice.updateProduct(productIdFromRequest, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    // 5. 음식 숨김 처리 (Hide) - 반환 타입: ResponseEntity<Void> (204 No Content)
    // PATCH /v1/product/{pId}/hide
    @PatchMapping("/{pId}/hide")
    public ResponseEntity<Void> hide(@PathVariable UUID pId) {
        // TODO: ProductService에 hideProduct(UUID pId) 구현 필요
        // productService.hideProduct(pId);
        return ResponseEntity.noContent().build();
    }

    // 6. 음식 삭제 (Delete) - 반환 타입: ResponseEntity<Void> (204 No Content)
    // DELETE /v1/product/{id}
    @DeleteMapping("/{pId}")
    public ResponseEntity<Void> delete(@PathVariable UUID pId) {
        productservice.deleteProduct(pId);
        return ResponseEntity.noContent().build();
    }

}


