package com.mealhub.backend.user.application.service;

import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtUtil;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.domain.exception.UserAuthenticationException;
import com.mealhub.backend.user.domain.exception.UserDuplicateException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private final UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
            "test",
            "김테스트",
            "테스트",
            "test1234!",
            UserRole.ROLE_CUSTOMER,
            "010-1234-5678"
    );

    private final User user = User.createUser(userSignUpRequest, "encodedPassword");

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        /* given */
        UserSignUpRequest request = userSignUpRequest;

        /* when */
        when(userRepository.existsByUserId(request.getUserId())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        /* then */
        assertDoesNotThrow(() -> authService.signUp(request));
        verify(userRepository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 (중복)")
    void signUp_Fail_Duplicate () {
        /* given */
        UserSignUpRequest request = userSignUpRequest;

        /* when */
        when(userRepository.existsByUserId(request.getUserId())).thenReturn(true);

        /* then */
        assertThrows(UserDuplicateException.class, () -> authService.signUp(request));
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success () {
        /* given */
        UserSignInRequest request = new UserSignInRequest(
                "test",
                "test1234!"
        );

        String accessToken = "accessToken";

        /* when */
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(jwtUtil.generateAccessToken(anyString(), any(UserRole.class))).thenReturn(accessToken);

        /* then */
        assertThat(authService.signIn(request)).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("로그인 실패(아이디 불일치)")
    void signIn_Fail_Mismatch_UserId() {
        /* given */
        UserSignInRequest request = new UserSignInRequest(
                "wrongId",
                "test1234!"
        );

        /* when */
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        /* then */
        assertThrows(UserAuthenticationException.class, () -> authService.signIn(request));
    }

    @Test
    @DisplayName("로그인 실패(비밀번호 불일치)")
    void signIn_Fail_Mismatch_Password () throws Exception {
        /* given */
        UserSignInRequest request = new UserSignInRequest(
                "test",
                "wrongPassword"
        );

        /* when */
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.FALSE);

        /* then */
        assertThrows(UserAuthenticationException.class, () -> authService.signIn(request));
    }
}