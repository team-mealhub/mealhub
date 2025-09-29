package com.mealhub.backend.global.infrastructure.config.security;

import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(username)
                .orElseThrow(
                        () -> new RuntimeException("Not Found User Temporary message")
                );

        return new UserDetailsImpl(user.getId(), user.getUserId(), user.getPassword(), user.getRole());
    }
}
