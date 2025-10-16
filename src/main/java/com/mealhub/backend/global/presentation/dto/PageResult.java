package com.mealhub.backend.global.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {

    @Schema(description = "페이지 콘텐츠")
    private List<T> content;

    @Schema(description = "전체 페이지 수", example = "12")
    private int totalPages;

    @Schema(description = "전체 요소 수", example = "113")
    private long totalElements;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "현재 페이지(0-base)", example = "0")
    private int number;

    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    public static <T> PageResult<T> of(Page<T> p) {
        return new PageResult<>(
                p.getContent(),
                p.getTotalPages(),
                p.getTotalElements(),
                p.getSize(),
                p.getNumber(),
                p.isFirst(),
                p.isLast()
        );
    }
}
