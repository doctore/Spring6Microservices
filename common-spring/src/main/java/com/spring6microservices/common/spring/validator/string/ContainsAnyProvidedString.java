package com.spring6microservices.common.spring.validator.string;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be included in the provided array of {@link String}s.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = ContainsAnyProvidedStringValidator.class)
public @interface ContainsAnyProvidedString {

    String message() default "must be one of the values included in {values}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return the array of accepted {@link String}s used to check the value
     */
    String[] anyOf() default {};

    /**
     * @return {@code true} if {@code null} is accepted as a valid value, {@code false} otherwise.
     */
    boolean isNullAccepted() default false;

}
