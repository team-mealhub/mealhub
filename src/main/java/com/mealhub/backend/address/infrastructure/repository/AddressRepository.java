package com.mealhub.backend.address.infrastructure.repository;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // 사용자 모든주소목록
    List<Address> findByUser(User user);

    //사용자 기본 주소
    Optional<Address> findByUserAndDefaultAddressTrue(User user);

    // 사용자 기본 주소 존재 여부
    boolean existsByUserAndDefaultAddressTrue(User user);

    // 사용자 주소 조회(id기준)
    Optional<Address> findByIdAndUser(UUID id, User user);

    // 사용자 주소 삭제(id기준)
    void deleteByIdAndUser(UUID id, User user);
}
