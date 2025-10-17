package com.mealhub.backend.user.presentation.dto.response;

import com.mealhub.backend.user.domain.entity.User;
import com.mealhub.backend.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String userId;
    private String username;
    private String nickname;
    private UserRole role;
    private String phone;

    public UserResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.phone = user.getPhone();
    }
}
