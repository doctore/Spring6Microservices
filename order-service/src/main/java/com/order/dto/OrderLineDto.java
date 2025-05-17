package com.order.dto;

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
@Schema(description = "Information related with the lines of an order")
public class OrderLineDto {

    @Schema(description = "Internal unique identifier", requiredMode = RequiredMode.REQUIRED)
    private Integer id;

    @Schema(description = "Related order", requiredMode = RequiredMode.REQUIRED)
    private Integer orderId;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "The purchased item", requiredMode = RequiredMode.REQUIRED)
    private String concept;

    @Schema(description = "Number of items included as concept", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private int amount;

    @Schema(description = "Cost", requiredMode = RequiredMode.REQUIRED)
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
        return null == id
                ? (
                        orderId.equals(orderLine.orderId) &&
                                concept.equals(orderLine.concept)
                  )
                : id.equals(
                        orderLine.id
                  );
    }

    @Override
    public int hashCode() {
        return null == id
                ? Objects.hash(orderId) + Objects.hash(concept)
                : Objects.hash(id);
    }

}
