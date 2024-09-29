package com.security.custom.enums.token;

import com.nimbusds.jose.JWSAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenSignatureAlgorithmTest {

    static Stream<Arguments> getByAlgorithmTestCases() {
        return Stream.of(
                //@formatter:off
                //            algorithm,            expectedResult
                Arguments.of( null,                 empty() ),
                Arguments.of( JWSAlgorithm.RS512,   of(TokenSignatureAlgorithm.RS512) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByAlgorithmTestCases")
    @DisplayName("getByAlgorithm: test cases")
    public void getByAlgorithm_testCases(JWSAlgorithm algorithm,
                                         Optional<TokenSignatureAlgorithm> expectedResult) {
        assertEquals(
                expectedResult,
                TokenSignatureAlgorithm.getByAlgorithm(algorithm)
        );
    }

}
