package com.mealhub.backend.address.application.service;

import com.mealhub.backend.address.domain.entity.Address;
import com.mealhub.backend.address.infrastructure.repository.AddressRepository;
import com.mealhub.backend.address.presentation.dto.request.AddressRequest;
import com.mealhub.backend.address.presentation.dto.response.AddressResponse;
import com.mealhub.backend.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService 단위 테스트")
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private User mockUser;
    private Address mockAddress;
    private final UUID mockAddressId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockAddress = Address.builder()
                .user(mockUser)
                .name("우리 집")
                .address("서울시 강남구")
                .defaultAddress(true)
                .build();
    }

    @Test
    @DisplayName("새로운 주소 생성 시 기존의 기본주소가 해제됨")
    void createNewAddress_ExistingDefault() {
        AddressRequest newDefaultRequest = new AddressRequest(
                "새로운 집", true, "새 로도명", null, 127.0, 37.0, "메모");

        Address existingDefaultAddress = Address.builder()
                .user(mockUser)
                .defaultAddress(true)
                .build();

        when(addressRepository.existsByUserAndDefaultAddressTrue(any(User.class))).thenReturn(true);
        when(addressRepository.findByUserAndDefaultAddressTrue(any(User.class))).thenReturn(Optional.of(existingDefaultAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

        addressService.create(mockUser, newDefaultRequest);

        assertThat(existingDefaultAddress.isDefaultAddress()).isFalse();
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("유효한 ID로 주소 조회 성공")
    void getAddressById_returnAddress() {
        when(addressRepository.findByIdAndUser(mockAddressId, mockUser)).thenReturn(Optional.of(mockAddress));

        AddressResponse addressResponse = addressService.getAddress(mockUser, mockAddressId);

        assertThat(addressResponse.getName()).isEqualTo("우리 집");
        assertThat(addressResponse.isDefault()).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 ID 조회 시 예외 발생")
    void getAddressByInvalidId_throwsException() {
        when(addressRepository.findByIdAndUser(any(UUID.class), any(User.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressService.getAddress(mockUser, UUID.randomUUID()));
    }

    @Test
    @DisplayName("모든 주소 조회 성공")
    void getAllAddresses_returnAllAddresses() {
        when(addressRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(mockAddress));

        List<AddressResponse> addresses = addressService.getAllAddresses(mockUser);

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getName()).isEqualTo("우리 집");
    }
}
