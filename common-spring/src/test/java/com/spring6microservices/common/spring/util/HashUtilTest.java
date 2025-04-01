package com.spring6microservices.common.spring.util;

import com.spring6microservices.common.spring.enums.HashAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashUtilTest {

    static Stream<Arguments> hashTestCases() {
        String sourceString = "123456";
        String expectedMD5 = "e10adc3949ba59abbe56e057f20f883e";
        String expectedSHA_256 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        String expectedSHA_384 = "0a989ebc4a77b56a6e2bb7b19d995d185ce44090c13e2984b7ecc6d446d4b61ea9991b76a4c2f04b1b4d244841449454";
        String expectedSHA_512 = "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413";
        String expectedSHA3_256 = "d7190eb194ff9494625514b6d178c87f99c5973e28c398969d2233f2960a573e";
        String expectedSHA3_384 = "1fb0da774034ba308fbe02f3e90dc004191df7aec3758b6be8451d09f1ff7ec18765f96e71faff637925c6be1d65f1cd";
        String expectedSHA3_512 = "64d09d9930c8ecf79e513167a588cb75439b762ce8f9b22ea59765f32aa74ca19d2f1e97dc922a3d4954594a05062917fb24d1f8e72f2ed02a58ed7534f94d27";

        return Stream.of(
                //@formatter:off
                //            sourceString,      hashAlgorithm,            expectedException,                expectedResult
                Arguments.of( null,              null,                     IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      HashAlgorithm.MD5,        null,                             expectedMD5 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA_256,    null,                             expectedSHA_256 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA_384,    null,                             expectedSHA_384 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA_512,    null,                             expectedSHA_512 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA3_256,   null,                             expectedSHA3_256 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA3_384,   null,                             expectedSHA3_384 ),
                Arguments.of( sourceString,      HashAlgorithm.SHA3_512,   null,                             expectedSHA3_512 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hashTestCases")
    @DisplayName("hash: test cases")
    public void hash_testCases(String sourceString,
                               HashAlgorithm hashAlgorithm,
                               Class<? extends Exception> expectedException,
                               String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> HashUtil.hash(sourceString, hashAlgorithm)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    HashUtil.hash(sourceString, hashAlgorithm)
            );
        }
    }


    static Stream<Arguments> verifyHashTestCases() {
        String sourceString = "123456";
        String expectedMD5 = "e10adc3949ba59abbe56e057f20f883e";
        String expectedSHA_256 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        String expectedSHA_384 = "0a989ebc4a77b56a6e2bb7b19d995d185ce44090c13e2984b7ecc6d446d4b61ea9991b76a4c2f04b1b4d244841449454";
        String expectedSHA_512 = "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413";
        String expectedSHA3_256 = "d7190eb194ff9494625514b6d178c87f99c5973e28c398969d2233f2960a573e";
        String expectedSHA3_384 = "1fb0da774034ba308fbe02f3e90dc004191df7aec3758b6be8451d09f1ff7ec18765f96e71faff637925c6be1d65f1cd";
        String expectedSHA3_512 = "64d09d9930c8ecf79e513167a588cb75439b762ce8f9b22ea59765f32aa74ca19d2f1e97dc922a3d4954594a05062917fb24d1f8e72f2ed02a58ed7534f94d27";

        return Stream.of(
                //@formatter:off
                //            sourceString,      hashedString,       hashAlgorithm,             expectedException,                expectedResult
                Arguments.of( null,              null,               null,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,              "ItDoesNotCare",    null,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,              "ItDoesNotCare",    HashAlgorithm.SHA_512,     IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,               null,                      IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   "ItDoesNotCare",    null,                      IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,               HashAlgorithm.SHA_512,     IllegalArgumentException.class,   null ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.MD5,         null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA_256,     null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA_384,     null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA_512,     null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA3_256,    null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA3_384,    null,                             false ),
                Arguments.of( sourceString,      "ItDoesNotCare",    HashAlgorithm.SHA3_512,    null,                             false ),
                Arguments.of( sourceString,      expectedMD5,        HashAlgorithm.MD5,         null,                             true ),
                Arguments.of( sourceString,      expectedSHA_256,    HashAlgorithm.SHA_256,     null,                             true ),
                Arguments.of( sourceString,      expectedSHA_384,    HashAlgorithm.SHA_384,     null,                             true ),
                Arguments.of( sourceString,      expectedSHA_512,    HashAlgorithm.SHA_512,     null,                             true ),
                Arguments.of( sourceString,      expectedSHA3_256,   HashAlgorithm.SHA3_256,    null,                             true ),
                Arguments.of( sourceString,      expectedSHA3_384,   HashAlgorithm.SHA3_384,    null,                             true ),
                Arguments.of( sourceString,      expectedSHA3_512,   HashAlgorithm.SHA3_512,    null,                             true )
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
