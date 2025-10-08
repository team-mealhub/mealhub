package com.mealhub.backend.product.infrastructure.repository;

import com.mealhub.backend.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    //음식 정보 삭제
    void deleteById(UUID pId);

    //음식 정보(단건) 조회
    Optional<Product> findById(UUID pId);

    //음식 정보 전체 조회
    List<Product> findAll();

    //음식 정보 수정
    Product save(Product product);


    //음식 숨김 처리(이용교 튜터님 말씀대로 STATUS로 처리 해서 보이면 TRUE 안 보이면 FALSE)
    List<Product> findAllByRIdAndStatus(UUID rId,boolean status);

    //(추가사항 : 인기가 높은 음식 순위로 조회하기? 나중에 추후 구현


    //추가 사항 : 평점이 높은 인기 음식 우선 순위로 올려두는거 조회?

    //



}
