package com.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Schema(
        description = "Information related with the lines of an order"
)
public class OrderLineDto {

    @Schema(
            description = "Internal unique identifier",
            requiredMode = RequiredMode.AUTO
    )
    @NotNull
    private int id;

    @Schema(
            description = "The purchased item",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 255
    )
    private String concept;

    @Schema(
            description = "Number of items included as concept",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private int amount;

    @Schema(
            description = "Cost",
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private double cost;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderLineDto orderLine = (OrderLineDto) o;
        return id == orderLine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
