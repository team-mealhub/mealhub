package com.mealhub.backend.address.application.service;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.address.infrastructure.repository.AddressRepository;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public AddressResponse create(User user, AddressRequest addressRequest) {
        if (addressRequest.isDefault() && addressRepository.existsByUserAndIsDefaultTrue(user)) {
            addressRepository.findByUserAndIsDefaultTrue(user)
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

    public AddressResponse getAddress(User user, UUID id) {
        Address address = addressRepository.findByAIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return toResponse(address);
    }

    public List<AddressResponse> getAllAddresses(User user) {
        return addressRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse updateAddress(User user, UUID id, AddressRequest addressRequest) {
        Address address = addressRepository.findByAIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (addressRequest.isDefault() && addressRepository.existsByUserAndIsDefaultTrue(user)) {
            addressRepository.findByUserAndIsDefaultTrue(user)
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

        return toResponse(address);
    }

    @Transactional
    public void deleteAddress(User user, UUID id) {
        addressRepository.deleteByAIdAndUser(id, user);
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
