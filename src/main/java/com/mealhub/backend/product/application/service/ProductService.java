package com.mealhub.backend.product.application.service;

import com.mealhub.backend.global.domain.exception.BadRequestException;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
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
    private final RestaurantRepository restaurantRepository;

      /* ==========================
          1. 상품 생성 (Create)
       ========================== */


    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, Long userId) {

        RestaurantEntity restaurant = restaurantRepository.findById(productRequest.getRId())
                .orElseThrow(() -> new BadRequestException("유효하지 않은 레스토랑 ID입니다."));

        validateRestaurantOwner(restaurant, userId);


        Product product = Product.createProduct(
                restaurant,
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
    public ProductResponse updateProduct(UUID pId, ProductRequest productRequest,Long userId) {
        // 1. 상품 조회 (없으면 예외 발생)
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateRestaurantOwner(product.getRestaurant(), userId);

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
    public void deleteProduct(UUID pId,Long userId) {

        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateRestaurantOwner(product.getRestaurant(), userId);
        // 삭제 전 해당 상품이 존재하는지 확인하거나,
             productRepository.deleteById(pId);
    }



    public Page<ProductResponse> searchProducts(UUID restaurantId, String keyword, Pageable pageable) {
        Page<Product> productPage;

        String searchKeyword = (keyword != null && !keyword.isEmpty()) ? keyword.trim() : null;

        if (restaurantId != null && searchKeyword != null) {

            productPage = productRepository.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, searchKeyword, pageable);
        } else if (restaurantId != null) {

            productPage = productRepository.findByRestaurantId(restaurantId, pageable);
        } else if (searchKeyword != null) {
            productPage = productRepository.findByNameContainingIgnoreCase(searchKeyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }         return productPage.map(ProductResponse::from);
    }

    /**
     * 특정 상품의 상태를 숨김(pStatus=false)으로 변경합니다.
     */
    @Transactional
    public ProductResponse hideProduct(UUID pId, Long userId) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID"));
        validateRestaurantOwner(product.getRestaurant(), userId);
               product.setHidden(true);
               return ProductResponse.from(product);
    }

    private void validateRestaurantOwner(RestaurantEntity restaurantEntity,Long userId) {
        if(!restaurantEntity.getUser().getId().equals(userId)) {
            throw new BadRequestException("해당 상품에 대한 수정/삭제 권한이 없습니다.");
        }
    }









}
