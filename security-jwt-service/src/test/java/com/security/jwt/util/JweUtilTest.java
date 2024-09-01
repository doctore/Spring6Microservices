package com.security.jwt.util;

import com.security.jwt.enums.token.TokenEncryptionAlgorithm;
import com.security.jwt.enums.token.TokenEncryptionMethod;
import com.security.jwt.enums.token.TokenSignatureAlgorithm;
import com.security.jwt.exception.token.TokenException;
import com.security.jwt.exception.token.TokenExpiredException;
import com.security.jwt.exception.token.TokenInvalidException;
import com.spring6microservices.common.core.functional.either.Either;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JweUtilTest {

    /*
    // TODO: PENDING TO REMOVE after adding more options to sign the token
    public static void main(String[] args) {
        Map<String, Object> informationToInclude = new LinkedHashMap<>();
        informationToInclude.put("username", "username value");
        informationToInclude.put("roles", List.of("admin", "user"));
        informationToInclude.put("name", "name value");
        informationToInclude.put("age", 23);

        Map<String, Object> informationToIncludeEmpty = new LinkedHashMap<>();
        informationToIncludeEmpty.put("jti", "8cbccfe1-8d89-4f4f-84f1-20f90a268986");

        System.out.println(
                "Expired and not empty: " +
                        JweUtil.generateToken(
                                informationToInclude,
                                RSA_OAEP_512_ENCRYPTION_ALGORITHM,
                                A128CBC_HS256_ENCRYPTION_METHOD,
                                RS_ENCRYPTION_PUBLIC_KEY,
                                HS256_SIGNATURE_ALGORITHM,
                                HS256_SIGNATURE_SECRET,
                                -1000000000
                        )
        );
        System.out.println("-----------------------------");
        System.out.println(
                "Not expired and empty [A128CBC_HS256]: " +
                        JweUtil.generateToken(
                                informationToIncludeEmpty,
                                RSA_OAEP_512_ENCRYPTION_ALGORITHM,
                                A128CBC_HS256_ENCRYPTION_METHOD,
                                RS_ENCRYPTION_PUBLIC_KEY,
                                HS256_SIGNATURE_ALGORITHM,
                                HS256_SIGNATURE_SECRET,
                                1
                        )
        );
        System.out.println("-----------------------------");
        System.out.println(
                "Not expired and not empty [A128CBC_HS256]: " +
                        JweUtil.generateToken(
                                informationToInclude,
                                RSA_OAEP_512_ENCRYPTION_ALGORITHM,
                                A128CBC_HS256_ENCRYPTION_METHOD,
                                RS_ENCRYPTION_PUBLIC_KEY,
                                HS256_SIGNATURE_ALGORITHM,
                                HS256_SIGNATURE_SECRET,
                                1
                        )
        );
        System.out.println("-----------------------------");
        System.out.println(
                "Not expired and not empty [A192CBC_HS384]: " +
                        JweUtil.generateToken(
                                informationToInclude,
                                RSA_OAEP_512_ENCRYPTION_ALGORITHM,
                                A192CBC_HS384_ENCRYPTION_METHOD,
                                RS_ENCRYPTION_PUBLIC_KEY,
                                HS256_SIGNATURE_ALGORITHM,
                                HS256_SIGNATURE_SECRET,
                                1
                        )
        );
        System.out.println("-----------------------------");
        System.out.println(
                "Not expired and not empty [A256CBC_HS512]: " +
                        JweUtil.generateToken(
                                informationToInclude,
                                RSA_OAEP_512_ENCRYPTION_ALGORITHM,
                                A256CBC_HS512_ENCRYPTION_METHOD,
                                RS_ENCRYPTION_PUBLIC_KEY,
                                HS256_SIGNATURE_ALGORITHM,
                                HS256_SIGNATURE_SECRET,
                                1
                        )
        );
    }
    */


    static Stream<Arguments> generateTokenEncryptTestCases() {
        String doesNotCareSecret = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            jwsToken,          encryptionAlgorithm,                 encryptionMethod,                  encryptionSecret,            expectedException
                Arguments.of( null,              null,                                null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,            null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          IllegalArgumentException.class ),
                Arguments.of( "",                null,                                null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,            null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          IllegalArgumentException.class ),
                // encryptionMethod and encryptionSecret does not match
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( NOT_JWS_TOKEN,     DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                // Not valid JWS
                Arguments.of( NOT_JWS_TOKEN,     DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    TokenInvalidException.class ),
                // DIR valid generated tokens
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   null ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,            A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   null ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,            A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   null ),
                // RSA valid generated tokens
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenEncryptTestCases")
    @DisplayName("generateToken: encrypt provided token test cases")
    public void generateTokenEncrypt_testCases(String jwsToken,
                                               TokenEncryptionAlgorithm encryptionAlgorithm,
                                               TokenEncryptionMethod encryptionMethod,
                                               String encryptionSecret,
                                               Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JweUtil.generateToken(jwsToken, encryptionAlgorithm, encryptionMethod, encryptionSecret)
            );
        }
        else {
            assertNotNull(
                    JweUtil.generateToken(jwsToken, encryptionAlgorithm, encryptionMethod, encryptionSecret)
            );
        }
    }


    static Stream<Arguments> generateTokenSignAndEncryptTestCases() {
        Map<String, Object> informationToInclude = new HashMap<>();
        String doesNotCareSecret = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   encryptionAlgorithm,                 encryptionMethod,                  encryptionSecret,            signatureAlgorithm,          signatureSecret,          expirationTimeInSeconds,   expectedException
                Arguments.of( null,                   null,                                null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   "",                       90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   null,                                null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   "",                       90,                        IllegalArgumentException.class ),
                // encryptionMethod and encryptionSecret does not match
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                // signatureAlgorithm and signatureSecret does not match
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,        90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,        90,                        TokenException.class ),
                // DIR valid generated tokens
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,            A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,            A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                // RSA valid generated tokens
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_PUBLIC_KEY,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenSignAndEncryptTestCases")
    @DisplayName("generateToken: sign and encrypt provided data test cases")
    public void generateTokenSignAndEncrypt_testCases(Map<String, Object> informationToInclude,
                                                      TokenEncryptionAlgorithm encryptionAlgorithm,
                                                      TokenEncryptionMethod encryptionMethod,
                                                      String encryptionSecret,
                                                      TokenSignatureAlgorithm signatureAlgorithm,
                                                      String signatureSecret,
                                                      long expirationTimeInSeconds,
                                                      Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JweUtil.generateToken(informationToInclude, encryptionAlgorithm, encryptionMethod, encryptionSecret, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
            );
        }
        else {
            assertNotNull(
                    JweUtil.generateToken(informationToInclude, encryptionAlgorithm, encryptionMethod, encryptionSecret, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
            );
        }
    }


    static Stream<Arguments> getAllClaimsFromTokenTestCases() {
        String doesNotCareValue = "ItDoesNotCare";
        String notValidtoken = "NotValidToken";

        Map<String, Object> expectedResultEmptyToken = new LinkedHashMap<>() {{
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("jti", "8cbccfe1-8d89-4f4f-84f1-20f90a268986");
        }};
        Map<String, Object> expectedResultNotEmptyToken = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("roles", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,                                                  encryptionSecret,            signatureSecret,                expectedException,                expectedResult
                Arguments.of( null,                                                      null,                        null,                           IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAllClaimsFromTokenTestCases")
    @DisplayName("getAllClaimsFromToken: test cases")
    public void getAllClaimsFromToken_testCases(String jwsToken,
                                                String encryptionSecret,
                                                String signatureSecret,
                                                Class<? extends Exception> expectedException,
                                                Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JweUtil.getAllClaimsFromToken(jwsToken, encryptionSecret, signatureSecret)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JweUtil.getAllClaimsFromToken(jwsToken, encryptionSecret, signatureSecret)
            );
        }
    }


    static Stream<Arguments> getSafeAllClaimsFromTokenTestCases() {
        String doesNotCareValue = "ItDoesNotCare";
        String notValidtoken = "NotValidToken";

        Either<Exception, Map<String, Object>> expectedResultEmptyToken = Either.left(
                new IllegalArgumentException(
                        "jweToken cannot be null or empty"
                )
        );
        Either<Exception, Map<String, Object>> expectedResultEmptyEncryptionSecret = Either.left(
                new IllegalArgumentException(
                        "encryptionSecret cannot be null or empty"
                )
        );
        Either<Exception, Map<String, Object>> expectedResultEmptySignatureSecret = Either.left(
                new IllegalArgumentException(
                        "signatureSecret cannot be null or empty"
                )
        );
        Either<Exception, Map<String, Object>> expectedResultInvalidToken = Either.left(
                new TokenInvalidException()
        );
        Either<Exception, Map<String, Object>> expectedResultExpiredToken = Either.left(
                new TokenExpiredException()
        );
        Either<Exception, Map<String, Object>> expectedResultTokenException = Either.left(
                new TokenException()
        );
        Either<Exception, Map<String, Object>> expectedResultValidEmptyToken = Either.right(
                new LinkedHashMap<>() {{
                    put("exp", new Date(5000000000L * 1000));
                    put("iat", new Date(1700000000L * 1000));
                    put("jti", "8cbccfe1-8d89-4f4f-84f1-20f90a268986");
                }}
        );
        Either<Exception, Map<String, Object>> expectedResultValidNotEmptyToken = Either.right(
                new LinkedHashMap<>() {{
                    put("name", "name value");
                    put("exp", new Date(5000000000L * 1000));
                    put("iat", new Date(1700000000L * 1000));
                    put("age", 23L);
                    put("roles", List.of("admin", "user"));
                    put("username", "username value");
                }}
        );
        return Stream.of(
                //@formatter:off
                //            jweToken,                                                  encryptionSecret,            signatureSecret,                expectedResult
                Arguments.of( null,                                                      null,                        null,                           expectedResultEmptyToken ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( "",                                                        null,                        null,                           expectedResultEmptyToken ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           expectedResultEmptySignatureSecret ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             expectedResultEmptySignatureSecret ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultInvalidToken ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               expectedResultTokenException ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getSafeAllClaimsFromTokenTestCases")
    @DisplayName("getSafeAllClaimsFromToken: test cases")
    public void getSafeAllClaimsFromToken_testCases(String jwsToken,
                                                    String encryptionSecret,
                                                    String signatureSecret,
                                                    Either<Exception, Map<String, Object>> expectedResult) {
        if (expectedResult.isRight()) {
            assertEquals(
                    expectedResult,
                    JweUtil.getSafeAllClaimsFromToken(jwsToken, encryptionSecret, signatureSecret)
            );
        }
        else {
            Either<Exception, Map<String, Object>> result = JweUtil.getSafeAllClaimsFromToken(jwsToken, encryptionSecret, signatureSecret);
            assertFalse(result.isRight());
            assertEquals(
                    expectedResult.getLeft().getClass(),
                    result.getLeft().getClass()
            );
            if (!(result.getLeft() instanceof TokenException)) {
                assertEquals(
                        expectedResult.getLeft().getMessage(),
                        result.getLeft().getMessage()
                );
            }
        }
    }


    static Stream<Arguments> getPayloadKeysTestCases() {
        String doesNotCareValue = "ItDoesNotCare";
        String notValidtoken = "NotValidToken";

        Set<String> keysToInclude = new HashSet<>(asList("username", "roles", "age"));
        Map<String, Object> expectedResult = new LinkedHashMap<>() {{
            put("username", "username value");
            put("roles", List.of("admin", "user"));
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,                                                  encryptionSecret,            signatureSecret,                keysToInclude,     expectedException,                expectedResult
                Arguments.of( null,                                                      null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             keysToInclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadKeysTestCases")
    @DisplayName("getPayloadKeys: test cases")
    public void getPayloadKeys_testCases(String jwsToken,
                                         String encryptionSecret,
                                         String signatureSecret,
                                         Set<String> keysToInclude,
                                         Class<? extends Exception> expectedException,
                                         Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JweUtil.getPayloadKeys(jwsToken, encryptionSecret, signatureSecret, keysToInclude)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JweUtil.getPayloadKeys(jwsToken, encryptionSecret, signatureSecret, keysToInclude)
            );
        }
    }


    static Stream<Arguments> getPayloadExceptKeysTestCases() {
        String doesNotCareValue = "ItDoesNotCare";
        String notValidtoken = "NotValidToken";

        Set<String> keysToExclude = new HashSet<>(asList("username", "roles", "age"));
        Map<String, Object> expectedResultEmptyToken = new LinkedHashMap<>() {{
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("jti", "8cbccfe1-8d89-4f4f-84f1-20f90a268986");
        }};
        Map<String, Object> expectedResultNotEmptyTokenWithKeysToExclude = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
        }};
        Map<String, Object> expectedResultNotEmptyTokenWithoutKeysToExclude = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("roles", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,                                                  encryptionSecret,            signatureSecret,                keysToExclude,     expectedException,                expectedResult
                Arguments.of( null,                                                      null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                      doesNotCareValue,            doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                        doesNotCareValue,            doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          null,                        "",                             keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          "",                          doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                          doesNotCareValue,            "",                             keysToExclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                                             doesNotCareValue,            doesNotCareValue,               keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             doesNotCareValue,            doesNotCareValue,               keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         RS_ENCRYPTION_PRIVATE_KEY,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadExceptKeysTestCases")
    @DisplayName("getPayloadExceptKeys: test cases")
    public void getPayloadExceptKeys_testCases(String jwsToken,
                                               String encryptionSecret,
                                               String signatureSecret,
                                               Set<String> keysToExclude,
                                               Class<? extends Exception> expectedException,
                                               Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JweUtil.getPayloadExceptKeys(jwsToken, encryptionSecret, signatureSecret, keysToExclude)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JweUtil.getPayloadExceptKeys(jwsToken, encryptionSecret, signatureSecret, keysToExclude)
            );
        }
    }


    static Stream<Arguments> isJweTokenTestCases() {
        return Stream.of(
                //@formatter:off
                //            jwsToken,                                                  expectedResult
                Arguments.of( null,                                                      false ),
                Arguments.of( "",                                                        false ),
                Arguments.of( "NotValidToken",                                           false ),
                Arguments.of( NOT_JWE_TOKEN,                                             false ),
                // Expired tokens
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                      true ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                            true ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                            true ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                            true ),
                // Not expired empty tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,            true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,   true ),
                // Not expired and not empty tokens
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                  true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                  true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                  true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isJweTokenTestCases")
    @DisplayName("isJweToken: test cases")
    public void isJweToken_testCases(String jweToken,
                                     boolean expectedResult) {
        assertEquals(
                expectedResult,
                JweUtil.isJweToken(jweToken)
        );
    }


    private static final TokenEncryptionAlgorithm DIR_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.DIR;

    private static final TokenEncryptionAlgorithm RSA_OAEP_256_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_256;

    private static final TokenEncryptionAlgorithm RSA_OAEP_384_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_384;

    private static final TokenEncryptionAlgorithm RSA_OAEP_512_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_512;

    private static final TokenEncryptionMethod A128CBC_HS256_ENCRYPTION_METHOD = TokenEncryptionMethod.A128CBC_HS256;

    private static final TokenEncryptionMethod A192CBC_HS384_ENCRYPTION_METHOD = TokenEncryptionMethod.A192CBC_HS384;

    private static final TokenEncryptionMethod A256CBC_HS512_ENCRYPTION_METHOD = TokenEncryptionMethod.A256CBC_HS512;

    private static final TokenSignatureAlgorithm HS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS256;

    private static final String HS256_SIGNATURE_SECRET = "hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k";

    private static final String HS256_WRONG_SIGNATURE_SECRET = "111111111111111111111111111111111111111111111111111111111111111";

    private static final String DIR_ENCRYPTION_SECRET_256 = "dirEncryptionSecret##9991a2(jwe)";

    private static final String DIR_ENCRYPTION_SECRET_384 = "dirEncryptionSecret##9991a2(jwe)$53232Rt_G3rew90";

    private static final String DIR_ENCRYPTION_SECRET_512 = "dirEncryptionSecret##9991a2(jwe)$53232Rt_G3rew9016310k_21iusN271";

    private static final String RS_ENCRYPTION_PRIVATE_KEY =
            """
                    -----BEGIN PRIVATE KEY-----
                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXHK07oaqx8fnY
                    r3UbfUS6HRXQFRvQ0J8qqzgq+UH4ZqtgxV44ciSOwzL65E2aZrixXxB+s7Kbbw1q
                    R0oUNvay8QhMlmwUZwXjCZbeNbQI8LXoXSU1l9xx2GZ7BS3/huFGHSyGzjrSYJdJ
                    cZKOYij26aCPIx7VYEeIUmPAbkCA1VVUhaOic81aQAdhqrKjqpBcYTwYW4YF2zcy
                    Dx8YLrRLJbjFzM94eg9oErqIsptyZ83daoNytVTbijzDoXAmkHrx58NfZnuJ0JfH
                    UKGZiMlt6fBAqDR3+Dls6hemJA+VxCO2dKBDp2vSGfIDc1mr1kQozFK3JqFINcWI
                    537vnPWVAgMBAAECggEAA/VAagMFx3k/p/05MMdi8l9afSkJtw+Of7hc4APyhlOw
                    HPiGdi2H3MUVnoHg23thzo7feHtzS+7Id+vBRQ7HKZrhHVpvnx2EsgnurZ1+p0ug
                    xCLpG4KBsmoD4yiDUtcBAGG5aG2El709G94cQ9uj2DXN2rnwL+VrR5GQOHqFeNUI
                    rTKUG4lwCPcvPOvnpdYj2jv4oj4uO2cbmgbZThcl4KdHK/Eo/jHr0UOhiT5J9ocm
                    RKryYYjEXE/t57tR2e0Rsel74fTmcgNygiixMjKDC1cmqX4R+g67m1gfR+/+SXR8
                    S9f9VzcwugcTnxIhke3TRta53QgfPNLOidpMM1tLwQKBgQC9faOxEVJ2KTQaAAMw
                    Nx8bBxhev5mifi+f8d14ERkG7XFb4SzPeUY29oB0KVxDyBwR8rgNars+GpUnquZv
                    91PVs5fYD3W+HwtOD/UOL0z3UtKnNI8nvtK08ru0PFDVzwzqEapy8dLkmbG556GP
                    HZ5WVn+8QeTX7GqbSU3xtPp21QKBgQDMJpTMzneQ+GrupU1lzdlD8GKF2RbsZ0Ui
                    rtIx4UYgIQV0lbvPhneJrGy16woOBUZ7jkCEDXKqofGumwCVfhpjjYzIqPfZzXaa
                    t5a6l2cLuwt0JnjluwqmIfWf1z+GdqCxgqUwdUgzxcPmzxcHwOCX1YFQQ8WONd6s
                    Id9DfAFjwQKBgQCLsKhQq11oAD4JgMLY83m52gQsLQEcWfvP5GSI08noYnhz7E61
                    cEjD0fqmJ6t9yHJxBMaMFYeNY9lbEdCo7+JcovWocNUy3/3cgUT9PP93QBZM7yEt
                    gq4geOTJHMHWrLlvgLBv5je7EFaFnu1p7MLCESg/ZzBFwWJhsauFKQ6PNQKBgFDc
                    PzfX15f+LSyVINDf9dxpDD0DvYapaMLSB8Nl/Qagza5d2GPcWOCZAP4VOIhRIpex
                    wnALe42GU1nbXyHXLtCbslWQR4tnTED/0p3ZdiE5VtIMovorWY5wCP/km+7Acemd
                    W5yT96M6A9wZzn9tsAezs2J9VXR8ddQsHmh2Z36BAoGBAIkFBge0QbWZGYCr3uk9
                    K0AhZUekGSzhakqp60XQs5kw8zb+TllCRxtYsQlyaHp1M8AH3Di/Uw+EhBt6h4Uw
                    fAPCZRg8vdG8Hp26PwXxybZ/M9u7NaKJ0BT4AwKKtZTUxZVxz/kPhdHT+MpoQqJf
                    JuzuwXVAAcl1GME2OiqkZhww
                    -----END PRIVATE KEY-----""";

    private static final String RS_ENCRYPTION_PUBLIC_KEY =
            """
                    -----BEGIN PUBLIC KEY-----
                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlxytO6GqsfH52K91G31E
                    uh0V0BUb0NCfKqs4KvlB+GarYMVeOHIkjsMy+uRNmma4sV8QfrOym28NakdKFDb2
                    svEITJZsFGcF4wmW3jW0CPC16F0lNZfccdhmewUt/4bhRh0shs460mCXSXGSjmIo
                    9umgjyMe1WBHiFJjwG5AgNVVVIWjonPNWkAHYaqyo6qQXGE8GFuGBds3Mg8fGC60
                    SyW4xczPeHoPaBK6iLKbcmfN3WqDcrVU24o8w6FwJpB68efDX2Z7idCXx1ChmYjJ
                    benwQKg0d/g5bOoXpiQPlcQjtnSgQ6dr0hnyA3NZq9ZEKMxStyahSDXFiOd+75z1
                    lQIDAQAB
                    -----END PUBLIC KEY-----""";

    private static final String NOT_JWS_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
            + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
            + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";

    private static final String VALID_JWS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY1OTU3LCJpYXQiOjE3MjQ2NjU5NTcsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.IPhRr7D68LHqWOkK763CLmtdvpDSV_b93GA5aWNHMqI";

    private static final String NOT_JWE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

    private static final String EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..q6ShzxgjvhRmqApkYZgU2w"
            + ".RDP2BYn9PD-qoxBoeCv24C2WKPyreNA_WSjFY-zHH3b6_oB-NiUSJtr1YJ1lPXJhIqB7svWKTe28KxIGEWbjud9P0NPhzN3j-rnQkJztHETHd_RHnn3PCHb0oHdisCocx7lDg5d_kTHrlgT"
            + "tecvYXkXd3HGtjChqK7HyEYKUgk_LD6Jdyh7K9hMLbD6gmE9JYPue1FLW6oHC8QycPblbb9G5Dl9Z9oUJlLC_i8UAgSAkll1Cox5qCaLHGkuH5oODf8ZI3NGYuuId7ZFqdxgWwhebd2wpo9c"
            + "K9pkHoY2otoo.rDV2sPBllhY3PZtlq44b5Q";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..oVii7DvRHR_2"
            + "X9pB_UxLpg.iBAplXL0bbyXQAsMkIsT98e9_eiqQ4W-p1p6qKaNLeUAyX6FFVFpBfNldC7tN4FQ_COwpfY84SF_WmT9KW8MelPQlx_uqM1v_5JSVM5UPU9fdxtUha4_ICmbAC4sZM4ZYCiEb"
            + "2y3wVxICIVJveqfe8YmVB72PWU1qiLcy0tz2HxUG5wsc_-nJig0wO6_2Grv2J9XhhfZPwF39xaeqE7aFbOX7RmQU_9LsT40xAq0L4s.uBZpSCFovRYA4csfEKjs-Q";

    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..f5--WfcLKFYYxkmYjc"
            + "7SBw.lRtxRB5HysExK4G_DPhNOo8i4p0Fk0RfuaqIRpJc9uwW4ukLyGfjJMVGEZxEfoYqCCd0-_2pOgJlkndwlpguzT78OguBzm2SLc5A8IzavsNzIjL6Aua_snqIlaFC5SsfzB63Z66soKN"
            + "r4Duw5bCsQRmeA2To_Quu1Qtr_l3e96dYow50HzQwxO9Bn96pFIx59g23KFbxCAsoTd5jJUqZIO1WhpYs_Dk6kvMVzjZEr0ELu5AbNmMs2EPaYzXPIuc8EG-r1pG-tR7zE8a45Mj6QuVhls6"
            + "ka45vCEJpex0yMNs.zFFs8uq5E3clFESX0Cqa0g";

    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiZGlyIn0.._bKNyOZXltUhrAFv0q"
            + "EuwA.K3o7R8cZNdBPBZq0hWtCPQLFLaYTGbCSQMdOwWavBWk8MinRAb9RqxtHnVQ9tZN8uY7vz0urDYQxJTppcJFkch2ZVYtbEwwa5kWy7f5RV0F-YrWYSwLUtj097ndQJlZ6fHwfMp4fTWe"
            + "3fzdBFZV8Bl9LQKnFlEpWd4KsPz2wl3GHtG-xQfm9pphalGRlEQqlE8Iu4fPshlV5r2jPWIOzVwZRnSt7Q4ASPo4zurHfwjofh-GAeX1Cx3ifPGYNjE3Jt9L5sga4E1rzaj5fnGCMmW20DVm"
            + "Nul7WLnQwl4CfwHg.HYMDbdN-n2Cs1DiOp0IfIht_1IY_rCzi";

    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiYWxnIjoiZGlyIn0..LRCm-GVYkglvSM0Odw"
            + "5WEg.PEWRx5jOgLxQLQDxw40-lvNt-2oajCaWmwoNJbjCSmcOXXm2FeUvl7iBvZ2t0fwbPI4MEdkfVZ6nWVHmQu54DCcLkJcgHhEOr3uK4Rgzjb7sCURoN-3JGEAp3gfuTefF9fsg7quK21W"
            + "uDkMGoNLXa6PPsMzfCsTLfQBhtPwYC-4h-ME3YWyK2kCs13MuN5FLpf1NT9SWIUYjCT2YaCa5kd8jjSm606OJ52cKZXLJ8P9nf4Bkjl1n6hBOzEWzAgClD5esHFfZDPwJCM_GM_sXbVHGXE6"
            + "vQ0yTfs1A1LNwfLM.Z1817usCKwrCLr3IZt4a3ZBSNEtpswl5djnL1CANba0";

    private static final String EXPIRED_JWE_TOKEN_RSA_OAEP_256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.a4jKbnncR5LrV-dOB"
            + "gsqFsZ-U5BUgYNogGSCfKxFBq_lPa4UPsYPZqYBDJVYioiyh710Ml3vCVEn5uBZ_TTAo_WPuJiVjAnV72LHSEXYJJLQMtQbG0lx_fdAn-dj9uPOkiaf9XlGIv4Niv5UmKrSd4L2YOMrSqXsl"
            + "3zzwLKkxg4JYiEDk_TZ3JCHsAInFLj0I5MYsZrbGgjS2WrCEpLAgh2_VH4vk_GJ5aOIIgnHt72Jnwi0sWprc9y_SomUp1K2LSd4ItFxfVSas_TixfI5hO1jeru2RdtGY3ET4DpRw9B_aI8vq"
            + "ZWqI0RO_cErDPxC4Rs_j1WMKsjKxB9Fq5iA2g.gAa44wLt8T9WE3ZyHNe_EQ.6PmHDGm8Z8EXXAp0MRBQ4aWkVYy03D74LFt3FQltCARRtZTKW6G6p8ffdBj_buxQWz-Oq4poXGwSecdsCXC"
            + "aTaqv9Dic0N7mXxQH3IzOtbKfs2wrL4rvBDcQdq3uclCmC9BNWPg8UE_2mEjUF2szeTnDMX5HRnGktLS1QOqutBj-pTNQubERfQVUvZrbYKTN3lWTtt7DGABZQnzgMble6b2GhbEmjoyswfx"
            + "5plolJVLplnO08akEpN2HAYQWPBZPh-WQKUNRpKdz8D5pFc9czhtlVcH1jgclwJD-fsuSbU4.2xbJiyL1jTD5HsAX5M7_lg";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAt"
            + "MjU2In0.PPkPN_Hb0he9MCJD7f7Dp2Fj4-VBpISgM77WRJQ9MdDyFphKrGXAEbJDIPvelszg0Stc2M6jhrtZO5hEb913cc3xRTxe0vI76iJZeZ4YxJoYib5VE_dSqJmyU2_iKkMx1OVsk007"
            + "j0k1Md79GLJJDULB0JCtmQS3Vil6J7eQoLEnlz1GYoeFm5gq5wWQ5ne_20mNMWO2wpIP_oLBsvgCzUSXSx2JL5O46qWJNUDF0h7viOl-cEPQhCkmXEbObd8y_hs-LcJKCSVcNXJuv47zmLDP"
            + "HngQxmTDyWhvJf54sn3ZttfOTGy72wIfFF2HQ1moPNunwYhswT8LD-yoUeuT8g.zKpGOkVli_IVxj2wN9Ljwg.O_0hMygxmfOH1rTL63bwfTOAkNAeOcG7ATa7D9Lv7peCQZo9jiSUupSEFR"
            + "7S_ZoF-eFgFb6JO0eEZpM1jkF9H83Ynm9B2XIcq0B7as_3L_z63AEa5zqRZBIugg7IpHeDR1aYBmiPjRT52LJXPHilRfwkM8KXHruuwoLA8KKfuPOyDDHGEdq4ZkZdqBXSZnRw7-rAH8kX_I"
            + "Crfm4t5-O2Yy-ij4yuEBHmoO_4qm_MnB0.WT_L1eKUGmCXBc5j3sXVZw";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMjU2In"
            + "0.Jw_2lRLDjPtGLL05LeQeIlNVVUxuI6IBnbbjfZZe_K-V1BUrRjoeK-4WBWniX7mGfyu_Rjv3LaZd2t_vwNUyT2JC8PNTZmJAhqHiybGR21Tk5E0JKBoMDQ9-q17Lo7w4jaEPOFfZx0aub9"
            + "touTUYsI5luy6_50lFKZRLZAEPeSvmYBNpS-CMAIqTTyF0UkbDzDaNxY86d9dfbXqJXQYKGwW393h2E_hpTAQT9xi6EK_t5ygH-3-c6T96OJw_JIKWWgy0rRxe7mYaje8oO7iR45LxNOu0i3"
            + "m5_qM5bl04h75NIqhZYYwubqcruCV3u9Ey65XKvd1VA-qVqPlAzgTowA.vdx4ox1jGc07PBb8TuoPeQ.V0NfZk3Ugb-xPLAQIrA88ntGIT0_UeO7DuoHFwDkAxv7BGFp57hzdpAEKGfiouhB"
            + "ZPkIo8QNUNSbdQe7mMDxgNZp-q9JDKoxGkG-voco6ZP-_tTP5KiCCvZi3bsli_VmdHqxpB4QCt7dV_m9hE16625joGercLUgtApJo5iN4fpshY7P2LdR3m_DVHGoG08_mtFlZFh9xtOCLH3v"
            + "hrSo2dhJjEtbZCSyo_KxB5l3DFw6yRbTdMiQrjl8BPs9hyBPnmRQZzTKFAUL4QdOfYXBJh4i50JBEHRuF0K3ApZdf7I.jLY1qrLo9wC7qpnGxmiysg";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiUlNBLU9BRVAtMjU2In"
            + "0.BtHLLDNQ2Xa3OrsGNEZ1f2uYm7zA00eV371afuzjMp32CzYVDvB8Mq5TfPVJL2Zmrc0Gmw0a5QdKX0dKRMESOZeKJhCNJlJz_ZX2WTOLWWBF_fn4gxrd_-kHdhgqfixHTdsbG-umNXm9Kn"
            + "BiZXBIJHQwQpCWnVDGCuCbQP5XPPwH_tUeEJMrgUvnMqQAriNcDObjnrXjbmmxjy7-Rx6ZtUQ9pfAab1rtBoEgulQCXbu_l4aGr7uaYdriaHvUQnGJzEdInt75OE0wHzaqvbJCze3m20u2zn"
            + "gaF8MtmG-IueIEBpq_lc-NuuXcJ0U2E5P_kP6rp7r-rooGC_wkHjtjag.vEUkcYR1Fv8AkGpIO7h8kQ.4oDW_GaJCweG2v9DgPzUezlkXzcb6IIMv0NcOTsek-CE_XLIs8uIDd2Zplz3DQNX"
            + "nWKtwfPi2z2a0i8zxhcX-4mkgUXcszbqpWo3vMZ6WpFZ2x3OSFtQajStwpWtL8pCQn6uKx5C_luvrpoG_-CxDSmgqs_RO-11sBxdMAzYDYtUHRpU_TYcdh4NcAZoWPQ5Fuci4fefC8-xSdTP"
            + "1wrYvOM8iaTm1OGWAnDQACJNzmTJ50S3cSlFaE0tsjNlZeYQIm5viALrWpQ9ua9aZwKYNS_sw_S66vUyAD3rOk69L0c.LFHPXbQWOBwYPXufk_2pPIK5sNCs9oqJ";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiYWxnIjoiUlNBLU9BRVAtMjU2In"
            + "0.dMYDJUQHh4me-3owKMKIhgYfUTvIwozPM8wYX9Dm2Q5mc0hhAFjdJnY7vQJS2uy2GEacf-3QDn7YPhTCKUjGtZ4ynC5gJScbnRKYY5W39OtdTaoUJTEuHR19B80yiVLoPPGlnbD8gcbCiM"
            + "ZnZaMsa1UcdWhJfQPH615g-8C--Vx4ELcuAszayey9yvQo-f1x1wtwzctHd_ZrFVsDj0FCSQDvf44Q0G8-dDFR--mI_CNcex4ikkGfsEQxgAHapz4et96O3C4HVdJNEN0hMvyMDU9gojDFkt"
            + "C_uxeZNyoYr0r1hZ17IBFxQKNKwpSSEkmiNsrbbdmg2Sibj3IhHEG5eA.bLzdKCyLi69-MrykNgVRZg.cq2GDPcOqrgFoAPtVCqjSe836AUGSj8J5ftEFUGc6h_CNHWjvqacbZHSw2jSQr6r"
            + "JrFdO-4ddc-x0HkA_gqLBCFl4Oi5jOSESohvxxQnESixCWMAKgvB6bhu8gWNee7VAtthlGNpY5eNc73dizTiT9ogbVczfEnCYzNEU0DWANU9_DIHatFislg1El9LYTgwpOXv_yZczmJtv7s9"
            + "T6OqlNf4U5lhbYXX4Nc5FqFy5ka08hZuqUgWdby9LiRx25FOOUcSwbmE-UCO9-tLlEXacyKvuXThwabPlIer-5tqd-g.nvNUGeCsW3_UVqRexYYHuQ84iw3THOoSsuLxf-sdWqw";

    private static final String EXPIRED_JWE_TOKEN_RSA_OAEP_384 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMzg0In0.fCTrZtqM_uw0P3yPf"
            + "VQb6tDFuK9vDJJ59qapfk9VktFBh6eaedW8G-xhGSvMmk_dlDyd72VkrFR1FRPKM4U5EWgB8RV26Y9sPy44_VtLeWU9nklHDJF-Gul47VM-lZJmwI6XeFe6JayPwfq0O2I5w8_uFsM4y9llm"
            + "YYdME2Zp8QyRPluR5kJ-va8T_4RwbNT1hEXTJMdzQl61_yxQP-A7CLg8IqZPjhJoV-qqkeaeGppAsXj3OHLd5eBP3TL872IO8yKKYIWCb693rtZjJebWhETMl-tAB6bnrznBA0OKd2nNpkVC"
            + "nZRVeXVxGXoVPidhSFWmET99uw6xuaB4atJsw.frN2HbNwSg-6S72Yoznq7Q.3dgu-2xj8vUrkIjDz7mQv2u_TiX23BwEBDpmPGe5BZu_8LrNyOkpN_zLSoqa5IO_QEtR8CHV1p0YZM37waK"
            + "5gGAt8nX8LC2eH7vzhtbCImtFJ2QGDpFHTAYQEfC5WorCXmPHWaTMJeqT6828XxNP4XQtdEz1rgu53EWAh_Ph75ks_0kso9M92ep5woJ9XP9wsUDCShnGlWAcrDqXCSD5NsLtFNaZ29-zPYe"
            + "pWoJHmQrLBxItZ4JuYUj8XAs5FnyjbQAqB0JkDqLqzXto0hkruC4iGrsfNinWKHY01N7rcVc.JNIn5nWrttZ8xqAcI5ns8g";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAt"
            + "Mzg0In0.C-PS3AexzOpFbi2vlwtp5S7AwLWQzG0tcLGCDoPZoSmNGxcEPwFv78qM2UAH1rMJXJ07azUZDX3murEOOUE69yI_RkDkE9sbjRblA5LAb4S5Lda_nn5Gv9aGVTYsMdQe3a0aSiFJ"
            + "xW9UYvo_2zPjo4SK9h9RLwn0uUuWqpUR2Jg9HXUuoQcZy7wMwQCDSJc5YLeBhxnvgdchX3wabejiINr2zdvdoBENT_IjYe3B78mp_RadxA6NM_hHOiOPqQu5gUD9R0fPBWk31MLYYCR2NfJw"
            + "gUDSf4x2Nrm448QE2jcEOy_FfK96NHfpp9zIAl_Rpz9jedSsfCOwj9ijOPgz5Q.kDysl3FJqLnqNorHlEQ-Iw.XeuQqzTuqPnKB21T22zyhuydyxf38cUUJvPk3n4cH4t-uVeUuSUjnJ78c4"
            + "Lr1PHGf0agaVGmNimdnz4TchyDlbZJqUSpDSx2XuToxsuT_vhaLua287vioXfx_pCExh9hU1sWPKG8jt6D2CNCx2geJ3g_05A60EIS12Gb0mVhpnR9OC7P4t60SMfUWC57nRxqmrVMs77I6N"
            + "VRtLJhf6dYkAMGDrKZLF4KxsMOXpwSn0o.uup98y4tGZLIrEwBKMU4bQ";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtMzg0In"
            + "0.TfjUCx0SXAvrGW4esRxXFudfvMh--VOrs9KHCeEEMksTMxDejmJgf21nSsdz5NLjGJDaPIUTRDNHGGNCdpuHG3E108ZT2G55oSBNMRfm7NMYFzlyGtARruoFMdiRtqT1kwi36xnDjnlsL7"
            + "yTiVScZpGL2nn_LJ5x7EVwePxBhZ911cNwj53hoVofZCyCK8HZFTJPHys4frmUu60ZECFrADSvzOG-E1pyMmPxhc8eXqWmxe1tvKAm9Hrr-p5OKV1j51dqQjo2-8R1uXl8Rhnu5mddbfEL7r"
            + "EoakNCRUz9h_R6HqJVKsWeOyEaR5K9sSTCe_6XofC02_iHJ8HMlhaznA._NcM8ZGMbpVlgCRBfQVRgQ.hNxFDQmCdrvbXhu5Kn52L6mCCWSNChHVd6zcAH1onFRiy954BwyXoAfSDQ4eW0uk"
            + "dbbEirs9p7vg1c_WQ03JjePVd8RB1q-lOyP-wNli9sbeGk9g5kdlkWu-kZEm8xyJJyVHwWR_LMjjwHQeV5SUnhX14N-KWr91_CR2rqHzpcs5u31G_HtslqN1WggD89JFi6BZSWwSP5AJL3Tu"
            + "6r8GRtdh3vM1VpjSvmBKE9ar5M62si3E_imVvBEL_Mb4_POLgn6wF0kAaaM0ELIFD6G8lqZnh1Flzdezj78NGu1_rWg.U9JZ3NVM5QxEslKdeWXDmg";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiUlNBLU9BRVAtMzg0In"
            + "0.fe3ZHI5QW9nzzVpWHMpXQJfcDOMHGSO5t-ahvzWDB4HHn3wyvJzugqYbY8T2Hdsu-8ZRNufEzxplwNCvFff0MKMjJnuiEvsUEOg7g1WTB4tiOCcir_Xq12QX6dzELq7sl9YfEjmk7_xaiL"
            + "AAUQpRod7gEDCW9_dftpDy1gKaoQO6FSg1hioeFaihqVX9SLQpKp6miEDmnp3UwmXmRixWMNcvB-6XoRP5ow8Aw8dNXqWrMSmGzRq6gj0_uQgBenevKtfWkqRAaoIdohUyxtErBlvSNvuZQt"
            + "PXqx-TAG-VB1hZfRR5zskN-p6sZYqDtcjI_4_2Z5SXL9oGY4PVRMDsUw.g58hSesNWoMoum76NJxBIQ.JrPQwg4NrYIB5TPIRccKDUy2hfZipux7L2fKQ73N9tC65_lKjvlroyyK6HPEn5kM"
            + "mH2orhyPddhavKpyuiyjCjk0l6c-29aKZyKLaGhsvJPXtnFassnINw8niLfVybR0hkfQi4x_8ZFx6fkmdsGhG_p6QSzU2Ax4bswBF8pfBMRTW6vGkqUz8_w5tDuuk-qcaj2QA9w7JunxAfaZ"
            + "QwfePy9pW5A1KvEHjoMx0kW4PplYGZU8gt-IADFcexBgAOJxe6omjUcjERePFcYMjP8x4X0rFhqiYRp76nEyb4HKBro.vppADJRw_05-kErj8bQbiOvlm11sFaHY";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiYWxnIjoiUlNBLU9BRVAtMzg0In"
            + "0.Ne-KfR7XRcmW3iSYcyjlea4LGIcIhbSu--LXjEGr9x-aTgQEOmMPCXNPFCNeGTFGdP1IdVF_IkbDcdWp0Fpk08DjuUAZKcPt2ptdokGvsVj7mXW0_IeaP6Fe6Af-cRbeu4AyfNXg8lTlXN"
            + "vJA4efeFPhQdF6-NmuW9JwGylSsMFZy6NDIVjuWtWvIEEcbJfRZ4voqcvALamgYa7b613WTbpHfbjSLqc-JZ7YweFwO_kXD5yK7ShbdBFuY-IxWyijjIYHu6YD2CD9MAaX994XwMuNMVU3yd"
            + "71Vlpj-GL8wUTr73HIlRjiNmc_5XUfqTx1ZqBdUeVJnUUe_9M1whq3rw.33P_Vb_Ng0As3GE1E8VCxA.2lUszyPJ376cjwfsZc4gy5BA2kyWUisNjFMUZnwhs66pdTqhKtMVWUWGi9mh-rsL"
            + "8Y0T0YjVwJbdGpLRXgjElVZq5OfXBqAmwRUjzO7-sKne8Mx0P59ChhOGMEnBgMro6Sv7_dQ-wFh32ISgBRwE1ygKJ8DKz8-0qHT5K7LVXuCwAmikruqwdtU1pMhF9rXqUt0X7hCPjSOvjcaR"
            + "Pa45ZnUWO4EewZnRpqIjgkmxL6rBitvKpcdAflWPwJl6PDfq18dhlLYn8gDQjKdAr4-BGchAhEzU818i0b3caFnjNtw.yc_3VFh9wWnL1Tek3KjmdtRvL0rXZN2FyUq0hMuPqLg";

    private static final String EXPIRED_JWE_TOKEN_RSA_OAEP_512 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtNTEyIn0.ZQ3KDtQClMoQxGxpp"
            + "ZO_Tft2UgR0HV4A59yCmpsMiFVaDOhFYTuly0QbQZXQvhkyP_VkqQZOttlL1ns5Jn2CnY6XqxY3lp9uoalnljUGB6B-AEv5hnR5fTtIaylmgG50I7s1bUVwhAcyRpjp3-HoROUTKQIUK7JCf"
            + "Fqzh6pDCAtYKokgkX2nNSgLJlwmsvNrHmLupXu6qBmYa6MUyA24BBAL8x3oWKNOTNX04HX1ADcSmFn-ozoQh5LOHrFgzeYW6c9bJU2Hhgs1oAHs6hYJwRcL7mghvcwvvfs0tSpigVwZLIUuz"
            + "19XrmwqhXWrpoK1MmMrvw6Z1iJ77D7uljqpIg.QrnIgr55_91loBc8XFQNkQ.rJAWwq3Km5jCp_5hkjrNigQWmPFe6uYtw4FeLp2cbSganZa0WuGwEXGtZsMsRJZnuiLJ0BFTcThG9TGQWCh"
            + "ryIqZ2SnrYhxrty90t_Ra20sfDMu7jCBlHXFsZXljeMtxCKsOxMo_turXz10NR62O8Yn-gYmsIM5QUevYnVU-OSMcrRuh3SD8xWxeHB3oC-8coNfIol4tTciTLTjPuS-s6TABtyCiyHAYK41"
            + "XIkSjOGgcdNXIH9DuyqUpU92dHaj6_ckzZPCppMlLZeIJBxhvl5m2PdUYcM2jzuJn14BWfLk.LDbZaZJwayZejhRPn4ph7g";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAt"
            + "NTEyIn0.IvI2zHdXowRlJyLuHXjWbhY2K3KUhT7rjSqKA2YPv8LYooVeAGLqnZdMixDhV8wTpNvwN-quaIe5T0qeOc2EPijvCag-1YKQHoUylSZxWKt9ilugnC5nnLFG8unS3rmqMEm8jWse"
            + "EOc2Lgj4Svn-XnOK33cxCVaG3dA15OABTzKNJcf0Np2qBMSY6AFIYdk1CdBY0n9IA2-nEstIzytqb9N8n3q48A70atFXGt49sBeRt3IJEIv6ClskP3dvRsK334icLL8DEtgq67xE7qsHw_8J"
            + "sCCNMktoueNNaIUbXdNVjpVUmCuWr3t63P5JxaQSdpfricV5uvo8RQzwJGXBrw.OrHJNr5_3o8IXio7wlcXbA.xIN6Mj_zeVsh5CwBhLnfUv1f63rQ-znNYBwZzNzPtN-VAJU5_Vsk139lrQ"
            + "2LemyuDMKelS_PhNF6gI57b5CnjEznLMCZmNx5JJ-nVgIwlj23-utyn0kVOwvSv2vy_ikKm8nJjHNiapxq7G2U-We3mOW4hb5ep6vr4LiAbpXH2QQ3y4gZaymw6yuVBbiE76j-06UhozbtaL"
            + "sMTMEUcdae1alpbthgEAlWmdGjabX_6BM.IO_X9iQqubjjQqUoMvxAHw";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiUlNBLU9BRVAtNTEyIn"
            + "0.gvkqnWELco3tSuVWiTEjsYzvJzeQIQrQ4g39rsYX43j3OkX3kFg_CHx272DkXAoZ2lBfXeSI6ERLcqrtmbwWq8iVTe9GFCml9Ri4QaEmnmVm6OFGPdTPOwrBE42eDnBBxXMLemd_1vWfoF"
            + "wB1RWCO3JFtyfpf01FmIccihIjy5Ic3DfEOua3l30DH9clQiSNz-Q00cC4rDi2S7hkuV4BVSSfuLOy6FiUfbIALBUQG8LhSql3U73Tjk7yEobl8YG9zuSuLF0uhEZ4fOcQitrac4N1qGBgtb"
            + "AAurHPuKF7vd_Mag53tpWRWblKjLcPHk2JYH9msopgkFrQDI4clK-xJA.SfuV9IIPKMHvMJ5z6q-KaQ.o9GgEdhOhB2HPYO1D6994YeDROogOsnxtZpEYxTLqGY3x2iSLGVH62fNH1yq7MMY"
            + "b3VjPfEKgHnvzy8GK8jES-eGQJpj_MtAVQb-qS8Q2MkEREJT_N8sV-V9eO74uRdsWgvnjBkKbOkhcsb9xGumsnYxMU7iQ3toLdSpkSDCgmzLHVftT_k_CczecXMWJ55bP9M3oLg9zRyMWdu9"
            + "PgxP7baVg_aiI-J-VV1uGuaKvAfKVUwH9k8IpTXpIzT-UTMlsabSpG5-J0yqoE1wcVwivaRbt_2fhv_hIC_UgnlgHUE.PTkqi35m7o04xJPPCPdcwA";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiUlNBLU9BRVAtNTEyIn"
            + "0.enn9Fs4Z2vlDJYdaZ6JYIFl6k8ZQtKSnaobASlLwNJdrtx7gZyfNsBplHni73d0d6jwPZElDVwDIji8fbhbVD2XZZcXuwNKEfzgHt93dmNyL9TPy_gr6Oyxz2n-u20LDlaPa8ZR6UQJ5qz"
            + "WYxS9yFGkyhqEcQM-vX-cdR0Q5DEF27MgMlrRz20wqJh74_lgOjU-lt7nDHRRVwBAdcWnQd2qerelZhDkDfZabL18VZnJPBUQHQm7SAEYDQhsK4Otajn8deQJIkAjE90YTOVnlIOWW8WPFz2"
            + "mUsUUsKmdApxE17FLguWcZEMDArYLanFh3oi5REAKGS9QLE6-l5oSROQ.XWlfZGzwQNPTe_vV3mdP3w.TW1YLOmlnXMiWIoX18M6SZ1LcuHMW3xMyvESA-MpEzo9wCYa2VpJpDy7pB_x5sWr"
            + "uDVkIA0ttygdopX3r7qZgzMoPJRlM7bi1JBc-ZThSw_Q_vkcH7gfvaJeiQzOdM90GMJdEytY_szZQcboy85eHoySTrhw1tIBzq91f5Od8kpQ06K773nKeBptlCuxT823VyOqp4rWefUGBf3w"
            + "nLEStfQ2wK6hyYEX0rPL9ppTa5B-4cskkmaDWUvriWKyX3Smf-M6A6KqjJrpLpaj7xGYPzdpc0X4sBPi3LMTKds-j0Q.6l-b1wBDh7jiwasgRr6nBIyrRfskGGKf";

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIiwiYWxnIjoiUlNBLU9BRVAtNTEyIn"
            + "0.J5KytvNYpyqgd04rkwGhUY4fgzob6NvHO095iYbfmVhIhCJcxfFyZmS0Av-0klDuF6bHOgzFEPN_tMkgaa-O6jZ5m5SJoqzuwFvBiMf9JEeVJNWuhBdGdLWaoikaVdvWxq8RpJOd57w3OU"
            + "g2iEqoUdEJz6b0PPu_VHS6hNACO3Z1FEy7UI7yQjeljOrpkt7FrVD8PhtlQCXs6YtlbdOMtcFBEcKDqPRyOEA0N772O0W-S_D4FH7xHR7SAOmXnKnz9SSeRTF4Q0Y2zVmILqRKLetleJ1Pf2"
            + "nUQOzeKKQYiabGX13jrkgHl-vpJ1qF9Ev8HVbRLBPY9bp8m_ZLZ05Cqg.LR6-Hk1diF7HNTGXV38JUQ.T8G71AuLBg5Ar-XQGB0Okg978i8pINcgQpClTipwiepWV86sOId2HIkei3G5Hk-C"
            + "25_DJ9mXzyAwaLDJOQ54-P_8tfHY4KvvUpYnrgJa26FINWdA24_UOCuR0JIru4XrVCGO9yRrNwy3acGjWCNjVcFpqoW1zliZEFfrttKcJpanL7RNSfwVlO-uWzBtaSWzQezjBjyu9wditKFa"
            + "FvO0n05QxmVVbsuGm27bXy_i2brsl8MUB2P1rQjM4qRpTbTJBR8ep-KHwCcNA4h0xY2QL2tl3r2xxNolu0MNQAwBZMk.3D6CIGoV1E2fUuHVB50l6U3pOa-sZ9FhRgyqpDQrVus";

}
