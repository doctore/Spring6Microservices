package com.security.custom.enums.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenKeyTest {

    static Stream<Arguments> getByKeyTestCases() {
        return Stream.of(
                //@formatter:off
                //            key,                          expectedResult
                Arguments.of( null,                         empty() ),
                Arguments.of( "NotFoundKey",                empty() ),
                Arguments.of( TokenKey.USERNAME.getKey(),   of(TokenKey.USERNAME) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByKeyTestCases")
    @DisplayName("getByKey: test cases")
    public void getByKey_testCases(String key,
                                   Optional<TokenKey> expectedResult) {
        assertEquals(
                expectedResult,
                TokenKey.getByKey(key)
        );
    }

}
