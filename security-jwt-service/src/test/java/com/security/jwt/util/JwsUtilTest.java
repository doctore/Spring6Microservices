package com.security.jwt.util;

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

public class JwsUtilTest {

    static Stream<Arguments> generateTokenTestCases() {
        Map<String, Object> informationToInclude = new HashMap<>();
        String doesNotCareSecret = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   signatureAlgorithm,          signatureSecret,            expirationTimeInSeconds,   expectedException
                Arguments.of( null,                   null,                        null,                       90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   HS256_SIGNATURE_ALGORITHM,   null,                       90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   HS256_SIGNATURE_ALGORITHM,   "",                         90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   null,                        doesNotCareSecret,          90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   HS256_SIGNATURE_ALGORITHM,   null,                       90,                        IllegalArgumentException.class ),
                // signatureAlgorithm and signatureSecret does not match
                Arguments.of( null,                   HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                Arguments.of( informationToInclude,   HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                Arguments.of( null,                   RS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                Arguments.of( informationToInclude,   RS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                Arguments.of( null,                   ES256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                Arguments.of( informationToInclude,   ES256_SIGNATURE_ALGORITHM,   doesNotCareSecret,          90,                        TokenException.class ),
                // ES valid generated tokens
                Arguments.of( null,                   ES256_SIGNATURE_ALGORITHM,   ES256_SIGNATURE_KEY_PAIR,   90,                        null ),
                Arguments.of( null,                   ES384_SIGNATURE_ALGORITHM,   ES384_SIGNATURE_KEY_PAIR,   90,                        null ),
                Arguments.of( null,                   ES512_SIGNATURE_ALGORITHM,   ES512_SIGNATURE_KEY_PAIR,   90,                        null ),
                Arguments.of( informationToInclude,   ES256_SIGNATURE_ALGORITHM,   ES256_SIGNATURE_KEY_PAIR,   90,                        null ),
                Arguments.of( informationToInclude,   ES384_SIGNATURE_ALGORITHM,   ES384_SIGNATURE_KEY_PAIR,   90,                        null ),
                Arguments.of( informationToInclude,   ES512_SIGNATURE_ALGORITHM,   ES512_SIGNATURE_KEY_PAIR,   90,                        null ),
                // HS valid generated tokens
                Arguments.of( null,                   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,     90,                        null ),
                Arguments.of( null,                   HS384_SIGNATURE_ALGORITHM,   HS384_SIGNATURE_SECRET,     90,                        null ),
                Arguments.of( null,                   HS512_SIGNATURE_ALGORITHM,   HS512_SIGNATURE_SECRET,     90,                        null ),
                Arguments.of( informationToInclude,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,     90,                        null ),
                Arguments.of( informationToInclude,   HS384_SIGNATURE_ALGORITHM,   HS384_SIGNATURE_SECRET,     90,                        null ),
                Arguments.of( informationToInclude,   HS512_SIGNATURE_ALGORITHM,   HS512_SIGNATURE_SECRET,     90,                        null ),
                // RS valid generated tokens
                Arguments.of( null,                   RS256_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null ),
                Arguments.of( null,                   RS384_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null ),
                Arguments.of( null,                   RS512_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null ),
                Arguments.of( informationToInclude,   RS256_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null ),
                Arguments.of( informationToInclude,   RS384_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null ),
                Arguments.of( informationToInclude,   RS512_SIGNATURE_ALGORITHM,   RS_SIGNATURE_KEY_PAIR,      90,                        null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenTestCases")
    @DisplayName("generateToken: test cases")
    public void generateToken_testCases(Map<String, Object> informationToInclude,
                                        TokenSignatureAlgorithm signatureAlgorithm,
                                        String signatureSecret,
                                        long expirationTimeInSeconds,
                                        Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
            );
        }
        else {
            assertNotNull(
                    JwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
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
                //            jwsToken,                            signatureSecret,            expectedException,                expectedResult
                Arguments.of( null,                                null,                       IllegalArgumentException.class,   null ),
                Arguments.of( null,                                doesNotCareValue,           IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  null,                       IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS256_SIGNATURE_SECRET,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS384_SIGNATURE_SECRET,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       RS_SIGNATURE_KEY_PAIR,      TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       ES512_SIGNATURE_KEY_PAIR,   TokenInvalidException.class,      null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS384_SIGNATURE_SECRET,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         RS_SIGNATURE_KEY_PAIR,      TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS512_SIGNATURE_SECRET,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         HS256_SIGNATURE_SECRET,     TokenException.class,             null ),
                // Expired ES
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   TokenExpiredException.class,      null ),
                // Expired HS
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     TokenExpiredException.class,      null ),
                // Expired RS
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      TokenExpiredException.class,      null ),
                // Valid ES
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   null,                             expectedResultNotEmptyToken ),
                // Valid HS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     null,                             expectedResultNotEmptyToken ),
                // Valid RS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      null,                             expectedResultNotEmptyToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAllClaimsFromTokenTestCases")
    @DisplayName("getAllClaimsFromToken: test cases")
    public void getAllClaimsFromToken_testCases(String jwsToken,
                                                String signatureSecret,
                                                Class<? extends Exception> expectedException,
                                                Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JwsUtil.getAllClaimsFromToken(jwsToken, signatureSecret)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JwsUtil.getAllClaimsFromToken(jwsToken, signatureSecret)
            );
        }
    }


    static Stream<Arguments> getSafeAllClaimsFromTokenTestCases() {
        String doesNotCareValue = "ItDoesNotCare";
        String notValidtoken = "NotValidToken";

        Either<Exception, Map<String, Object>> expectedResultEmptyToken = Either.left(
                new IllegalArgumentException(
                        "jwsToken cannot be null or empty"
                )
        );
        Either<Exception, Map<String, Object>> expectedResultEmptySecret = Either.left(
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
                //            jwsToken,                            signatureSecret,            expectedResult
                Arguments.of( null,                                null,                       expectedResultEmptyToken ),
                Arguments.of( null,                                doesNotCareValue,           expectedResultEmptyToken ),
                Arguments.of( "",                                  null,                       expectedResultEmptyToken ),
                Arguments.of( "",                                  doesNotCareValue,           expectedResultEmptyToken ),
                Arguments.of( doesNotCareValue,                    null,                       expectedResultEmptySecret ),
                Arguments.of( doesNotCareValue,                    "",                         expectedResultEmptySecret ),
                // Not valid tokens
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     expectedResultInvalidToken ),
                Arguments.of( NOT_JWS_TOKEN,                       HS384_SIGNATURE_SECRET,     expectedResultInvalidToken ),
                Arguments.of( NOT_JWS_TOKEN,                       RS_SIGNATURE_KEY_PAIR,      expectedResultInvalidToken ),
                Arguments.of( NOT_JWS_TOKEN,                       ES512_SIGNATURE_KEY_PAIR,   expectedResultInvalidToken ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS384_SIGNATURE_SECRET,     expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         RS_SIGNATURE_KEY_PAIR,      expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS512_SIGNATURE_SECRET,     expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         HS256_SIGNATURE_SECRET,     expectedResultTokenException ),
                // Expired ES
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   expectedResultExpiredToken ),
                // Expired HS
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     expectedResultExpiredToken ),
                // Expired RS
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      expectedResultExpiredToken ),
                // Valid ES
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   expectedResultValidNotEmptyToken ),
                // Valid HS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     expectedResultValidNotEmptyToken ),
                // Valid RS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      expectedResultValidNotEmptyToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getSafeAllClaimsFromTokenTestCases")
    @DisplayName("getSafeAllClaimsFromToken: test cases")
    public void getSafeAllClaimsFromToken_testCases(String jwsToken,
                                                    String signatureSecret,
                                                    Either<Exception, Map<String, Object>> expectedResult) {
        if (expectedResult.isRight()) {
            assertEquals(
                    expectedResult,
                    JwsUtil.getSafeAllClaimsFromToken(jwsToken, signatureSecret)
            );
        }
        else {
            Either<Exception, Map<String, Object>> result = JwsUtil.getSafeAllClaimsFromToken(jwsToken, signatureSecret);
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
                //            jwsToken,                            signatureSecret,            keysToInclude,     expectedException,                expectedResult
                Arguments.of( null,                                doesNotCareValue,           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                doesNotCareValue,           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                doesNotCareValue,           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         keysToInclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS256_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS256_SIGNATURE_SECRET,     keysToInclude,     TokenInvalidException.class,      null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS384_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         RS_SIGNATURE_KEY_PAIR,      keysToInclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS384_SIGNATURE_SECRET,     keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         HS256_SIGNATURE_SECRET,     keysToInclude,     TokenException.class,             null ),
                // Expired ES
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   keysToInclude,     TokenExpiredException.class,      null ),
                // Expired HS
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     keysToInclude,     TokenExpiredException.class,      null ),
                // Expired RS
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      keysToInclude,     TokenExpiredException.class,      null ),
                // Valid ES
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   keysToInclude,     null,                             expectedResult ),
                // Valid HS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     keysToInclude,     null,                             expectedResult ),
                // Valid RS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      keysToInclude,     null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadKeysTestCases")
    @DisplayName("getPayloadKeys: test cases")
    public void getPayloadKeys_testCases(String jwsToken,
                                         String signatureSecret,
                                         Set<String> keysToInclude,
                                         Class<? extends Exception> expectedException,
                                         Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JwsUtil.getPayloadKeys(jwsToken, signatureSecret, keysToInclude)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JwsUtil.getPayloadKeys(jwsToken, signatureSecret, keysToInclude)
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
                //            jwsToken,                            signatureSecret,            keysToExclude,     expectedException,                expectedResult
                Arguments.of( null,                                doesNotCareValue,           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                doesNotCareValue,           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                doesNotCareValue,           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                  doesNotCareValue,           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    null,                       keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                    "",                         keysToExclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                       HS256_SIGNATURE_SECRET,     keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS256_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWS_TOKEN,                       HS256_SIGNATURE_SECRET,     keysToExclude,     TokenInvalidException.class,      null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS384_SIGNATURE_SECRET,     null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         RS_SIGNATURE_KEY_PAIR,      keysToExclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS384_SIGNATURE_SECRET,     keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         HS256_SIGNATURE_SECRET,     keysToExclude,     TokenException.class,             null ),
                // Expired ES
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             ES256_SIGNATURE_KEY_PAIR,   keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             ES384_SIGNATURE_KEY_PAIR,   keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             ES512_SIGNATURE_KEY_PAIR,   keysToExclude,     TokenExpiredException.class,      null ),
                // Expired HS
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             HS256_SIGNATURE_SECRET,     keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             HS384_SIGNATURE_SECRET,     keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             HS512_SIGNATURE_SECRET,     keysToExclude,     TokenExpiredException.class,      null ),
                // Expired RS
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             RS_SIGNATURE_KEY_PAIR,      keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             RS_SIGNATURE_KEY_PAIR,      keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             RS_SIGNATURE_KEY_PAIR,      keysToExclude,     TokenExpiredException.class,      null ),
                // Valid ES
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   ES256_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         ES256_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   ES384_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultEmptyToken),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         ES384_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   ES512_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         ES512_SIGNATURE_KEY_PAIR,   keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                // Valid HS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   HS256_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         HS256_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   HS384_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         HS384_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   HS512_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         HS512_SIGNATURE_SECRET,     keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                // Valid RS
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultEmptyToken),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         RS_SIGNATURE_KEY_PAIR,      keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadExceptKeysTestCases")
    @DisplayName("getPayloadExceptKeys: test cases")
    public void getPayloadExceptKeys_testCases(String jwsToken,
                                               String signatureSecret,
                                               Set<String> keysToExclude,
                                               Class<? extends Exception> expectedException,
                                               Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JwsUtil.getPayloadExceptKeys(jwsToken, signatureSecret, keysToExclude)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    JwsUtil.getPayloadExceptKeys(jwsToken, signatureSecret, keysToExclude)
            );
        }
    }


    static Stream<Arguments> isJwsTokenTestCases() {
        return Stream.of(
                //@formatter:off
                //            jwsToken,                            expectedResult
                Arguments.of( null,                                false ),
                Arguments.of( "",                                  false ),
                Arguments.of( "NotValidToken",                     false ),
                Arguments.of( NOT_JWS_TOKEN,                       false ),
                // Expired ES tokens
                Arguments.of( EXPIRED_JWS_TOKEN_ES256,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES384,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_ES512,             true ),
                // Expired HS tokens
                Arguments.of( EXPIRED_JWS_TOKEN_HS256,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS384,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_HS512,             true ),
                // Expired RS tokens
                Arguments.of( EXPIRED_JWS_TOKEN_RS256,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS384,             true ),
                Arguments.of( EXPIRED_JWS_TOKEN_RS512,             true ),
                // Not expired empty RS tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512,   true ),
                // Not expired empty HS tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512,   true ),
                // Not expired empty RS tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512,   true ),
                // Not expired and not empty ES tokens
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES256,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES384,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_ES512,         true ),
                // Not expired and not empty HS tokens
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_HS512,         true ),
                // Not expired and not empty RS tokens
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS256,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS384,         true ),
                Arguments.of( NOT_EXPIRED_JWS_TOKEN_RS512,         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isJwsTokenTestCases")
    @DisplayName("isJwsToken: test cases")
    public void isJwsToken_testCases(String jwsToken,
                                     boolean expectedResult) {
        assertEquals(
                expectedResult,
                JwsUtil.isJwsToken(jwsToken)
        );
    }


    private static final TokenSignatureAlgorithm ES256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES256;

    private static final TokenSignatureAlgorithm ES384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES384;

    private static final TokenSignatureAlgorithm ES512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES512;

    private static final TokenSignatureAlgorithm HS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS256;

    private static final TokenSignatureAlgorithm HS384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS384;

    private static final TokenSignatureAlgorithm HS512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS512;

    private static final TokenSignatureAlgorithm RS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS256;

    private static final TokenSignatureAlgorithm RS384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS384;

    private static final TokenSignatureAlgorithm RS512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS512;

    private static final String ES256_SIGNATURE_KEY_PAIR =
            """
            -----BEGIN PUBLIC KEY-----
            MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEhg/0fhvKZZdMj/sZpWn4KSPLr/Cb
            i5NklTivah7LysnELUylyJ5LBmyZoDOLG5coE2B5aFMxnVGxm6VxB7VZcg==
            -----END PUBLIC KEY-----
            -----BEGIN EC PRIVATE KEY-----
            MHcCAQEEIDHJEaGIz2IkvHB24ZTTvlMtPbjG+JJ+q/XzSAAJn6fxoAoGCCqGSM49
            AwEHoUQDQgAEhg/0fhvKZZdMj/sZpWn4KSPLr/Cbi5NklTivah7LysnELUylyJ5L
            BmyZoDOLG5coE2B5aFMxnVGxm6VxB7VZcg==
            -----END EC PRIVATE KEY-----""";

    private static final String ES384_SIGNATURE_KEY_PAIR =
            """
            -----BEGIN PUBLIC KEY-----
            MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEOI116z1yKfH1+omNkA3z959YL+fx2wWW
            08JfNt0ff33v35oFH7PPynK9+tldWGgjst0khk+dJfZYH3/JmDgKAgKTkvdwnBqz
            LcJngT0MJmLz3UGP9O0G15Dt33CDwMf5
            -----END PUBLIC KEY-----
            -----BEGIN EC PRIVATE KEY-----
            MIGkAgEBBDAm5FYwdmZyprAE965pH1Pqdq43nriaRXdgF+fBMT9E916EMvCjowW1
            joHITiuasGegBwYFK4EEACKhZANiAAQ4jXXrPXIp8fX6iY2QDfP3n1gv5/HbBZbT
            wl823R9/fe/fmgUfs8/Kcr362V1YaCOy3SSGT50l9lgff8mYOAoCApOS93CcGrMt
            wmeBPQwmYvPdQY/07QbXkO3fcIPAx/k=
            -----END EC PRIVATE KEY-----""";

    private static final String ES512_SIGNATURE_KEY_PAIR =
            """
            -----BEGIN PUBLIC KEY-----
            MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQANHKBULTT8JHBfYKY4MM5a0bWb5ok
            moRwwQQeXV0yudW5nC7Mw+hv9Tr0WhXfbzDBHfTu/KVKvjoXjZm1cCiNnkgBuYYb
            mL/X83Q9CAVCNT7p/8V7xuW/p7nIoEm71fqKwV5NtOcrsSxHQ7g7bamznLaG2a1L
            /4dy0+WtEkpyFtmaJOg=
            -----END PUBLIC KEY-----
            -----BEGIN EC PRIVATE KEY-----
            MIHcAgEBBEIBmuTYjyp06kHcRWgyF09x6352bI0G38k/1iC9K3DKEeU+5JjTGvZr
            lC3CClP0uYrqiUpQdPex6Fp5weJHJ4Ue4jegBwYFK4EEACOhgYkDgYYABAA0coFQ
            tNPwkcF9gpjgwzlrRtZvmiSahHDBBB5dXTK51bmcLszD6G/1OvRaFd9vMMEd9O78
            pUq+OheNmbVwKI2eSAG5hhuYv9fzdD0IBUI1Pun/xXvG5b+nucigSbvV+orBXk20
            5yuxLEdDuDttqbOctobZrUv/h3LT5a0SSnIW2Zok6A==
            -----END EC PRIVATE KEY-----""";

    private static final String HS256_SIGNATURE_SECRET = "hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k";

    private static final String HS384_SIGNATURE_SECRET = "hs384SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k_extra_384";

    private static final String HS512_SIGNATURE_SECRET = "hs512SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k_extra_required_512";

    private static final String RS_SIGNATURE_KEY_PAIR =
                    """
                    -----BEGIN PUBLIC KEY-----
                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlxytO6GqsfH52K91G31E
                    uh0V0BUb0NCfKqs4KvlB+GarYMVeOHIkjsMy+uRNmma4sV8QfrOym28NakdKFDb2
                    svEITJZsFGcF4wmW3jW0CPC16F0lNZfccdhmewUt/4bhRh0shs460mCXSXGSjmIo
                    9umgjyMe1WBHiFJjwG5AgNVVVIWjonPNWkAHYaqyo6qQXGE8GFuGBds3Mg8fGC60
                    SyW4xczPeHoPaBK6iLKbcmfN3WqDcrVU24o8w6FwJpB68efDX2Z7idCXx1ChmYjJ
                    benwQKg0d/g5bOoXpiQPlcQjtnSgQ6dr0hnyA3NZq9ZEKMxStyahSDXFiOd+75z1
                    lQIDAQAB
                    -----END PUBLIC KEY-----
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

    private static final String NOT_JWS_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
            + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
            + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";

    private static final String EXPIRED_JWS_TOKEN_ES256 = "eyJhbGciOiJFUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI1NzAxMTkxLCJpYXQiOjE3MjU3MDExOTEsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.JNEAU1ZzKgFnV3Ymacu5zBC3yyvnJSHuPFPdJ_vMVPYqhRo-2kAFszbhv7yaQ-A185E"
            + "JANLu11YP72e5uBX8Ng";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_ES256 = "eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.PKhGcOGNvJrzaTg6BDyBoNx2nt2pNb26bP_-0gn40e1gH0Wn1QWI0ZBk-1tWw14YGplSK1YSafjHyJeI6iymHg";

    private static final String NOT_EXPIRED_JWS_TOKEN_ES256 = "eyJhbGciOiJFUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.BDEj77LPUOHYJU_SjolZGmWjiyVUj-vPtGOj46oP-BWcjFRt1_Xa9GjBS4RTEa"
            + "mHbXMwQuvvtfM3Ms5Q9xDtMg";

    private static final String EXPIRED_JWS_TOKEN_ES384 = "eyJhbGciOiJFUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI1NzAyNTI0LCJpYXQiOjE3MjU3MDI1MjQsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.3LJhh4XooMJZOJ5zdGzQF3JpVFCGm9zuTNogfhvkNm8Yd65mvXwF084fuR8hbw9gue4"
            + "5W8XeDtxm0uPIHLp7yQVg6Z6IHxWS3blNhiz7PMHDKAuSajG67K_iDF3wNXnw";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_ES384 = "eyJhbGciOiJFUzM4NCJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.m2xDTakTILCocCtASPETJVPhCOB0lJGflXBHn705RIJAz7Mi_HQXHw_FGooz1Zb6uDcBw32bDwR9SCBTtyspPXNRVKvdZJwDSa268JiGJ"
            + "FhsT4oZscFrPbwhhHZFWPXN";

    private static final String NOT_EXPIRED_JWS_TOKEN_ES384 = "eyJhbGciOiJFUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.QubDRdcYkXN1U4WC8b1zKS2AOK3YI356WGILTKEgaC-frNQ87Hm78tNWOn9MYM"
            + "mB8-0YFVKyn0yHq17d-hYFxc6dyS73kS21jcCSTUDTpxtfV7ehLFd6guuBJTHIL5w_";

    private static final String EXPIRED_JWS_TOKEN_ES512 = "eyJhbGciOiJFUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI1NzAyNzA0LCJpYXQiOjE3MjU3MDI3MDQsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.AUCXRGYFDqkTJYMAEtDe5cUoahdsSbb5exBGhFp5IuMjKo2ztTlPS5_NShyVONt4c0D"
            + "Q5hMdVU4QkzjE6vbMTgaZAMg8VI0jpbG0FONLA3Ssry-6GaGBqZKgqUPmi0RiyO7p0CxCubxItWEnTvBkdsHH-gkW_QKwMVKAdQgpzvKEhvTd";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_ES512 = "eyJhbGciOiJFUzUxMiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.AOf9jljAFpyLjkEH4I_ND2hl4_06-GNTiIX0u6UAnzuWNEu2K-9l5kh3WhQekYCaaSaNnMEm10I4OIo8ERmD9Tt7AaY5QQ1j0JQ-F96IA"
            + "GSjJxGjK4Y0FPbBEWnkhDaIgfmIUw2qPBgqDvp2FQwkZ6p5UZp73_9XKN-dAjjo4p6jKUO-";

    private static final String NOT_EXPIRED_JWS_TOKEN_ES512 = "eyJhbGciOiJFUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.AOzxutSUBYMb7sNnAToMxhqZs-L1gBoATPFQlgIh6aCox86vE0M21OFZDpBt-G"
            + "nW8AVANFWxgFIHgeoBMcdJjKNuAGjnOtMbVlLZ319Ku5VRoa5Du_awS9W90jwcF0g7Mj5aJeJBjWzGK_bqr6mKCm7MMiiKSuBwULqW_37t7IngTxsV";

    private static final String EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY1OTU3LCJpYXQiOjE3MjQ2NjU5NTcsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.IPhRr7D68LHqWOkK763CLmtdvpDSV_b93GA5aWNHMqI";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.7DxuiKpActXBWdLDuLXs3fdNEa_Uy7omOyPYaAosqR4";

    private static final String NOT_EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

    private static final String EXPIRED_JWS_TOKEN_HS384 = "eyJhbGciOiJIUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY2MTAwLCJpYXQiOjE3MjQ2NjYxMDAsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.psVzCHpTKbLgHcJdoxTEz0U56hBO1BkwC2VG5kTiW5r4FTMD1gNzmTAfjiRjDN84";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_HS384 = "eyJhbGciOiJIUzM4NCJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.AtMuNQc7SRDHzxHGRqQbYnsJXWc2TWl2EXAfgv2OyXFDl0RHVGwAduMcjq0sS-YH";

    private static final String NOT_EXPIRED_JWS_TOKEN_HS384 = "eyJhbGciOiJIUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.rihbRpLs1zmxqo2OdkMQk2NGPsXMiGbORop-o2fls6mMAbjmqDG53W4KwmpflN"
            + "7t";

    private static final String EXPIRED_JWS_TOKEN_HS512 = "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY2MTY3LCJpYXQiOjE3MjQ2NjYxNjcsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.5-uDZKuKrioVYs94yKFuBfNjqlg5ha1dwqWoyIa5io_aePpyVSR1tLkh6eboJBeQ8pR"
            + "6PkJaMsMDEMT6su4BmQ";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_HS512 = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.tHtqhB5bOO6hwF-8WAmY_uBdV1JshJMdj5Tkeizb0fD4NStnTXwopC_ROUEf4vAU1hJ-4bD6TrUiUXMxEULrXg";

    private static final String NOT_EXPIRED_JWS_TOKEN_HS512 = "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.nGVy0kIKFnGrG2lR0RJmqTIEYho12ZbZS4ez4JrCL25l76n6kNMOqoadA-WTP-"
            + "rUYRlEbVVEydt4ieiMBujPsg";

    private static final String EXPIRED_JWS_TOKEN_RS256 = "eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY2MjMwLCJpYXQiOjE3MjQ2NjYyMzAsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.kCsm77eWVSJVr0CMTkMQHLjc_DrvnmF7B4lkDxFNKOpZzqM8s5evKPMu4HcbbELRM9C"
            + "bY2Ftoozcjq-q5PRs2im3_WQbyfx4VGfjA7hvwoz_LngDOZZVojQqmVcw2SSOQDgLU72FgEBVDvLfrhdQer5E0dOubbVctWpUNlgsuIjpbm3y9bbRjYDaDUiQfbDH07To3bG-RjhbFq-v9Pv"
            + "yfrmE1Eb-g88w2e9ODR1GPkL6IF6nI59MjWp9F0tG953YkJ0BuNZQUEv81z7POS51Wo0MjpzSHWbGl4etTr-YOdd1TAezjyXXNAgpql7EUt2fLKf6zH2BNfVC2Hg1DVcZHQ";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_RS256 = "eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.DWzkkS9L0kB4KPyxg0RX5lZKaLAz7ocP0iPx6G__pRI_3rc5WiuaUxQ1uKZeZ7xov4s00FNo86V4_9hMVSbJpJn2zBCXKvRLWv-xPaq7e"
            + "QKmzcOgrwGFZwUyxSzzuXV2X1eP-DNpDhBgfnArutBg7WN9AvaggZwNcoDrQq26DarcPva3wLGdkaqUl7FNbKg42yXxj6aI_6Dfx7YKECPb_yBUoidY-uimmMxTP8LQ-tAt2_DguKD6vevZT"
            + "Hj_9ffYUWvrp2oRmwVDaFOEfY9M1wmy3x1_JAGjz39tXmgwFuQ22Q04ucdQMmv681FytAFKb2RnE-3y9Sz2z4vck-7tTA";

    private static final String NOT_EXPIRED_JWS_TOKEN_RS256 = "eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.fVoOVUPscVG1TH9-BsF__K47jLefRkEIdIWvN-lK0Ymid99zwhcKeKKG93ia6Q"
            + "vQ2n2_g4kPNs-KaFYVTfig_SSNsbBhjumnj9k3jJr_I-0Ll8AVqEyCaDWyq0sMrSz1NCEFmGITHkq9PKurlJz-T5-fih5K6Oni7L0zSsJjA3V1fsTF9hhN9e4wPijdg0G7YShK21WfnymjmS"
            + "_lgrexaUT_9DbWfyJQsNwQJ9GRZiFNP8IVdEZe1NFiu0fnETTSp5JyJ_M37do-pLdtJ81VOsclHF_FRzYM5hzSEU2fm6ZOTgRgoSJiFLCE5xh6QWmoM3NSylIql7InJFDBwVfv_g";

    private static final String EXPIRED_JWS_TOKEN_RS384 = "eyJhbGciOiJSUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY2Mzc0LCJpYXQiOjE3MjQ2NjYzNzQsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.XaBvCe5qNaGj4avUKbzy7t1-opZwrO1uMOGwcvx1L_b5bVMLyvPy-z4RGHdB4w23brV"
            + "HqpSpiLej0a7baoObCC3f73Cuv14wO_U4dxk_4klRlu8hH8fd29bEkfmuh3gdpI2d6vyOk2s6UbIOMFsZ3soTofB41bcoOgIF3hOvL5j9RM3tJlQcyCRv5xIlSmc5lV__A80lMYY0tCXIPOE"
            + "B9rjJDwipVC2_cNd0j6N9KO5dwOR3hatotCx6Yi4hVEAAK-SEfHAAn6z5w3qLO05WYnJc8GJxRLIXKBozAmZw1oZPr32TpmT_xq-Qrv4LG0hLdNxRCZDrC6tEVtCWWiwcvg";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_RS384 = "eyJhbGciOiJSUzM4NCJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.TnpNT9iBctviBvXlkXqv4Rhg_L6K-vmaO4j4Bcf2HeUQ3dqexTfwau6GMdn4PgWeHnziq81SJmp12bO5ObU1Hbt39DkmK7YDKwb2Z8Igv"
            + "RgDQu4LdUOrnlQ7i9mb0lLP6AQTuDly_dkYIjtRxzhSHGJB0FiEMwSHV34BXhcTr3izrDOlCZMotGPPmEbzn6fWWFlDgDhOhNTpHkqV1b4xrbJBj2bYgpXDOlEzv5oaQfZ7ofkC2PDgtRzcI"
            + "SqPG64-2He1vgplYeCZadItbH-8G6eyURj00avZYPFYwmbbQCONgTrvRo8KLsrHJfP79QMaQWAjJlIsfB7jMf1Yd7mISQ";

    private static final String NOT_EXPIRED_JWS_TOKEN_RS384 = "eyJhbGciOiJSUzM4NCJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.WKwlq9WoUqFcmkYDJgsAEXbzxlATXaodZZRvbH5L5gyNME5OfMxH8AGeGUqtzS"
            + "rdZiWIFzgfABpVJ7b_OgrGa8G__jw99JgJlgu7PqjEBxi5hb6wH5kBVlGAa7_YW6gnOfpf7kZkF54mDCGfseXMAw3SJVYPhshWmurcicvY2Xf0kL0HOnWGj74vzt5S2xp1WmgRtRdP-Obmh3"
            + "2zlwG_EvDNgnf7tH5wOqqUTG4qd7LCpzKK7F4ZaW0B8WX1A5O5oxWzamBL7AFUiNElo1j4KvZtZzkvhc6Bx1RQeKWYznnGi8asUEZkxC2zGvI-vdprfyh4aS2W7A_9IoCxH4AM3g";

    private static final String EXPIRED_JWS_TOKEN_RS512 = "eyJhbGciOiJSUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY2NDgwLCJpYXQiOjE3MjQ2NjY0ODAsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.JDS5cNF7Y0fhN3WwZW1JceKeasuRiu-Ah3RUVEEBHjBoFVZbnAce_qDSCu7LUx03QqL"
            + "l7kejF6FzzZX4fEksCjo5ETimusTgmNdN5BgWYjsYspslTVNCMW1T0gfLVxkbUvXf1mVI6m0E7o3rIxsO1mVVAFr1KGfmy9ztUs0iXxONAD6z-8HuKXQx2odUUW-i3veVtoD7EVjh2GhDJYA"
            + "3xY2dwdkxuy1_6C1PO9GxMoIm0UTDWrUkUw2NqWG6Uk7bCsSvlCNnsjmtygHsD0lRRE-48Tt7clQOAEJNdkxqdDGxbfUog3JFHY_sCF3Dne0QSBoignpN6ypVFEUwMBD93A";

    private static final String NOT_EXPIRED_EMPTY_JWS_TOKEN_RS512 = "eyJhbGciOiJSUzUxMiJ9.eyJleHAiOjUwMDAwMDAwMDAsImlhdCI6MTcwMDAwMDAwMCwianRpIjoiOGNiY2NmZTEtO"
            + "GQ4OS00ZjRmLTg0ZjEtMjBmOTBhMjY4OTg2In0.hCL_JNsoQmKxdgzBKuOeZC_Tzpvxlwm7BX9iZB0mn9WJ6rnQx7j9SZOydDg9F2bF3silUESlbqRzEQjZRUdCHUtaUYK9SB6d8m4dBSdhi"
            + "sd5AhN7VKJHUTNrKUEWdT2aEjNfA78bnIogyl5mL4IBe0rUIxaAEGd6wh_lB-se1MMB5Oo27PhFRKXeZnCJ6S-yRDUtoP5XkS-KI01Yre4q0Um4Skrz-0BZXHxIKJzgjUDYv7cXQvblGb-th"
            + "AGVgeomr4GJJN96JBK0E6wiyFTDOcXNcmCBkK5ULnbGCWJ_15OTXvUQ_m8UH7iM5K0BgRNEf58iVfaPc3NoEak2fmUXwg";

    private static final String NOT_EXPIRED_JWS_TOKEN_RS512 = "eyJhbGciOiJSUzUxMiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.Td5lXW2uB-taihifbt-IcyBQvMNQYM5RMqvsJwI0w9633U6_Xynl-JZJ4ZTJfH"
            + "k9fE-16nMUDAGavbpUIn8k6S5rKYG4LouCusAXl6ptVkxbLiQN51ehEnlU2el5_yXKH7wTxzq0OIL7izjm4_5D_6XxBXQ5J580ehwwHlOcqXOhws4Qe-BQxWqHZFw8_zu7Lb6KFQHdaberFq"
            + "byEkhjgAgMheGS0cqNbLjA8KCD0Y9xMDj4KK1pUsfnmGC98AzJCkfwSEZjwJCABqlCTme3t1bwWGwCkH7XsbrJHCk9dN6MrCOA6O-HExDMv7bAEX5gW-2Kq44ehaBF8c_aA69yBw";

}
