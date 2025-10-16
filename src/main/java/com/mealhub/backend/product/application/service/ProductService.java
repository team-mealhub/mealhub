package com.mealhub.backend.product.application.service;

import com.mealhub.backend.global.domain.exception.BadRequestException;
import com.mealhub.backend.global.domain.exception.NotFoundException;
import com.mealhub.backend.product.domain.entity.Product;
import com.mealhub.backend.product.domain.entity.QProduct;
import com.mealhub.backend.product.infrastructure.repository.ProductRepository;
import com.mealhub.backend.product.presentation.dto.request.ProductRequest;
import com.mealhub.backend.product.presentation.dto.response.ProductResponse;
import com.mealhub.backend.restaurant.domain.entity.RestaurantEntity;
import com.mealhub.backend.restaurant.infrastructure.repository.RestaurantRepository;
import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse getProduct(UUID pId, boolean status) {
        QProduct qProduct = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qProduct.id.eq(pId))
                .and(qProduct.status.eq(status))
                .and(qProduct.deletedAt.isNull());
        Product product = productRepository.findOne(builder)
                .orElseThrow(() -> new NotFoundException("해당 상품이 없습니다."));
        return ProductResponse.from(product);
    }

    @Transactional
    public List<ProductResponse> getVisibleProductsByRestaurant(UUID rId) {

        BooleanBuilder builder = new BooleanBuilder();
        QProduct qProduct = QProduct.product;
        builder.and(qProduct.restaurant.restaurantId.eq(rId))
                .and(qProduct.status.eq(true))
                .and(qProduct.deletedAt.isNull());
        List<Product> products = (List<Product>)productRepository.findAll(builder);

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(UUID pId, ProductRequest productRequest,Long userId) {

        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new NotFoundException("해당 상품이 없습니다."));

        validateRestaurantOwner(product.getRestaurant(), userId);

        product.updateInfo(
                productRequest.getPName(),
                productRequest.getPDescription(),
                productRequest.getPPrice()
        );

        return ProductResponse.from(product);
    }

    //상품 삭제
    @Transactional
    public void deleteProduct(UUID pId,Long userId) {

        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new NotFoundException("해당 상품이 없습니다"));

        validateRestaurantOwner(product.getRestaurant(), userId);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);

    }


     // 특정 상품의 상태를 숨김(pStatus=false)으로 변경합니다.

    @Transactional
    public ProductResponse hideProduct(UUID pId, Long userId,boolean status) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new NotFoundException("해당 상품이 없습니다."));
        validateRestaurantOwner(product.getRestaurant(), userId);
        product.setHidden(status);
        return ProductResponse.from(product);
    }

    private void validateRestaurantOwner(RestaurantEntity restaurantEntity,Long userId) {
        if(!restaurantEntity.getUser().getId().equals(userId)) {
            throw new BadRequestException("해당 상품에 대한 수정/삭제 권한이 없습니다.");
        }
    }

    //상품 검색 페이징 및 오름차순 내림차순 정렬 (querydsl)
    @Transactional
    public Page<ProductResponse> searchProducts(UUID restaurantId, String keyword, Pageable pageable) {

        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. [검색 조건]: 레스토랑 ID 조건 추가
        if (restaurantId != null) {
            builder.and(product.restaurant.restaurantId.eq(restaurantId));
        }

        // 2. [검색 조건]: 키워드 검색 조건 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            builder.and(product.name.containsIgnoreCase(searchKeyword));
        }

        builder.and(product.deletedAt.isNull())
                .and(product.status.eq(true));

        // 'builder'는 검색 조건을, 'pageable'은 페이지 번호, 크기, 정렬 기준(ASC/DESC)을 포함한다.
        Page<Product> productPage = productRepository.findAll(builder, pageable);

        return productPage.map(ProductResponse::from);
    }
}
