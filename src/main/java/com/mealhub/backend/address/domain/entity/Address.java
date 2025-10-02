package com.mealhub.backend.address.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import com.mealhub.backend.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = "user")
public class Address extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "a_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @Column(name = "a_name", nullable = false, length = 255)
    private String name;

    @Column(name = "a_default", nullable = false)
    private boolean defaultAddress;

    @Column(name = "a_address", nullable = false, length = 255)
    private String address;

    @Column(name = "a_old_address", length = 255)
    private String oldAddress;

    @Column(name = "a_longitude")
    private Double longitude;

    @Column(name = "a_latitude")
    private Double latitude;

    @Column(name = "a_memo", length = 255)
    private String memo;

    @Builder
    private Address(User user, String name, boolean defaultAddress, String address,
                    String oldAddress, Double longitude, Double latitude, String memo) {
        this.user = user;
        this.name = name;
        this.defaultAddress = defaultAddress;
        this.address = address;
        this.oldAddress = oldAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.memo = memo;
    }

    public void update(String name, boolean defaultAddress, String address,
                       String oldAddress, Double longitude, Double latitude, String memo) {
        this.name = name;
        this.defaultAddress = defaultAddress;
        this.address = address;
        this.oldAddress = oldAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.memo = memo;
    }

    public void changeDefault(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
