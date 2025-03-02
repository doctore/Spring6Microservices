package com.security.custom.model.validator;

import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenType;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.core.functional.validation.Validation;
import com.spring6microservices.common.core.functional.validation.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWS;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationClientDetailsValidatorTest {

    private ApplicationClientDetailsValidator validator;


    @BeforeEach
    public void init() {
        validator = new ApplicationClientDetailsValidator();
    }


    @Test
    @DisplayName("validate: when applicationClientDetails is null then Invalid is returned")
    public void validate_whenApplicationClientDetailsIsNull_thenInvalidIsReturned() {
        Validation<ValidationError, ApplicationClientDetails> result = validator.validate(null);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(
                1,
                result.getErrors().size()
        );
        List<ValidationError> errors = new ArrayList<>(result.getErrors());
        assertEquals(
                "applicationClientDetails must be not null",
                errors.getFirst().getErrorMessage()
        );
    }


    @Test
    @DisplayName("validate: when tokenType is null then Invalid is returned")
    public void validate_whenTokenTypeIsNull_thenInvalidIsReturned() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWS("JWS");
        applicationClientDetails.setTokenType(null);

        Validation<ValidationError, ApplicationClientDetails> result = validator.validate(applicationClientDetails);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(
                1,
                result.getErrors().size()
        );
        List<ValidationError> errors = new ArrayList<>(result.getErrors());
        assertEquals(
                "tokenType must be not null",
                errors.getFirst().getErrorMessage()
        );
    }


    @Test
    @DisplayName("validate: when tokenType requires encryption configuration but related data are null then Invalid is returned")
    public void validate_whenTokenTypeRequiresEncryptionConfigurationButRelatedDataAreNull_thenInvalidIsReturned() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("JWE");
        applicationClientDetails.setEncryptionAlgorithm(null);
        applicationClientDetails.setEncryptionMethod(null);
        applicationClientDetails.setEncryptionSecret(null);

        Validation<ValidationError, ApplicationClientDetails> result = validator.validate(applicationClientDetails);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(
                3,
                result.getErrors().size()
        );
        List<ValidationError> errors = new ArrayList<>(result.getErrors());
        assertEquals(
                "encryptionAlgorithm must be not null",
                errors.getFirst().getErrorMessage()
        );
        assertEquals(
                "encryptionMethod must be not null",
                errors.get(1).getErrorMessage()
        );
        assertEquals(
                "encryptionSecret must be not null or empty",
                errors.getLast().getErrorMessage()
        );
    }


    @Test
    @DisplayName("validate: when tokenType does not require encryption configuration but related data are not null then Invalid is returned")
    public void validate_whenTokenTypeDoesNotRequireEncryptionConfigurationButRelatedDataAreNotNull_thenInvalidIsReturned() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWS("JWS");
        applicationClientDetails.setEncryptionAlgorithm(TokenEncryptionAlgorithm.DIR);
        applicationClientDetails.setEncryptionMethod(TokenEncryptionMethod.A128CBC_HS256);
        applicationClientDetails.setEncryptionSecret("dirEncryptionSecret##9991a2(jwe)");

        Validation<ValidationError, ApplicationClientDetails> result = validator.validate(applicationClientDetails);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(
                3,
                result.getErrors().size()
        );
        List<ValidationError> errors = new ArrayList<>(result.getErrors());
        assertEquals(
                "encryptionAlgorithm must be null",
                errors.getFirst().getErrorMessage()
        );
        assertEquals(
                "encryptionMethod must be null",
                errors.get(1).getErrorMessage()
        );
        assertEquals(
                "encryptionSecret must be null",
                errors.getLast().getErrorMessage()
        );
    }


    static Stream<Arguments> validateValidTestCases() {
        ApplicationClientDetails validJWS = buildApplicationClientDetailsJWS("JWS");
        ApplicationClientDetails validJWE = buildApplicationClientDetailsJWE("JWE");

        ApplicationClientDetails validEncryptedJWS = buildApplicationClientDetailsJWS("JWS");
        validEncryptedJWS.setTokenType(TokenType.ENCRYPTED_JWS);

        ApplicationClientDetails validEncryptedJWE = buildApplicationClientDetailsJWE("JWE");
        validEncryptedJWE.setTokenType(TokenType.ENCRYPTED_JWE);
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails
                Arguments.of( validJWS ),
                Arguments.of( validJWE ),
                Arguments.of( validEncryptedJWS ),
                Arguments.of( validEncryptedJWE )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("validateValidTestCases")
    @DisplayName("validate: valid test cases")
    public void validateValid_testCases(ApplicationClientDetails applicationClientDetails) {
        Validation<ValidationError, ApplicationClientDetails> result = validator.validate(applicationClientDetails);

        assertNotNull(result);
        assertTrue(result.isValid());
    }

}
