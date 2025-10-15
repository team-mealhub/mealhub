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

    List<Product> findAllByRestaurantRestaurantIdAndStatus(UUID rId,boolean status);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByRestaurantRestaurantId(UUID rId, Pageable pageable);

    Page<Product> findByRestaurantRestaurantIdAndNameContainingIgnoreCase(UUID rId, String keyword, Pageable pageable);
}