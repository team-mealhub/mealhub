package com.mealhub.backend.user.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealhub.backend.user.application.service.AuthService;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"default", "test"})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private static final String ENDPOINT = "/v1/auth";

    private final UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
            "test",
            "김테스트",
            "테스트",
            "test1234!",
            UserRole.ROLE_CUSTOMER,
            "010-1234-5678"
    );

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {
        /* given */
        UserSignUpRequest request = userSignUpRequest;

        /* when */
        /* then */
        mockMvc.perform(post(ENDPOINT + "/signUp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 실패(중복된 아이디)")
    void signUp_Fail () throws Exception {
        /* given */
        UserSignUpRequest request = userSignUpRequest;

        authService.signUp(userSignUpRequest);

        /* when */
        /* then */
        mockMvc.perform(post(ENDPOINT + "/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인")
    void signIn () throws Exception {
        /* given */
        UserSignInRequest request = new UserSignInRequest(
                "test",
                "test1234!"
        );

        authService.signUp(userSignUpRequest);

        /* when */
        /* then */
        mockMvc.perform(post(ENDPOINT + "/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @DisplayName("로그인 실패(잘못된 아이디 또는 비밀번호)")
    void signIn_Fail() throws Exception {
        /* given */
        UserSignInRequest request = new UserSignInRequest(
                "test",
                "test1234!!"
        );

        /* when */
        /* then */
        mockMvc.perform(post(ENDPOINT + "/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String writeValueAsString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}