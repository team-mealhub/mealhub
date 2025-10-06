package com.mealhub.backend.user.infrastructure.repository;

import com.mealhub.backend.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
