package com.mealhub.backend.ai.domain.entity;

import com.mealhub.backend.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "p_ai")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_id", nullable = false)
    private UUID aiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @Column(name = "ai_request", nullable = false, length = 10000)
    private String aiRequest;

    @Column(name = "ai_response", nullable = false, length = 10000)
    private String aiResponse;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static AiEntity of(User findUser, String aiRequest, String aiResponse) {
        AiEntity aiEntity = new AiEntity();
        aiEntity.user = findUser;
        aiEntity.aiRequest = aiRequest;
        aiEntity.aiResponse = aiResponse;
        return aiEntity;
    }
}
