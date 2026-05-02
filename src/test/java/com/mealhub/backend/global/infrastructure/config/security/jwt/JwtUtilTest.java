package com.mealhub.backend.global.infrastructure.config.security.jwt;

import com.mealhub.backend.user.domain.enums.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT 유틸리티 보안 테스트")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private String testUserId;
    private UserRole testRole;

    @BeforeEach
    void setUp() {
        testUserId = "testUser123";
        testRole = UserRole.ROLE_CUSTOMER;
    }

    @Test
    @DisplayName("JWT 토큰 생성 - 정상")
    void generateAccessToken_Success() {
        // when
        String token = jwtUtil.generateAccessToken(testUserId, testRole);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("JWT 토큰 검증 - 유효한 토큰")
    void validateToken_ValidToken() {
        // given
        String token = jwtUtil.generateAccessToken(testUserId, testRole);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("JWT 토큰 검증 - 잘못된 형식의 토큰")
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.format";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰 검증 - null 토큰")
    void validateToken_NullToken() {
        // when
        boolean isValid = jwtUtil.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰 검증 - 빈 문자열 토큰")
    void validateToken_EmptyToken() {
        // when
        boolean isValid = jwtUtil.validateToken("");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자 정보 추출 - 정상")
    void getUserInfoFromToken_Success() {
        // given
        String token = jwtUtil.generateAccessToken(testUserId, testRole);

        // when
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(testUserId);
        assertThat(claims.get("role", String.class)).isEqualTo(testRole.name());
    }

    @Test
    @DisplayName("HTTP 요청 헤더에서 JWT 토큰 추출 - Bearer 토큰 포함")
    void getJwtFromHeader_WithBearerToken() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = jwtUtil.generateAccessToken(testUserId, testRole);
        request.addHeader("Authorization", "Bearer " + token);

        // when
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // then
        assertThat(extractedToken).isNotNull();
        assertThat(extractedToken).isEqualTo(token);
    }

    @Test
    @DisplayName("HTTP 요청 헤더에서 JWT 토큰 추출 - Bearer 접두어 없음")
    void getJwtFromHeader_WithoutBearerPrefix() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = jwtUtil.generateAccessToken(testUserId, testRole);
        request.addHeader("Authorization", token);

        // when
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // then
        assertThat(extractedToken).isNull();
    }

    @Test
    @DisplayName("HTTP 요청 헤더에서 JWT 토큰 추출 - Authorization 헤더 없음")
    void getJwtFromHeader_NoAuthorizationHeader() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String extractedToken = jwtUtil.getJwtFromHeader(request);

        // then
        assertThat(extractedToken).isNull();
    }

    @Test
    @DisplayName("JWT 토큰 만료 시간 검증")
    void validateToken_ExpirationCheck() {
        // given
        String token = jwtUtil.generateAccessToken(testUserId, testRole);

        // when
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        // then
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("JWT 토큰 - 다양한 역할 검증")
    void validateToken_DifferentRoles() {
        // given
        UserRole[] roles = {UserRole.ROLE_CUSTOMER, UserRole.ROLE_OWNER, UserRole.ROLE_MANAGER};

        for (UserRole role : roles) {
            // when
            String token = jwtUtil.generateAccessToken(testUserId, role);
            Claims claims = jwtUtil.getUserInfoFromToken(token);

            // then
            assertThat(claims.get("role", String.class)).isEqualTo(role.name());
        }
    }
}
