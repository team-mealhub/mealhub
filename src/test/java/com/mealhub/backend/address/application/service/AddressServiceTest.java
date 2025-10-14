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
    @DisplayName("주소 생성 성공")
    void createAddress() {
        AddressRequest addressRequest = new AddressRequest("새로운 집", true, "새 로도명", null, 127.0, 37.0, "메모");

        Address address = Address.builder()
                .user(mockUser)
                .defaultAddress(true)
                .build();

        when(addressRepository.existsByUserAndDefaultAddressTrue(any(User.class))).thenReturn(true);
        when(addressRepository.findByUserAndDefaultAddressTrue(any(User.class))).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

        addressService.create(mockUser, addressRequest);

        assertThat(address.isDefaultAddress()).isFalse();
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("주소 단일 조회")
    void getAddress() {
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
    @DisplayName("주소 전체 조회")
    void getAllAddresses() {
        when(addressRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(mockAddress));

        List<AddressResponse> addresses = addressService.getAllAddresses(mockUser, null);

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getName()).isEqualTo("우리 집");

        verify(addressRepository, times(1)).findByUser(mockUser);
        verify(addressRepository, never()).searchAddress(any(), anyString());
    }

    @Test
    @DisplayName("주소 수정 성공")
    void updateAddress() {
        AddressRequest addressRequest = new AddressRequest("회사", false, "서울시 서초구", null, 127.1, 36.9, "출근용");
        when(addressRepository.findByIdAndUser(mockAddressId, mockUser)).thenReturn(Optional.of(mockAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(mockAddress);

        AddressResponse addressResponse = addressService.updateAddress(mockUser, mockAddressId, addressRequest);

        assertThat(addressResponse.getName()).isEqualTo("회사");
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress() {
        UUID id = mockAddressId;

        doNothing().when(addressRepository).deleteByIdAndUser(id, mockUser);

        addressService.deleteAddress(mockUser, id);

        verify(addressRepository, times(1)).deleteByIdAndUser(id, mockUser);

    }

    @Test
    @DisplayName("주소 검색 성공")
    void searchAddresses() {
        when(addressRepository.searchAddress(mockUser, "우리")).thenReturn(Collections.singletonList(mockAddress));

        List<AddressResponse> result = addressService.getAllAddresses(mockUser, "우리");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("우리 집");

        verify(addressRepository, times(1)).searchAddress(mockUser, "우리");
        verify(addressRepository, never()).findByUser(any());
    }


    @Test
    @DisplayName("기본 주소 변경 성공")
    void changeDefaultAddress() {
        when(addressRepository.findByIdAndUser(mockAddressId, mockUser)).thenReturn(Optional.of(mockAddress));
        when(addressRepository.findByUserAndDefaultAddressTrue(mockUser)).thenReturn(Optional.of(mockAddress));

        AddressResponse response = addressService.changeDefault(mockUser, mockAddressId);

        assertThat(response.isDefault()).isTrue();
        verify(addressRepository, times(1)).findByUserAndDefaultAddressTrue(mockUser);
        verify(addressRepository, times(1)).findByIdAndUser(mockAddressId, mockUser);
    }


}

