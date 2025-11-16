package com.spring6microservices.common.core.dto;

import com.spring6microservices.common.core.functional.Cloneable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class PizzaDto implements Cloneable<PizzaDto> {

    private String name;
    private Double cost;


    @Override
    public PizzaDto clone() {
        return new PizzaDto(
                this.name,
                this.cost
        );
    }

}


