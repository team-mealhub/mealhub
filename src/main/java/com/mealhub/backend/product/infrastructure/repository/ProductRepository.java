package com.mealhub.backend.product.infrastructure.repository;

import com.mealhub.backend.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

//    List<Product> findAllByRIdAndStatus(UUID rId,boolean status);
    List<Product> findAllByRestaurantRestaurantIdAndStatus(UUID rId,boolean status);

//    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // rId만 있는 경우 (keyword가 null) - 음식 정보 검색
//    Page<Product> findByRestaurantId(UUID rId, Pageable pageable);
    Page<Product> findByRestaurantRestaurantId(UUID rId, Pageable pageable);

    // rId와 keyword 모두 있는 경우 - 음식 정보 검색
//    Page<Product> findByRestaurantIdAndNameContainingIgnoreCase(UUID rId, String keyword, Pageable pageable);
    Page<Product> findByRestaurantRestaurantIdAndNameContainingIgnoreCase(UUID rId, String keyword, Pageable pageable);


    //(추가사항 : 인기가 높은 음식 순위로 조회하기? 나중에 추후 구현


    //추가 사항 : 리뷰 평점이 높은 인기 음식 우선 순위로 올려두는거 조회?





}