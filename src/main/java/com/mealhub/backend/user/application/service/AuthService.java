package com.mealhub.backend.user.application.service;

import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.exception.UserDuplicateException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(UserSignUpRequest request) {
        String userId = request.getUserId();

        if (userRepository.existsByUserId(userId)) {
            throw new UserDuplicateException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.createUser(request, encodedPassword);
        userRepository.save(user);
    }
}
