package com.security.custom.enums.token;

import com.nimbusds.jose.EncryptionMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenEncryptionMethodTest {

    static Stream<Arguments> getByMethodTestCases() {
        return Stream.of(
                //@formatter:off
                //            method,                           expectedResult
                Arguments.of( null,                             empty() ),
                Arguments.of( EncryptionMethod.A128CBC_HS256,   of(TokenEncryptionMethod.A128CBC_HS256) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByMethodTestCases")
    @DisplayName("getByMethod: test cases")
    public void getByMethod_testCases(EncryptionMethod method,
                                      Optional<TokenEncryptionMethod> expectedResult) {
        assertEquals(
                expectedResult,
                TokenEncryptionMethod.getByMethod(method)
        );
    }

}
