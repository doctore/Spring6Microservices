package com.security.custom.model.validator;

import com.security.custom.enums.token.TokenType;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.core.functional.validation.Validate;
import com.spring6microservices.common.core.functional.validation.Validation;
import com.spring6microservices.common.core.functional.validation.ValidationError;
import com.spring6microservices.common.core.util.StringUtil;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *    Validates the stored information in database related with {@link ApplicationClientDetails} that cannot be managed
 * using validation annotations like {@link NotNull} and similar.
 */
public final class ApplicationClientDetailsValidator implements Validate<ApplicationClientDetails> {


    @Override
    public Validation<ValidationError, ApplicationClientDetails> validate(final ApplicationClientDetails applicationClientDetails) {
        return Validation.combine(
                validateTokenType(applicationClientDetails)
        );
    }


    /**
     * Verifies the {@link ApplicationClientDetails#getTokenType()} of the provided {@code applicationClientDetails}.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} to check
     *
     * @return {@link Validation} with the result after checking {@link ApplicationClientDetails#getTokenType()}
     */
    private Validation<ValidationError, ApplicationClientDetails> validateTokenType(final ApplicationClientDetails applicationClientDetails) {
        if (null == applicationClientDetails) {
            return Validation.invalid(
                    List.of(
                            ValidationError.of(
                                    1,
                                    "applicationClientDetails must be not null"
                            )
                    )
            );
        }
        if (null == applicationClientDetails.getTokenType()) {
            return Validation.invalid(
                    List.of(
                            ValidationError.of(
                                    1,
                                    "tokenType must be not null"
                            )
                    )
            );
        }
        List<ValidationError> errors = verifyTokenType(
                applicationClientDetails
        );
        return errors.isEmpty()
                ? Validation.valid(applicationClientDetails)
                : Validation.invalid(errors);
    }


    /**
     *    Verifies the {@link ApplicationClientDetails#getTokenType()} of the given {@code applicationClientDetails},
     * returning the errors found.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} to check
     *
     * @return {@link List} of {@link ValidationError}
     */
    private List<ValidationError> verifyTokenType(final ApplicationClientDetails applicationClientDetails) {
        List<String> errorMessages = new ArrayList<>();
        if (TokenType.getRequiredEncryptionAlgorithm().contains(applicationClientDetails.getTokenType())) {
            if (null == applicationClientDetails.getEncryptionAlgorithm()) {
                errorMessages.add("encryptionAlgorithm must be not null");
            }
            if (null == applicationClientDetails.getEncryptionMethod()) {
                errorMessages.add("encryptionMethod must be not null");
            }
            if (StringUtil.isBlank(applicationClientDetails.getEncryptionSecret())) {
                errorMessages.add("encryptionSecret must be not null or empty");
            }
        } else {
            if (null != applicationClientDetails.getEncryptionAlgorithm()) {
                errorMessages.add("encryptionAlgorithm must be null");
            }
            if (null != applicationClientDetails.getEncryptionMethod()) {
                errorMessages.add("encryptionMethod must be null");
            }
            if (null != applicationClientDetails.getEncryptionSecret()) {
                errorMessages.add("encryptionSecret must be null");
            }
        }
        return errorMessages.stream()
                .map(e ->
                        ValidationError.of(2, e)
                )
                .toList();
    }

}