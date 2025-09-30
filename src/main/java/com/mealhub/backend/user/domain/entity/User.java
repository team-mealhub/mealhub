package com.mealhub.backend.user.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.user.domain.enums.UserRole;
import com.mealhub.backend.user.presentation.dto.request.UserSignUpRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "p_user")
@NoArgsConstructor
@Getter
public class User extends BaseAuditEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    @Setter
    private Long id;

    @Column(name = "u_user_id", nullable = false, unique = true, length = 100)
    @Setter
    private String userId;

    @Column(name = "u_username", length = 100)
    private String username;

    @Column(name = "u_nickname", length = 100)
    private String nickname;

    @Column(name = "u_password", nullable = false)
    @Setter
    private String password;

    @Column(name = "u_role", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    @Setter
    private UserRole role;

    @Column(name = "u_phone", length = 11)
    private String phone;

    private User(String userId, String username, String nickname, String password, UserRole role, String phone) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    public static User createUser(UserSignUpRequest request, String encodedPassword) {
        return new User(
                request.getUserId(),
                request.getUsername(),
                request.getNickname(),
                encodedPassword,
                request.getRole(),
                request.getPhone()
        );
    }
}
