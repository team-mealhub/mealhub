package com.mealhub.backend.user.application.service;

import com.mealhub.backend.global.infrastructure.config.security.jwt.JwtUtil;
import com.mealhub.backend.user.application.dto.SignInResult;
import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.domain.exception.InvalidUserRoleException;
import com.mealhub.backend.user.domain.exception.UserAuthenticationException;
import com.mealhub.backend.user.domain.exception.UserDuplicateException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserSignInRequest;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signUp(UserSignUpRequest request) {
        if (request.getRole().equals(UserRole.ROLE_MANAGER)) {
            throw new InvalidUserRoleException();
        }

        String userId = request.getUserId();

        if (userRepository.existsByUserId(userId)) {
            throw new UserDuplicateException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.createUser(request, encodedPassword);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public SignInResult signIn(UserSignInRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(UserAuthenticationException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserAuthenticationException();
        }

        String token = jwtUtil.generateAccessToken(user.getUserId(), user.getRole());
        UserResponse response = new UserResponse(user);

        return new SignInResult(token, response);
    }
}
