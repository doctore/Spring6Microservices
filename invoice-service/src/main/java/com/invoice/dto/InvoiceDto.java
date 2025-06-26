package com.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invoice.configuration.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

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
        description = "Information related with am invoice"
)
public class InvoiceDto {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = RequiredMode.REQUIRED
    )
    private Integer id;

    @Schema(
            description = "Unique identifier of the customer",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String code;

    @Schema(
            description = "Related customer",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Valid
    CustomerDto customer;

    @Schema(
            description = "Order identifier with the elements included in the invoice",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    private Integer orderId;

    @Schema(
            description = "Total amount related with the invoice",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private double cost;

    @Schema(
            description = "When the invoice was created",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @JsonFormat(
            pattern = Constants.DATETIME_FORMAT
    )
    private LocalDateTime createdAt;

}
