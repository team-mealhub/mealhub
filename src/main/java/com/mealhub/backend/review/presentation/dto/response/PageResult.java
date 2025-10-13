package com.mealhub.backend.review.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
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
