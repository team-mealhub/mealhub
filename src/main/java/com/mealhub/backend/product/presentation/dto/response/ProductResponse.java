package com.mealhub.backend.product.presentation.dto.response;

import com.mealhub.backend.product.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder // DTO 생성을 위해 Builder 패턴 추가
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID pId;
    private UUID rId;
    private String pName;
    private String pDescription;
    private long pPrice;


    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .pId(product.getPId())
                .rId(product.getRId())
                .pName(product.getPName())
                .pDescription(product.getPDescription())
                .pPrice(product.getPPrice())
                .build();
    }

    // BaseAuditEntity 필드는 필요에 따라 추가

}
