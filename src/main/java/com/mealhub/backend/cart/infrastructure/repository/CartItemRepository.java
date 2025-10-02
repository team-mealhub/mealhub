package com.mealhub.backend.cart.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItemRepository, UUID> {
}
