package com.order.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@EqualsAndHashCode(
        of = {
                "code"
        }
)
@NoArgsConstructor
@ToString(
        exclude = "orderLines"
)
public class Order implements IModel, Serializable {

    @Serial
    private static final long serialVersionUID = -2334294382987163151L;

    private Integer id;

    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String code;

    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String customerCode;

    @NotNull
    private LocalDateTime createdAt;

    private List<OrderLine> orderLines;


    @Override
    public boolean isNew() {
        return null == id;
    }

}
