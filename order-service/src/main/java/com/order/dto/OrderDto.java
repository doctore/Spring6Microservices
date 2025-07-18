package com.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.order.configuration.Constants;
import com.spring6microservices.common.spring.validator.group.CreateAction;
import com.spring6microservices.common.spring.validator.group.UpdateAction;
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
        of = {
                "code"
        }
)
@Data
@NoArgsConstructor
@Schema(
        description = "Information related with an order"
)
public class OrderDto {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = RequiredMode.AUTO
    )
    @NotNull(
            groups = {
                    UpdateAction.class
            }
    )
    private Integer id;

    @Schema(
            description = "Unique identifier of the order",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull(
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    @Size(
            min = 1,
            max = 64,
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    private String code;

    @Schema(
            description = "Customer identifier",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64,
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    private String customerCode;

    @Schema(
            description = "When the order was created",
            requiredMode = RequiredMode.AUTO
    )
    @JsonFormat(
            pattern = Constants.DATETIME_FORMAT
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "List of order lines",
            requiredMode = RequiredMode.REQUIRED
    )
    @Valid
    List<OrderLineDto> orderLines;

}
