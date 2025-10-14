package com.mealhub.backend.address.application.service;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.address.infrastructure.repository.AddressRepository;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;

    // 새 주소 생성(기본주소로 설정하면 기존기본주소 해제
    @Transactional
    public AddressResponse create(User user, AddressRequest addressRequest) {
        if (addressRequest.isDefault() && addressRepository.existsByUserAndDefaultAddressTrue(user)) {
            addressRepository.findByUserAndDefaultAddressTrue(user)
                    .ifPresent(address -> address.changeDefault(false));
        }

        Address address = Address.builder()
                .user(user)
                .name(addressRequest.getName())
                .defaultAddress(addressRequest.isDefault())
                .address(addressRequest.getAddress())
                .oldAddress(addressRequest.getOldAddress())
                .longitude(addressRequest.getLongitude())
                .latitude(addressRequest.getLatitude())
                .memo(addressRequest.getMemo())
                .build();

        return toResponse(addressRepository.save(address));

    }

    // 주소 조회(단일)
    public AddressResponse getAddress(User user, UUID id) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return toResponse(address);
    }

    // 전체 주소 조회(+검색, 키워드 없으면 전체 조회)
    public List<AddressResponse> getAllAddresses(User user, String keyword) {
        List<Address> result;
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = addressRepository.searchAddress(user, keyword.trim());
        } else {
            result = addressRepository.findByUser(user);
        }
        return result
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //주소 수정(기본주소로 설정하면 기존기본주소 해제
    @Transactional
    public AddressResponse updateAddress(User user, UUID id, AddressRequest addressRequest) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (addressRequest.isDefault() && addressRepository.existsByUserAndDefaultAddressTrue(user)) {
            addressRepository.findByUserAndDefaultAddressTrue(user)
                    .ifPresent(address1 -> address1.changeDefault(false));
        }

        address.update(
                addressRequest.getName(),
                addressRequest.isDefault(),
                addressRequest.getAddress(),
                addressRequest.getOldAddress(),
                addressRequest.getLongitude(),
                addressRequest.getLatitude(),
                addressRequest.getMemo()
        );

        addressRepository.save(address);

        return toResponse(address);
    }

    // 주소 삭제
    @Transactional
    public void deleteAddress(User user, UUID id) {
        addressRepository.deleteByIdAndUser(id, user);
    }

    // 기존 주소 중 하나를 기본주소로 설정
    @Transactional
    public AddressResponse changeDefault(User user, UUID addressId) {
        addressRepository.findByUserAndDefaultAddressTrue(user)
                .ifPresent(address -> address.changeDefault(false));

        Address target = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        target.changeDefault(true);
        return toResponse(target);
    }



    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getName(),
                address.isDefaultAddress(),
                address.getAddress(),
                address.getOldAddress(),
                address.getLongitude(),
                address.getLatitude(),
                address.getMemo()
        );
    }

}
