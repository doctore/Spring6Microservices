package com.security.custom.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashAlgorithmTest {

    static Stream<Arguments> getByAlgorithmTestCases() {
        return Stream.of(
                //@formatter:off
                //            algorithm,                              expectedResult
                Arguments.of( null,                                   empty() ),
                Arguments.of( "NotFoundKey",                          empty() ),
                Arguments.of( HashAlgorithm.SHA_384.getAlgorithm(),   of(HashAlgorithm.SHA_384) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByAlgorithmTestCases")
    @DisplayName("getByAlgorithm: test cases")
    public void getByAlgorithm_testCases(String algorithm,
                                         Optional<HashAlgorithm> expectedResult) {
        assertEquals(
                expectedResult,
                HashAlgorithm.getByAlgorithm(algorithm)
        );
    }


    @Test
    @DisplayName("getAvailableAlgorithms: test cases")
    public void getAvailableAlgorithms() {
        assertEquals(
                "SHA-256,SHA-384,SHA-512",
                HashAlgorithm.getAvailableAlgorithms().toString()
        );
    }

}
