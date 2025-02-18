package com.spring6microservices.common.spring.dto;

import com.spring6microservices.common.spring.enums.PizzaEnum;
import com.spring6microservices.common.spring.validator.enums.EnumHasInternalStringValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class PizzaDto {

    @EnumHasInternalStringValue(
            enumClass = PizzaEnum.class
    )
    private String name;

    private Double cost;

}