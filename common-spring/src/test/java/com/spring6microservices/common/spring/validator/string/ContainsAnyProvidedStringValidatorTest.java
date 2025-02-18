package com.spring6microservices.common.spring.validator.string;

import com.spring6microservices.common.spring.dto.AddressDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainsAnyProvidedStringValidatorTest {

    private Validator validator;


    @BeforeEach
    public void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }


    @Test
    @DisplayName("isValid: when given string value is not in provided array then validation fails")
    public void whenGivenStringValueIsNotInArray_thenValidationFails() {
        AddressDto dto = new AddressDto(
                "Address 1 value",
                "Las Palmas",
                "Not valid state"
        );

        Set<ConstraintViolation<AddressDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<AddressDto> error = violations.iterator().next();
        assertEquals(
                "state",
                error.getPropertyPath().toString()
        );
        assertEquals(
                "must be one of the values included in [Canarias, Andalucia]",
                error.getMessage()
        );
    }


    @Test
    @DisplayName("isValid: when given string value is in provided array then validation succeeds")
    public void whenGivenStringValueIsInArray_thenValidationSucceeds() {
        AddressDto dto = new AddressDto(
                "Address 1 value",
                null,
                "Canarias"
        );

        Set<ConstraintViolation<AddressDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

}
