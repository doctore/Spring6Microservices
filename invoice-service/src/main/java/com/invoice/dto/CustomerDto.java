package com.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invoice.configuration.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
        description = "Information related with a customer"
)
public class CustomerDto {

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
            description = "Customer address",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String address;

    @Schema(
            description = "Customer phone",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 16
    )
    private String phone;

    @Schema(
            description = "Customer email",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @Size(
            max = 64
    )
    private String email;

    @Schema(
            description = "When the customer was created",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @JsonFormat(
            pattern = Constants.DATETIME_FORMAT
    )
    private LocalDateTime createdAt;

}