package com.invoice.dto.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.invoice.TestDataFactory.buildPageDto;
import static com.invoice.TestDataFactory.buildSortDto;
import static org.junit.jupiter.api.Assertions.*;

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

}
