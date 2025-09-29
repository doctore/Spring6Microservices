package com.spring6microservices.common.spring.jms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(
        of = {
                "id"
        }
)
@Data
@NoArgsConstructor
@Schema(
        description = "Information related with an order sent using JMS"
)
public class OrderEventDto {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer id;

    @Schema(
            description = "Customer identifier",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String customerCode;

    @Schema(
            description = "Cost",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private double cost;

}
