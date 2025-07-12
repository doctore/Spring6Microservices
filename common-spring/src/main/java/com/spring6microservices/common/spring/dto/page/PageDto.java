package com.spring6microservices.common.spring.dto.page;

import com.spring6microservices.common.core.util.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(
        of = {
                "page",
                "size"
        }
)
@Data
@NoArgsConstructor
@Schema(
        description = "Properties for searching for data information in storage using pagination"
)
public class PageDto {

    @Schema(
            description = "The page to be returned",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int page;

    @Schema(
            description = "The number of items to be returned",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Positive
    private int size;

    @Schema(
            description = "The sorting parameters",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    List<SortDto> sort;


    /**
     * Transforms this {@link PageDto} into a {@link Pageable} instance.
     *
     * @return {@link Pageable}
     */
    public Pageable toPageable() {
        if (CollectionUtil.isEmpty(sort)) {
            return PageRequest.of(
                    page,
                    size
            );
        }
        List<Sort.Order> orders = sort.stream()
                .map(s ->
                        s.isAscending()
                                ? Sort.Order.asc(
                                        s.getProperty()
                                  )
                                : Sort.Order.desc(
                                        s.getProperty()
                                  )
                )
                .collect(
                        toList()
                );

        return PageRequest.of(
                page,
                size,
                Sort.by(
                        orders
                )
        );
    }

}
