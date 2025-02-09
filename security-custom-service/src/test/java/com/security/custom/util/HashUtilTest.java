package com.security.custom.util;

import com.security.custom.enums.HashAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashUtilTest {

    static Stream<Arguments> verifyHashTestCases() {
        String sourceString = "123456";
        String expectedSHA256 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        String expectedSHA384 = "0a989ebc4a77b56a6e2bb7b19d995d185ce44090c13e2984b7ecc6d446d4b61ea9991b76a4c2f04b1b4d244841449454";
        String expectedSHA512 = "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413";

        return Stream.of(
                //@formatter:off
                //            sourceString,      hashedString,      hashAlgorithm,           expectedException,                expectedResult
                Arguments.of( null,              null,              null,                    IllegalArgumentException.class,   null ),
                Arguments.of( null,              "ItDoesNotCare",   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( null,              "ItDoesNotCare",   HashAlgorithm.SHA_512,   IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,              null,                    IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   "ItDoesNotCare",   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,              HashAlgorithm.SHA_512,   IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      "ItDoesNotCare",   HashAlgorithm.SHA_256,   null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",   HashAlgorithm.SHA_384,   null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",   HashAlgorithm.SHA_512,   null,                             false ),
                Arguments.of( sourceString,      expectedSHA256,    HashAlgorithm.SHA_256,   null,                             true ),
                Arguments.of( sourceString,      expectedSHA384,    HashAlgorithm.SHA_384,   null,                             true ),
                Arguments.of( sourceString,      expectedSHA512,    HashAlgorithm.SHA_512,   null,                             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("verifyHashTestCases")
    @DisplayName("verifyHash: test cases")
    public void verifyHash_testCases(String sourceString,
                                     String hashedString,
                                     HashAlgorithm hashAlgorithm,
                                     Class<? extends Exception> expectedException,
                                     Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> HashUtil.verifyHash(sourceString, hashedString, hashAlgorithm)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    HashUtil.verifyHash(sourceString, hashedString, hashAlgorithm)
            );
        }
    }

}
