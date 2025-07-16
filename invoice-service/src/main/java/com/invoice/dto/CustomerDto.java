package com.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.invoice.configuration.Constants;
import com.spring6microservices.common.spring.validator.group.CreateAction;
import com.spring6microservices.common.spring.validator.group.UpdateAction;
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
            requiredMode = RequiredMode.AUTO
    )
    @NotNull(
            groups = {
                    UpdateAction.class
            }
    )
    private Integer id;

    @Schema(
            description = "Unique identifier of the customer",
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
            description = "Customer address",
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
            max = 128,
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    private String address;

    @Schema(
            description = "Customer phone",
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
            max = 16,
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    private String phone;

    @Schema(
            description = "Customer email",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @Size(
            max = 64,
            groups = {
                    CreateAction.class,
                    UpdateAction.class
            }
    )
    private String email;

    @Schema(
            description = "When the customer was created",
            requiredMode = RequiredMode.AUTO
    )
    @JsonFormat(
            pattern = Constants.DATETIME_FORMAT
    )
    private LocalDateTime createdAt;

}