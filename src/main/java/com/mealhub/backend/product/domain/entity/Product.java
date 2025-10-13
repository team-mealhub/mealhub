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

    // @ManyToOne(fetch = FetchType.LAZY) <-- 제거
    // @GeneratedValue(strategy = GenerationType.UUID) <-- 제거
    @Column(name = "r_id", nullable = false) // 일반 컬럼으로 매핑
    private UUID rId;

    @Column(name = "p_name", length = 20, nullable = false)
    private String pName;

    @Column(name = "p_description", length = 255, nullable = true)
    private String pDescription;

    @Column(name = "p_price", nullable = false)
    private long pPrice;

    @Column(name = "p_status", nullable = false)
    private boolean pStatus;
    /**
     * 정적 팩토리 메서드: DTO로부터 새로운 Product 엔티티를 생성합니다.
     */
    public static Product createProduct(UUID rId, String pName, String pDescription, long pPrice, boolean pStatus) {
        return Product.builder()
                .rId(rId)
                .pName(pName)
                .pDescription(pDescription)
                .pPrice(pPrice)
                .pStatus(pStatus)
                .build();
    }

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

    /**
     * 음식 숨김 상태 처리 (true = 음식이 보여짐 , false = 음식 숨김 처리)
     */
    public void setHidden(boolean isHidden) {
        // isHidden이 true일 때 pStatus는 false가 되어야 함 (숨김)
        this.pStatus = !isHidden;
    }


}
