package com.spring6microservices.common.spring.jms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

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
        description = "Information related with an event sent using JMS"
)
public final class EventDto<T> {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private String id;

    @Schema(
            description = "Specific data included in the event",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Map<String, Object> metadata;

    @Schema(
            description = "Content of the event",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private T body;

}
