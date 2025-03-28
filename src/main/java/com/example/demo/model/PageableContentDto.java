package com.example.demo.model;

import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
public record PageableContentDto<S>(
        long totalEntries,
        int totalNumberOfPages,
        int pageNumber,
        List<S> content
) {
    public static <T, S> PageableContentDto<S> from(Page<T> page, Pageable pageable, List<S> content) {
        return new PageableContentDto<>(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(), content);
    }
}
