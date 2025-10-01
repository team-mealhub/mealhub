package com.mealhub.backend.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private UUID rId; // UUID (문자열로 받아서 변환)
    private String name;
    private String description;
    private Long price;
}