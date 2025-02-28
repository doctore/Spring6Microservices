package com.spring6microservices.common.spring.validator.string;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

/**
 * Validates if the given {@link String} matches with one of the provided array of {@link String}s.
 */
public final class ContainsAnyProvidedStringValidator implements ConstraintValidator<ContainsAnyProvidedString, String> {

    private static final String ERROR_MESSAGE_PARAMETER = "values";

    private Set<String> validValues;
    private String constraintTemplate;
    private boolean isNullAccepted;


    @Override
    public void initialize(final ContainsAnyProvidedString constraintAnnotation) {
        validValues = Arrays.stream(
                        constraintAnnotation.anyOf()
                )
                .collect(
                        toSet()
                );
        constraintTemplate = constraintAnnotation.message();
        isNullAccepted = constraintAnnotation.isNullAccepted();
    }


    @Override
    public boolean isValid(final String value,
                           final ConstraintValidatorContext context) {
        boolean isValid =
                isNull(value)
                        ? isNullAccepted
                        : validValues.contains(value);
        if (!isValid) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext.addMessageParameter(
                            ERROR_MESSAGE_PARAMETER,
                            validValues
                    )
                    .buildConstraintViolationWithTemplate(constraintTemplate)
                    .addConstraintViolation();
        }
        return isValid;
    }

}
