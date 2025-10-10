package com.mealhub.backend.product.application.service;

import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

      /* ==========================
          1. 상품 생성 (Create)
       ========================== */


    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        // 1. DTO에서 엔티티로 변환
        Product product = Product.createProduct(
                productRequest.getRId(),
                productRequest.getPName(),
                productRequest.getPDescription(),
                productRequest.getPPrice(),
                true //  다섯 번째 인자 (pStatus) 추가
        );

        // 2. 저장 및 저장된 엔티티 반환
        Product savedProduct = productRepository.save(product);

        // 3. 응답 DTO로 변환하여 반환
        return ProductResponse.from(savedProduct); // ProductResponse의 from() 메서드를 사용한다고 가정
    }


    @Transactional
    public ProductResponse getProduct(UUID pId) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductResponse.from(product);
    }


    @Transactional
    public List<ProductResponse> getVisibleProductsByRestaurant(UUID rId) {
        // findAllByRIdAndStatus(UUID rId, boolean status)를 사용한다고 가정
        List<Product> products = productRepository.findAllByRIdAndStatus(rId, true);

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(UUID pId, ProductRequest productRequest) {
        // 1. 상품 조회 (없으면 예외 발생)
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. 엔티티의 비즈니스 메서드를 이용한 정보 수정
        product.updateInfo(
                productRequest.getPName(),
                productRequest.getPDescription(),
                productRequest.getPPrice()
        );

        // @Transactional에 의해 자동 저장(더티 체킹)되므로 별도 save() 호출 불필요

        // 3. 응답 DTO로 변환하여 반환
        return ProductResponse.from(product);
    }


  //상품 삭제

    @Transactional
    public void deleteProduct(UUID pId) {
        // 삭제 전 해당 상품이 존재하는지 확인하거나,
        // 그냥 deleteById를 호출하여 예외를 잡는 방식도 가능합니다.
        productRepository.deleteById(pId);
    }


    // 모든 상품 목록 조회
    @Transactional
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }



    public Page<ProductResponse> searchProducts(UUID rId, String keyword, Pageable pageable) {
        Page<Product> productPage;
        // 키워드 trim 및 null/empty 체크
        String searchKeyword = (keyword != null && !keyword.isEmpty()) ? keyword.trim() : null;

        if (rId != null && searchKeyword != null) {
            // 1. rId와 keyword 모두 있는 경우
            // Repository 정의에 따라 findByRestaurantIdAndNameContainingIgnoreCase를 사용합니다.
            productPage = productRepository.findByRestaurantIdAndNameContainingIgnoreCase(rId, searchKeyword, pageable);
        } else if (rId != null) {
            // 2. rId만 있는 경우
            // Repository 정의에 따라 findByRestaurantId를 사용합니다.
            productPage = productRepository.findByRestaurantId(rId, pageable);
        } else if (searchKeyword != null) {
            // 3. keyword만 있는 경우
            // Repository 정의에 따라 findByNameContainingIgnoreCase를 사용합니다.
            productPage = productRepository.findByNameContainingIgnoreCase(searchKeyword, pageable);
        } else {
            // 4. rId, keyword 모두 없는 경우 (전체 상품 조회 + 페이지네이션)
            productPage = productRepository.findAll(pageable);
        }

        // Product 엔티티 Page를 ProductResponse DTO Page로 변환하여 반환
        return productPage.map(ProductResponse::from);
    }

    /* ==========================
        5. 상품 숨김 처리 (Hide)
    ========================== */
    /**
     * 특정 상품의 상태를 숨김(pStatus=false)으로 변경합니다.
     */
    @Transactional
    public void hideProduct(UUID pId) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pId));

        // 엔티티의 비즈니스 메서드를 호출하여 상태 변경
        product.setHidden(true);

        // @Transactional에 의해 변경 내용이 자동으로 DB에 반영됨 (더티 체킹)
    }






}
