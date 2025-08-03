package com.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invoice.configuration.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @NotNull
    private int id;

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
            description = "Customer identifier",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
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


    public void addOrderLine(OrderLineDto orderLineDto) {
        if (null == this.orderLines) {
            this.orderLines = new ArrayList<>();
        }
        this.orderLines.add(
                orderLineDto
        );
    }

}
