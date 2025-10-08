package com.mealhub.backend.product.domain.entity;

import com.mealhub.backend.global.domain.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "p_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Product extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "p_id", updatable = false, nullable = false)
    private UUID pId;
    @Column(name = "r_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rId;
    @Column(name = "p_name", length = 20, nullable = false)
    private String pName;
    @Column(name = "p_description", length = 255, nullable = true)
    private String pDescription;
    @Column(name = "p_price", nullable = false)
    private long pPrice;





    /* ==========================
          비즈니스 메서드
       ========================== */

    /**
     * 음식 정보 수정 (이름, 설명, 가격)
     */
    public void updateInfo(String name, String description, long price) {
        if (name != null && !name.isBlank()) {
            this.pName = name;
        }
        this.pDescription = description;
        this.pPrice = price;
    }


}
