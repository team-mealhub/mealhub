package com.mealhub.backend.product.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")

public class ProductController {
    private final ProductService productservice;
    // 음식 생성
    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) {
        return productService.createProduct(dto);
    }

    // 음식 조회
    @GetMapping("/{pId}")
    public ProductDTO get(@PathVariable UUID pId) {
        return productService.getProduct(id);
    }

    // 음식 검색
    @GetMapping
    public List<ProductDTO> search(
            @RequestParam UUID pId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.searchProducts(pId, keyword, page, size);
    }

    // 음식 수정
    @PutMapping
    public ProductDTO update(@RequestBody ProductDTO dto) {
        return productService.updateProduct(dto);
    }

    // 음식 숨김 처리
    @PatchMapping("/{pId}/hide")
    public void hide(@PathVariable UUID pId) {
        productService.hideProduct(id);
    }

    // 음식 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
*/

