package com.mealhub.backend.product.infrastructure.repository;

import com.mealhub.backend.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    //음식 정보 삭제


    //음식 정보 조회


    //음식 정보 수정


    //사용자가 주문한 음식 정보 조회


    //음식 숨김 처리(이용교 튜터님 말씀대로 STATUS로 처리 해서 보이면 TRUE 안 보이면 FALSE)


    //(추가사항 : 인기가 높은 음식 순위로 조회하기? 나중에 추후 구현


    //



}
