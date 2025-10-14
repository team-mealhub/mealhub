package com.mealhub.backend.user.application.service;

import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.exception.UserNotFoundException;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import com.mealhub.backend.user.presentation.dto.request.UserUpdateRequest;
import com.mealhub.backend.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = getUserById(id);
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        user.updateUser(request, encodedPassword);
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse deleteUser(Long id) {
        User user = getUserById(id);

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(id);

        return new UserResponse(user);
    }

    private User getUserById(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
