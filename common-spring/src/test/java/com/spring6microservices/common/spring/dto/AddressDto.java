package com.spring6microservices.common.spring.dto;

import com.spring6microservices.common.spring.validator.string.ContainsAnyProvidedString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class AddressDto {

    private String address;

    @ContainsAnyProvidedString(
            anyOf = { "Las Palmas", "Malaga"},
            isNullAccepted = true
    )
    private String city;

    @ContainsAnyProvidedString(
            anyOf = { "Canarias", "Andalucia"}
    )
    private String state;

}
