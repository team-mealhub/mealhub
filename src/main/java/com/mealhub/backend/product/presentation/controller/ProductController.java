package com.mealhub.backend.product.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/product")

public class ProductController {
    // 음식 생성
    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) {
        return productService.createProduct(dto);
    }

    // 음식 조회
    @GetMapping("/{id}")
    public ProductDTO get(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    // 음식 검색
    @GetMapping
    public List<ProductDTO> search(
            @RequestParam Long r_id,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.searchProducts(r_id, keyword, page, size);
    }

    // 음식 수정
    @PutMapping
    public ProductDTO update(@RequestBody ProductDTO dto) {
        return productService.updateProduct(dto);
    }

    // 음식 숨김 처리
    @PatchMapping("/{id}/hide")
    public void hide(@PathVariable Long id) {
        productService.hideProduct(id);
    }

    // 음식 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
*/

