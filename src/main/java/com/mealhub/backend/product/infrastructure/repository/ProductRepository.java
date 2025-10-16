package com.mealhub.backend.product.infrastructure.repository;

import com.mealhub.backend.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;


public interface ProductRepository extends JpaRepository<Product, UUID>, QuerydslPredicateExecutor<Product> {



}