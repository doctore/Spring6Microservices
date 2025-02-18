package com.spring6microservices.common.spring.validator.enums;

import com.spring6microservices.common.spring.dto.PizzaDto;
import com.spring6microservices.common.spring.enums.PizzaEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.spring6microservices.common.spring.enums.PizzaEnum.CARBONARA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumHasInternalStringValueValidatorTest {

    private Validator validator;


    @BeforeEach
    public void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }


    @Test
    @DisplayName("isValid: when given string value is not in enum then validation fails")
    public void whenGivenStringValueIsNotInEnum_thenValidationFails() {
        PizzaDto dto = new PizzaDto(
                CARBONARA.getInternalPropertyValue() + PizzaEnum.MARGUERITA.getInternalPropertyValue(),
                5D
        );

        Set<ConstraintViolation<PizzaDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<PizzaDto> error = violations.iterator().next();
        assertEquals(
                "name",
                error.getPropertyPath().toString()
        );
        assertEquals(
                "must be one of the values included in [Margherita, Carbonara]",
                error.getMessage()
        );
    }


    @Test
    @DisplayName("isValid: when given string value is in enum then validation succeeds")
    public void whenGivenStringValueIsInEnum_thenValidationSucceeds() {
        PizzaDto dto = new PizzaDto(
                CARBONARA.getInternalPropertyValue(),
                5D
        );

        Set<ConstraintViolation<PizzaDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

}
