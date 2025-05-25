package com.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(
        of = { "code" }
)
@Data
@NoArgsConstructor
@Schema(
        description = "Information related with an order"
)
public class OrderDto {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = RequiredMode.REQUIRED
    )
    private Integer id;

    @Schema(
            description = "Unique identifier of the order",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String code;

    @Schema(
            description = "When the order was created",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    private LocalDateTime createdAt;

    @Schema(
            description = "List of order lines",
            requiredMode = RequiredMode.REQUIRED
    )
    @Valid
    List<OrderLineDto> orderLines;

}
