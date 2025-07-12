package com.spring6microservices.common.spring.dto.page;

import com.spring6microservices.common.core.util.CollectionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PageDtoTest {

    @Test
    @DisplayName("toPageable: when null Sort is given then no sort is configured")
    public void toPageable_whenNullSortIsGiven_thenNoSortIsConfigured() {
        PageDto pageDto = buildPageDto(
                1,
                2,
                null
        );

        Pageable result = pageDto.toPageable();

        assertEquals(
                pageDto.getPage(),
                result.getPageNumber()
        );
        assertEquals(
                pageDto.getSize(),
                result.getPageSize()
        );
        assertEquals(
                Sort.unsorted(),
                result.getSort()
        );
    }


    @Test
    @DisplayName("toPageable: when not null sort is given then it will be included in the result")
    public void toPageable_whenNotNullSortIsGiven_thenItWillBeIncludedInPageRequest() {
        SortDto sortDto = buildSortDto(
                "property1",
                true
        );
        PageDto pageDto = buildPageDto(
                1,
                2,
                List.of(
                        sortDto
                )
        );

        Pageable result = pageDto.toPageable();

        assertEquals(
                pageDto.getPage(),
                result.getPageNumber()
        );
        assertEquals(
                pageDto.getSize(),
                result.getPageSize()
        );
        Sort.Order order = result.getSort()
                .getOrderFor(
                        sortDto.getProperty()
                );
        assertNotNull(order);
        assertEquals(
                pageDto.getSort().getFirst().getProperty(),
                order.getProperty()
        );
        assertEquals(
                pageDto.getSort().getFirst().isAscending(),
                order.isAscending()
        );
    }


    public static PageDto buildPageDto(final int page,
                                       final int size,
                                       final Collection<SortDto> sortDtos) {
        return PageDto.builder()
                .page(page)
                .size(size)
                .sort(
                        CollectionUtil.isEmpty(sortDtos)
                                ? new ArrayList<>()
                                : new ArrayList<>(sortDtos)
                )
                .build();
    }


    public static SortDto buildSortDto(final String property,
                                       final boolean isAscending) {
        return SortDto.builder()
                .property(property)
                .isAscending(isAscending)
                .build();
    }

}
