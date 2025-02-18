package com.spring6microservices.common.spring.validator.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 *    Validates if the given {@link String} matches with one of the internal {@link String} property belonging to the
 * provided {@link Class} of {@link Enum}.
 */
public class EnumHasInternalStringValueValidator implements ConstraintValidator<EnumHasInternalStringValue, String> {

	private static final String ERROR_MESSAGE_PARAMETER = "values";

	private List<String> enumValidValues;
	private String constraintTemplate;
	private boolean isNullAccepted;


	@Override
	@SuppressWarnings("unchecked")
	public void initialize(final EnumHasInternalStringValue constraintAnnotation) {
		enumValidValues = Arrays.stream(
						constraintAnnotation.enumClass().getEnumConstants()
				)
				.map(e ->
						((IEnumInternalPropertyValue<String>)e).getInternalPropertyValue()
				)
				.collect(
						toList()
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
						: enumValidValues.contains(value);
		if (!isValid) {
			HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
			hibernateContext.disableDefaultConstraintViolation();
			hibernateContext.addMessageParameter(
					        ERROR_MESSAGE_PARAMETER,
							enumValidValues
					)
					.buildConstraintViolationWithTemplate(constraintTemplate)
					.addConstraintViolation();
		}
		return isValid;
	}

}
