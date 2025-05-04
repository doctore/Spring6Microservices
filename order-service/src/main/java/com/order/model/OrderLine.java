package com.order.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderLine implements IModel, Serializable {

    @Serial
    private static final long serialVersionUID = -162314489828209397L;

    private Integer id;

    @NotNull
    private Order order;

    @NotNull
    @Size(min = 1, max = 255)
    private String concept;

    @NotNull
    @Positive
    private int amount;

    @NotNull
    @Positive
    private double cost;


    @Override
    public boolean isNew() {
        return null == id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderLine orderLine = (OrderLine) o;
        return null == id
                ? (
                        order.getId().equals(orderLine.order.getId()) &&
                                concept.equals(orderLine.concept)
                  )
                : id.equals(
                        orderLine.id
                  );
    }

    @Override
    public int hashCode() {
        return null == id
                ? Objects.hash(order) + Objects.hash(concept)
                : Objects.hash(id);
    }

}
