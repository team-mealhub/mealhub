package com.mealhub.backend.product.application.service;

import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse getProduct(UUID pId) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductResponse.from(product);
    }

    public List<ProductResponse> getVisibleProductsByRestaurant(UUID rId) {
        // findAllByRIdAndStatus(UUID rId, boolean status)를 사용한다고 가정
        List<Product> products = productRepository.findAllByRIdAndStatus(rId, true);

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }


}
