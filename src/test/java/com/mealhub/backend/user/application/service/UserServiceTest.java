package com.mealhub.backend.user.application.service;

import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.domain.exception.UserNotFoundException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import com.mealhub.backend.user.presentation.dto.request.UserUpdateRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final Long id = 1L;
    private final String userId = "test";

    private final UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
            userId,
            "김테스트",
            "테스트",
            "test1234!",
            UserRole.ROLE_CUSTOMER,
            "010-1234-5678"
    );

    private final User user = User.createUser(userSignUpRequest, "encodedPassword");

    @BeforeEach
    void setup() {
        user.setId(id);
    }

    @Test
    @DisplayName("유저 정보 조회 성공")
    void getUser_Success() {
        /* given */
        Long userId = 1L;

        /* when */
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        /* then */
        UserResponse response = userService.getUser(userId);

        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUserId()).isEqualTo(user.getUserId());

        verify(userRepository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 실패 (존재하지 않는 유저)")
    void getUser_Fail_NotFound () {
        /* given */
        Long userId = 1L;

        /* when */
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        /* then */
        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void updateUser() {
        /* given */
        String modifiedUsername = "김테스트수정";
        String modifiedNickname = "테스트수정";
        String modifiedPassword = "test1234@";
        String modifiedPhone = "010-5678-1234";

        var request = new UserUpdateRequest(
                modifiedUsername,
                modifiedNickname,
                modifiedPassword,
                modifiedPhone
        );

        /* when */
        when(userRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(modifiedPassword)).thenReturn("encodedPassword");

        /* then */
        UserResponse response = userService.updateUser(id, request);

        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUserId()).isEqualTo(user.getUserId());
        assertThat(response.getUsername()).isEqualTo(modifiedUsername);
        assertThat(response.getNickname()).isEqualTo(modifiedNickname);
        assertThat(response.getPhone()).isEqualTo(modifiedPhone);
    }

    @Test
    @DisplayName("유저 탈퇴 성공")
    void deleteUser() {
        /* given */
        /* when */
        when(userRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(user));

        /* then */
        UserResponse response = userService.deleteUser(id);

        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUserId()).isEqualTo(user.getUserId());

        assertNotNull(user.getDeletedAt());
        assertThat(user.getDeletedBy()).isEqualTo(id);
    }
}