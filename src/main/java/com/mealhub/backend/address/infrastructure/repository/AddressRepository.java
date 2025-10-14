package com.mealhub.backend.address.infrastructure.repository;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // 사용자 모든주소목록 (페이징 적용)
    Page<Address> findByUserAndDeletedFalse(User user, Pageable pageable);

    //사용자 기본 주소
    Optional<Address> findByUserAndDefaultAddressTrueAndDeletedFalse(User user);

    // 사용자 기본 주소 존재 여부
    boolean existsByUserAndDefaultAddressTrueAndDeletedFalse(User user);

    // 사용자 주소 조회(id기준)
    Optional<Address> findByIdAndUserAndDeletedFalse(UUID id, User user);

    // soft-delete
    @Modifying
    @Query("UPDATE Address a SET a.deleted = true WHERE a.id = :id AND a.user = :user AND a.deleted = false")
    void softDeleteByIdAndUser(@Param("id") UUID id, @Param("user") User user);


    // 검색 기능(keyword포함 된 주소 조회 / 페이징 적용)
    @Query("""
SELECT a FROM Address a WHERE a.user = :user AND a.deleted = false
AND (
LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
OR LOWER(a.oldAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))
)
""")
    Page<Address> searchAddress(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);
}
