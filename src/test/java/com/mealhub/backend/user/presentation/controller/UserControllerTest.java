package com.mealhub.backend.user.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealhub.backend.user.application.service.UserService;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.domain.exception.UserNotFoundException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.libs.MockUser;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import com.mealhub.backend.user.presentation.dto.request.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"default", "test"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String ENDPOINT = "/v1/user";

    // MockUser 기본 값
    private Long id = 1L;
    private final String userId = "test";
    private final String password = "test1234!";
    private final UserRole role = UserRole.ROLE_CUSTOMER;

    private final String username = "김테스트";
    private final String nickname = "테스트";
    private final String phone = "010-1234-5678";

    @BeforeEach
    void setup() {
        UserSignUpRequest request = new UserSignUpRequest(
                userId,
                username,
                nickname,
                password,
                role,
                phone
        );

        User user = User.createUser(request, "encodedPassword");
        userRepository.save(user);
    }

    @Test
    @DisplayName("로그인한 유저 정보 조회")
    @MockUser
    @DirtiesContext
    void getCurrentUser() throws Exception {
        /* given */
        /* when */
        /* then */
        mockMvc.perform(get(ENDPOINT + "/me"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.userId").value(userId),
                        jsonPath("$.username").value(username),
                        jsonPath("$.nickname").value(nickname),
                        jsonPath("$.role").value(role.name()),
                        jsonPath("$.phone").value(phone)
                );
    }

    @Test
    @DisplayName("유저 ID로 유저 정보 조회")
    @MockUser(role = UserRole.ROLE_MANAGER)
    @DirtiesContext
    void getUser() throws Exception {
        /* given */
        Long targetId = 1L;

        /* when */
        /* then */
        mockMvc.perform(get(ENDPOINT + "/{userId}", targetId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.userId").value(userId),
                        jsonPath("$.username").value(username),
                        jsonPath("$.nickname").value(nickname),
                        jsonPath("$.role").value(role.name()),
                        jsonPath("$.phone").value(phone)
                );
    }

    @Test
    @DisplayName("로그인한 유저 정보 수정")
    @MockUser
    @DirtiesContext
    void updateUser() throws Exception {
        /* given */
        String modifiedUsername = "김테스트수정";
        String modifiedNickname = "테스트수정";
        String modifiedPassword = "test1234@";
        String modifiedPhone = "010-5678-1234";

        UserUpdateRequest request = new UserUpdateRequest(
                modifiedUsername,
                modifiedNickname,
                modifiedPassword,
                modifiedPhone
        );

        /* when */
        /* then */
        mockMvc.perform(put(ENDPOINT)
                        .contentType("application/json")
                        .content(writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.userId").value(userId),
                        jsonPath("$.username").value(modifiedUsername),
                        jsonPath("$.nickname").value(modifiedNickname),
                        jsonPath("$.role").value(role.name()),
                        jsonPath("$.phone").value(modifiedPhone)
                );
    }

    @Test
    @DisplayName("로그인한 유저 탈퇴")
    @MockUser
    @DirtiesContext
    void deleteUser() throws Exception {
        /* given */
        /* when */
        /* then */
        mockMvc.perform(delete(ENDPOINT))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.userId").value(userId),
                        jsonPath("$.username").value(username),
                        jsonPath("$.nickname").value(nickname),
                        jsonPath("$.role").value(role.name()),
                        jsonPath("$.phone").value(phone)
                );

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
    }

    private String writeValueAsString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}