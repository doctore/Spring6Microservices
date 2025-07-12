package com.spring6microservices.common.spring.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(
        of = { "property" }
)
@Data
@NoArgsConstructor
@Schema(
        description = "Sort option to search information in the storage"
)
public class SortDto {

    @Schema(
            description = "Property used to sort the results",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String property;

    @Schema(
            description = "True if the sorting is ascending according to the property, descending otherwise",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean isAscending;

}