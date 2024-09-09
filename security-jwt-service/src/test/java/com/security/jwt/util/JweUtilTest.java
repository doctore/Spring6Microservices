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

    static Stream<Arguments> generateTokenEncryptTestCases() {
        String doesNotCareSecret = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            jwsToken,          encryptionAlgorithm,                    encryptionMethod,                  encryptionSecret,            expectedException
                Arguments.of( null,              null,                                   null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,               null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   null,                        IllegalArgumentException.class ),
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          IllegalArgumentException.class ),
                Arguments.of( "",                null,                                   null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,               null,                              null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   null,                        IllegalArgumentException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          IllegalArgumentException.class ),
                // encryptionMethod and encryptionSecret does not match
                Arguments.of( null,              DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( "",                DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( NOT_JWS_TOKEN,     DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               XC20P_ENCRYPTION_METHOD,           DIR_ENCRYPTION_SECRET_384,   TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               XC20P_ENCRYPTION_METHOD,           DIR_ENCRYPTION_SECRET_512,   TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,    TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,    TokenException.class ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,    TokenException.class ),
                // Not valid JWS
                Arguments.of( NOT_JWS_TOKEN,     DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      TokenInvalidException.class ),
                Arguments.of( NOT_JWS_TOKEN,     RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      TokenInvalidException.class ),
                // DIR valid generated tokens
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   null ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   null ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   null ),
                Arguments.of( VALID_JWS_TOKEN,   DIR_ENCRYPTION_ALGORITHM,               XC20P_ENCRYPTION_METHOD,           DIR_ENCRYPTION_SECRET_256,   null ),
                // ECDH valid generated tokens
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                Arguments.of( VALID_JWS_TOKEN,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    null ),
                // RSA valid generated tokens
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      null ),
                Arguments.of( VALID_JWS_TOKEN,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      null )
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
                //            informationToInclude,   encryptionAlgorithm,                    encryptionMethod,                  encryptionSecret,            signatureAlgorithm,          signatureSecret,          expirationTimeInSeconds,   expectedException
                Arguments.of( null,                   null,                                   null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   "",                       90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   null,                                   null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               null,                              null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   null,                        null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          null,                        null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   "",                          HS256_SIGNATURE_ALGORITHM,   "",                       90,                        IllegalArgumentException.class ),
                // encryptionMethod and encryptionSecret does not match
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               XC20P_ENCRYPTION_METHOD,           DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               XC20P_ENCRYPTION_METHOD,           DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        TokenException.class ),
                Arguments.of( informationToInclude,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                         TokenException.class ),
                Arguments.of( informationToInclude,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                         TokenException.class ),
                Arguments.of( informationToInclude,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   XC20P_ENCRYPTION_METHOD,           ECDH_ENCRYPTION_KEY_PAIR,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                         TokenException.class ),
                // signatureAlgorithm and signatureSecret does not match
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,        90,                        TokenException.class ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   doesNotCareSecret,           HS256_SIGNATURE_ALGORITHM,   doesNotCareSecret,        90,                        TokenException.class ),
                // DIR valid generated tokens
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   DIR_ENCRYPTION_ALGORITHM,               A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A128CBC_HS256_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A192CBC_HS384_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   DIR_ENCRYPTION_ALGORITHM,               A256CBC_HS512_ENCRYPTION_METHOD,   DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                // ECDH valid generated tokens
                Arguments.of( null,                   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A128CBC_HS256_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A192CBC_HS384_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM,   A256CBC_HS512_ENCRYPTION_METHOD,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                // RSA valid generated tokens
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( null,                   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_256_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_384_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A128CBC_HS256_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A192CBC_HS384_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      A256CBC_HS512_ENCRYPTION_METHOD,   RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RSA_OAEP_512_ENCRYPTION_ALGORITHM,      XC20P_ENCRYPTION_METHOD,           RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_ALGORITHM,   HS256_SIGNATURE_SECRET,   90,                        null )
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
                //            jweToken,                                                     encryptionSecret,            signatureSecret,                expectedException,                expectedResult
                Arguments.of( null,                                                         null,                        null,                           IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                // Expired ECDH
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                // Valid ECDH
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,                             expectedResultNotEmptyToken )
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
                //            jweToken,                                                     encryptionSecret,            signatureSecret,                expectedResult
                Arguments.of( null,                                                         null,                        null,                           expectedResultEmptyToken ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( "",                                                           null,                        null,                           expectedResultEmptyToken ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               expectedResultEmptyToken ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           expectedResultEmptySignatureSecret ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               expectedResultEmptyEncryptionSecret ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             expectedResultEmptySignatureSecret ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultInvalidToken ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultInvalidToken ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               expectedResultTokenException ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               expectedResultTokenException ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   expectedResultInvalidToken ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                // Expired ECDH
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultExpiredToken ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                // Valid ECDH
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         expectedResultValidNotEmptyToken )
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
                //            jweToken,                                                     encryptionSecret,            signatureSecret,                keysToInclude,     expectedException,                expectedResult
                Arguments.of( null,                                                         null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               keysToInclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             keysToInclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               keysToInclude,     TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   keysToInclude,     TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                // Expired ECDH
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                // Valid ECDH
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             new HashMap<>() ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToInclude,     null,                             expectedResult )
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
                //            jweToken,                                                     encryptionSecret,            signatureSecret,                keysToExclude,     expectedException,                expectedResult
                Arguments.of( null,                                                         null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( null,                                                         doesNotCareValue,            doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               new HashSet<>(),   IllegalArgumentException.class,   null ),
                Arguments.of( "",                                                           doesNotCareValue,            doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          null,                           keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             null,                        "",                             keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             "",                          doesNotCareValue,               keysToExclude,     IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             null,              IllegalArgumentException.class,   null ),
                Arguments.of( doesNotCareValue,                                             doesNotCareValue,            "",                             keysToExclude,     IllegalArgumentException.class,   null ),
                // Not valid tokens
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( notValidtoken,                                                doesNotCareValue,            doesNotCareValue,               keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                doesNotCareValue,            doesNotCareValue,               keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_JWE_TOKEN,                                                ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     TokenInvalidException.class,      null ),
                // Token and encryptionSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_384,   doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_512,   doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               null,              TokenException.class,             null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         RS_ENCRYPTION_KEY_PAIR,      doesNotCareValue,               keysToExclude,     TokenException.class,             null ),
                // Token and signatureSecret does not match
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   null,              TokenInvalidException.class,      null ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_WRONG_SIGNATURE_SECRET,   keysToExclude,     TokenInvalidException.class,      null ),
                // Expired DIR
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                // Expired ECDH
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                // Expired RSA
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              TokenExpiredException.class,      null ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     TokenExpiredException.class,      null ),
                // Valid DIR
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     DIR_ENCRYPTION_SECRET_384,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     DIR_ENCRYPTION_SECRET_512,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             DIR_ENCRYPTION_SECRET_256,   HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                // Valid ECDH
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         ECDH_ENCRYPTION_KEY_PAIR,    HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                // Valid RSA
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultEmptyToken ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         null,              null,                             expectedResultNotEmptyTokenWithoutKeysToExclude ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    RS_ENCRYPTION_KEY_PAIR,      HS256_SIGNATURE_SECRET,         keysToExclude,     null,                             expectedResultNotEmptyTokenWithKeysToExclude )
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
                //            jwsToken,                                                     expectedResult
                Arguments.of( null,                                                         false ),
                Arguments.of( "",                                                           false ),
                Arguments.of( "NotValidToken",                                              false ),
                Arguments.of( NOT_JWE_TOKEN,                                                false ),
                // Expired DIR tokens
                Arguments.of( EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                         true ),
                // Expired ECDH tokens
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW,                            true ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW,                            true ),
                Arguments.of( EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW,                            true ),
                // Expired RSA tokens
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_256,                               true ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_384,                               true ),
                Arguments.of( EXPIRED_JWE_TOKEN_RSA_OAEP_512,                               true ),
                // Not expired empty DIR tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_DIR__A128CBC_HS256,               true ),
                // Not expired empty ECDH tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,   true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,   true ),
                // Not expired empty RSA tokens
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,      true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,      true ),
                Arguments.of( NOT_EXPIRED_EMPTY_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,      true ),
                // Not expired and not empty DIR tokens
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,                     true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A192CBC_HS384,                     true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__A256CBC_HS512,                     true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_DIR__XC20P,                             true ),
                // Not expired and not empty ECDH tokens
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384,         true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512,         true ),
                // Not expired and not empty RSA tokens
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A128CBC_HS256,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A192CBC_HS384,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__A256CBC_HS512,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P,                    true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A128CBC_HS256,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A192CBC_HS384,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__A256CBC_HS512,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A128CBC_HS256,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A192CBC_HS384,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__A256CBC_HS512,            true ),
                Arguments.of( NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P,                    true )
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

    private static final TokenEncryptionAlgorithm ECDH_1PU_A128KW_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.ECDH_1PU_A128KW;

    private static final TokenEncryptionAlgorithm ECDH_1PU_A192KW_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.ECDH_1PU_A192KW;

    private static final TokenEncryptionAlgorithm ECDH_1PU_A256KW_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.ECDH_1PU_A256KW;

    private static final TokenEncryptionAlgorithm RSA_OAEP_256_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_256;

    private static final TokenEncryptionAlgorithm RSA_OAEP_384_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_384;

    private static final TokenEncryptionAlgorithm RSA_OAEP_512_ENCRYPTION_ALGORITHM = TokenEncryptionAlgorithm.RSA_OAEP_512;

    private static final TokenEncryptionMethod A128CBC_HS256_ENCRYPTION_METHOD = TokenEncryptionMethod.A128CBC_HS256;

    private static final TokenEncryptionMethod A192CBC_HS384_ENCRYPTION_METHOD = TokenEncryptionMethod.A192CBC_HS384;

    private static final TokenEncryptionMethod A256CBC_HS512_ENCRYPTION_METHOD = TokenEncryptionMethod.A256CBC_HS512;

    private static final TokenEncryptionMethod XC20P_ENCRYPTION_METHOD = TokenEncryptionMethod.XC20P;

    private static final TokenSignatureAlgorithm HS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS256;

    private static final String HS256_SIGNATURE_SECRET = "hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k";

    private static final String HS256_WRONG_SIGNATURE_SECRET = "111111111111111111111111111111111111111111111111111111111111111";

    private static final String DIR_ENCRYPTION_SECRET_256 = "dirEncryptionSecret##9991a2(jwe)";

    private static final String DIR_ENCRYPTION_SECRET_384 = "dirEncryptionSecret##9991a2(jwe)$53232Rt_G3rew90";

    private static final String DIR_ENCRYPTION_SECRET_512 = "dirEncryptionSecret##9991a2(jwe)$53232Rt_G3rew9016310k_21iusN271";

    private static final String ECDH_ENCRYPTION_KEY_PAIR =
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

    private static final String RS_ENCRYPTION_KEY_PAIR =
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

    // Encrypted with DIR_ENCRYPTION_SECRET_256
    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__XC20P = "eyJjdHkiOiJKV1QiLCJlbmMiOiJYQzIwUCIsImFsZyI6ImRpciJ9..RnLAVIYW3vIoO2hASZxb1xvYfwUzQkHQ.3BfZ"
            + "t-S-iYgMuk1i-SLe_3-QKDEBhoFJsRFp1XP4eh1oruieaBMh5EzqIg8wal1Ak8Zu-ErJ7ih3dYOTO2fP-EOI6r8li5O1D8fTpVrrFKwo09o4U4wCKLrQ_mwRBJQiWUWzp6oXGjla868r-EmG"
            + "j3vYX0YRuQvgknoBPfxcAgDfjO4JWs_gFhMegDGc8mwioj7hsedCQ-LlQxV57ZMyC8adjT2oHPL-NcIquIWDXCDcvRpFnidw6Sd63B7QDTUPw0bBKyvdyZLvGilkMex1DH1HkfwNStpOvfBB"
            + "vXE.aYxrfcVrhIVDR_qxgA43YQ";

    private static final String EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJfbDFFMkZDMjZ0STlDQUpsMFVoV0xNZGtVdVRRTG"
            + "JrVXMwTHZOMTVCamp3IiwieSI6IktXNzVhSTRhWEhwTU9vQThkV3dIaEd2UlBoOW5KSy1YWnFWVmlvSkIybEUifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1IUzI1NiIsImFsZyI6Ik"
            + "VDREgtMVBVK0ExMjhLVyJ9.uAbquuDTn6PciRH95pNc3_dByJdtVYd4U6HUc9RMddHQBvcGF7aP4A.iGImEYVRPzB9ifyR1AmYEg.ap9XnC1u5tHM6CqsfgWo6Rh6I-6p-hIolp_-bDwdnCY"
            + "WjWTQvGgZd_NrYggD3H5Rml3SQPKvoWBQlYvFpmaM8ub3sVIiYwtv_VIfE8nh1tSOQKMkqhY9cndC7lpl1wCDF7C2mcdSHn_6_JD9OkpNYE2KCsc_sRX0LgQMtKU2ZPb4_Fywy-Wug_h_DCt"
            + "XQi7UhiBbId8EZrVrdVY547sRJN6py2c2DbS4_KNyNZFJNOaKkpKTyF6tWMXIvBuVm28pVXsz_Dnh8-RUCZBXJgYpBcvaIGjtqoBccA-sY52g26M.mfXYcchtGgmWu8huNIiaow";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJLbDdvbi1pc0R4a"
            + "FF6SGU4YTVjc3U4YzNNV3RUOGVmV3RLb29yZTZuR0xFIiwieSI6IkVoa2c3Q0lueWtzejNEQ2NtWEFQblVlTFczNDBjWUVSYnk4OTRkSDlHaWsifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyO"
            + "ENCQy1IUzI1NiIsImFsZyI6IkVDREgtMVBVK0ExMjhLVyJ9.55r9Fi78a0B0enjuVVcV2-fXaLjZ8HPEwpt-tsPF4VhFDv24XghvZw.jsCJfozsMIDFvG9dIMJgQQ.3bnCqfzEw0VkWIZ-Xz"
            + "HZbnm3n0L7zvtciEU53o7Y6QXDRS0jiz9ubf3JtHLbp2-z7UjaItueFvtmMk2R298dNxo13X6uqf36pLzmRHGOU6s0nS4vMWmXFQHUSNJZ0jU33Y7b9v8GWY29LwtPwsTkacmXNaL43yRotb"
            + "b0czMtf75DRjtCHW19TPYF3m2hNqFtjXMQcee4hFtg-Fdp06ty0Axh6x1nydeaT5KkNwiTodw.mhSeMgcarMqQEowY9xKIqw";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJYVHRiMm5HLVA2RWdTU2J"
            + "TQkVZWEZjcDhWWEVEMUxqMHJtVWgyUFVTSTdNIiwieSI6Imt1eWFGQkhVY2p0QmhKcThxMm8xYl9ES0tiM0NxQnlmRml6RVYwZmRLR0UifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1"
            + "IUzI1NiIsImFsZyI6IkVDREgtMVBVK0ExMjhLVyJ9.60KvQJB5zgLag-fsRKmCcDM1Q5Sm1IqXuUMtzdBqstgu9viy1zC0dA.OremiRDdNqXp7U9A7yFn9A.VaJ4AUHc9PanDAWNy6qenY-z"
            + "sLyvhailiSy-fRrZL3apZbQOB_xTeHzfa1f1Z7Azw9eqhGm4EmBZdb-Rt9Ewd3DHqC_DFfp14iNQu6P4y6tD_et-6V1RXkI12s1f3zqqIg3pQ-AGP6vW9cJhCLawsHmIx__njV80cqzJBkWS"
            + "8OUNt6I1JUDmlvBPioje8OJYrBrjEedYkwCKMeqodQMQ3ldDtCA7PkICfOyoGpciYGxo0gDFSqfimypccBep9XMzcWrmMFvx-nQZNMKJIEZH6S3R-f39-cDLpEEvzhSrbWA.JW0ElApgdo0A"
            + "Ryj2P6dwRg";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A192CBC_HS384 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJwcDkwLWF6c3kxS0IzX0x"
            + "BM1AzYlFudUdpYmJRNF9qdDUzdGJVcUlhM2ZnIiwieSI6Ilp5OGZyQzU1bVlmV1c2ZDFLOTQ0Wk9JcmNLQkF4ZWltUkNLZHp1WUNuODQifSwiY3R5IjoiSldUIiwiZW5jIjoiQTE5MkNCQy1"
            + "IUzM4NCIsImFsZyI6IkVDREgtMVBVK0ExMjhLVyJ9.6ej6XeT5vUbdjBoT0Zv7HY9F3koSU8BlirgBrpHlBbrsniLQEZx7BFLo3qJ-ooKQYJJs8LxErlw.mtF9N5rZSHbGNX0WQOuoyg.yp7"
            + "x2Px4fAnIXtq21Nw00Lckdoh1H71XUfxSp7sBmz5A8KeyeQA-NfcA2hlKTeeXAneew_hoVs4iq4eNROnGpmktq_6mYrF8Ew1viccqA1CRDXmYwmGadOWZXYFwq35XvIgfHDGVgr2_upoLmCJ"
            + "OeUOn7eCwg2YLxXzWY_T4UqyT5uP0Y8VahQ_Crkm0U1toAUT1jVpbAIVaEm_D1_UOND45iCIJAiVjO0pZA1ylUamd5CkkNg9rqp3odRzt275AwTfMacrXmOJgkbHV6gDZNQwg1o65BrVh4lE"
            + "r0Tajq4E.Vg5xYT-yOwKb0MGAiY0G9NufxVNcR89R";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A128KW__A256CBC_HS512 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJzMm53R0Q2d0xDRzVwTGp"
            + "WWkR4N0ktVmpqM0l6NmNXYUs1dXV0enlKSFNFIiwieSI6IkdyUldQemNnaFV2RnlZN0dTSGJuRWR5WkFkWXFNV01WLTVsWmNJQ1ZBZ28ifSwiY3R5IjoiSldUIiwiZW5jIjoiQTI1NkNCQy1"
            + "IUzUxMiIsImFsZyI6IkVDREgtMVBVK0ExMjhLVyJ9.-H-QYQVuj3Qa96SdJ4LK_w_ZXJ15tiXMTjazSdMKGaC_UQUx88MZVXAL8Q0lJcC612pqL_AFKoPoOtZvC2xXfIXQNZhqEV9y.JzluC"
            + "g7ouNuD_Ib5CnghYQ.21ZucRMmMC_yM4s_DXtflXY3qo7BhRmcHLAdmH0GMu3ofbuVjvinAbUfV3MiBdZ13FmH2meW7QWRHBtrp8Ooy-xnti820GtS3E0zB3bUteMaJq35pEq10wr7gBtyJc"
            + "enUTx_A8WJndtJLKe1JTn31nzKUTVaLZlUe6xAYdKf8BZ8k-q9ApGCPLVDmKP07ggUiePcnZ0ImpteG4WP0Po4q8MGDbzPp8GAS3LVVwBa5D9Tl3rgmuq_mmbwsdug7gSaXg3X4pAGJXdhY-"
            + "1TwcbDoMjBFgTmkdJHtSnZb7eqiBE.ctoVnFE-C4zpbcbCZ0vspxm53XTB9li3IOprwQIybd4";

    private static final String EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJpYWkxRUdzeVNYcmtjanRFMGR0bnJ5ODlWOThEZkNBeXRRYVJuVDQzTHZ3IiwieSI6IjBuR1I3MGdhMUFiS1RfYTM2YzB3VGlUOHBaVGpBUFVsbDJsZW56Y2lwSk0ifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1IUzI1NiIsImFsZyI6IkVDREgtMVBVK0ExOTJLVyJ9.NuBI3Ml7veUxYmGhUhBCWehmbYkk7kGpSDqo_ZVCMCiXkEXiViFXrQ.kgUYAorzd1IkP8tyF8DxHA.-ErHx7m7YmbBIICR0w0ZFrwYMxfn022eZGcQmNBGpHJQ1n9pjOExyPEG8zuktXhUYpNeBdS9pTHIcwQPwGf7U9aCTN9QParO2s5acTh1_MmJbWk4vaX4s13C7Y1rLuJKj2wu9aOV2it7s1Q1-4dDZlO-BonK_433W1mY3u2JMxjMZkiG_qCotwBuIo6VL7XAjwnZq-9BSoydw_CoVrTxpGWrnerrBmXO59b_Y7zJdGg-S9kACI8NjsX_FQoWC78Rt-mfBP8jP45TQuiTzy2ZMkmWdMJ7PC4ukbEVIVsI0NI._Tpq8z1Zmb9QtH7Rcak8Cw";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJtMXlVSnVRY1haZ"
            + "nk3b1dLejc1cUpwaWlCUURtSjc4WVhJVmV6NDlaVmlvIiwieSI6IkdJWFBpWWw1bWNmdm0tNnN4c0ExZ2hRaVk0SDBNSHNEaE9nWm5INlFrT3MifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyO"
            + "ENCQy1IUzI1NiIsImFsZyI6IkVDREgtMVBVK0ExOTJLVyJ9.DOQsrlCBSIXEb0O9rDrpbq9ob3qaUmyFl7bqH15D7GF4r5WWFsoHvg.tC2DFibO5Pw-h0P3QIiBtw.nG9-VG6MsYaJUsoTfL"
            + "a5s7F-q5K_n86UsZG9iFCZJe0B7BUOy-kvr_e3pjSq8_UKbC2PAwUTyuV0o49H3_wEESzaHViXJAsuKQjDC5GMB1H6xI_-f0H6gul_vLDMelcGga1Yje-a1nrTFb0IoMMsX47SA2TEA0PjaE"
            + "r9dYn5kH87elQMDRDh2t_RMg1V0s4hqmlKZmHY8pxFlwJc-Q5iR3qLUtnjy7-ytxKGRZ5q2wE.LgvlxL1D93yYsAtLDRuQsg";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJwQlBweXNOWEpVbVZUM1d"
            + "3QmhsZ0ZCVURnZkpEMFFwV2JIZEhWS3g5X2pzIiwieSI6IjNRbndmbmFWZjlZeWpDS05SMnYtYnBsa0k3UWFyemZQQjE0VVE3QUY3d1EifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1"
            + "IUzI1NiIsImFsZyI6IkVDREgtMVBVK0ExOTJLVyJ9.tcrTfPmWapKPRrbN7uwxPi3T_pw6YlNlGdAb_JwZzpGpuVldqYcr6A.vrmQ5BHa4IWmS9jh0V0xlQ.sZwe4OwQxvcfJZdLJul2CGCD"
            + "Hlppb6rcuI6QWA7Y7HYZapUuCcJVQkSqnAeYe0jr_wuqm6qsvdaUogD08i-pxoV9WtEdZPQO4J3bDIQgma-UMYhvnuPWRQmkgHcnbSzu1Dw71rFCuvulcS_GBziLcv8yrGoqOQ2HcMDvZKHl"
            + "GVsMMPB7A9-3Waoe-0n7hSYcbEZXEiUICcg_3lAPPcotsSkpdyIjnJMxaroyTQKEjXywZBwFakPwno-II0QVdvqO9p9263Q8-fN7daBUXxBKfOUqxJEsaRgYvPsBFrHvf40.z8OcFJiWy7sY"
            + "5ZkUOGKFvg";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A192CBC_HS384 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJtM3M0Z1UycU5pbDhURTN"
            + "uSlJPd0M0cjl2UlZzMG5yRzBHOXJ4Ti05T0lFIiwieSI6ImNtMHY4ZXhtWXB4MmhrczZMN1B4MjZJTWtmV3h6SkVDRTN6WHV3WjFJTGcifSwiY3R5IjoiSldUIiwiZW5jIjoiQTE5MkNCQy1"
            + "IUzM4NCIsImFsZyI6IkVDREgtMVBVK0ExOTJLVyJ9.6mfe9-NcxZu_qVh48ur1Uk276zU5hF1ugmjR-0s6Zs2rusRdY_cMifI6SJkgW5nQCQbXQaij2Gg.tAyV_dRHkyAaRfCS3qPCIA.Xik"
            + "0Vceo7DPU3KJ-Tyby5qgb5vUv5yfhcrQJBu_VdAvzdUt01rOwunSifZf3SaD86ud6S0FbgM0MoskdA83riha15UHZA2ImJOp6LM5H_e6P5J6WJ-jCEwSoIzSuXPsLelIYeQ3jCWiVmvuAHZm"
            + "UzNKMRy2lxNTyjGLP31Mix69hPbP29p7641_a-HMDeUnTkCeN-YlkFB6T3VVaB-pgirjo55B7BiphmN6mM6mrjHyI5Huet5MRbj0jN6JsyNaMlJC178Oj8ZYWG-yJ9jSPZ1t-gpsIPMABXCi"
            + "4Fsw16So.gEp15AQGFzfHhsEIe8X6MxW9xvNd-J_J";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A192KW__A256CBC_HS512 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJsa3V2dFNwd2dGUXZZLVl"
            + "0R1htdWFaY01EaFZfUEZGSUNwZlQ1NkMxdWpRIiwieSI6Il9HMEMxM2ZtQmozQVRhdk94NWlYSjFxSlg0a1ZUZWFqWl9Tb3BKb1ZnRjQifSwiY3R5IjoiSldUIiwiZW5jIjoiQTI1NkNCQy1"
            + "IUzUxMiIsImFsZyI6IkVDREgtMVBVK0ExOTJLVyJ9.PZNUsKKpo7Mn-9VemSL_Xsvm1kXsp3O-touLQeWB16BBtu8sLMiyfloqAMogub9LPhk7qrxG8rBjc_gd31jOZwo8TKrbTBTL.kpqIr"
            + "X5L5FtDMmrgKZ0T0Q.uo90NuhTWmigo3BT5_arRJJAtWUv7Aztu1u8upgkc3kLyAsqPXsZ1RVMRg2mo5gj_XpKpChBQ366j_kWs0uOpULciUyek6Zv10PLTDJbthupYQP05OlfJBXh-Bnq5d"
            + "dl-urAkSI_1BsFZmqC8OJoKWqqK0sjXmCQwLJqakfjiGHngmzELHt_dQ7sNpVKjS6dAypKdtMSeRx8wqNKcRBGAHVhhiVd5KccKeqCbYgXhh5MXsBOa9eJPeIVhxbCnGcW_3DMmAgz4CjE9s"
            + "3lauOr3in983HEECJCm_-ifF3N2_Y.oWKdLUFFzYbwpP6thR1oZYBGRbcT0txBFc11wXDLzII";

    private static final String EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJBOU5vbTJzckNFbzZ6VWJ4c1g4Slp4M1lseEtDYjNFYm5JUTVpeGdxWURzIiwieSI6IkJBRGVMeHAxRm96OGU0UG1sOE1oSlJxQ1poWXozZDl2akFiU3c0SV9qZjQifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1IUzI1NiIsImFsZyI6IkVDREgtMVBVK0EyNTZLVyJ9.rRyG9Hfu8SPfsf62xzBEH7XXVvRBpuj9U1kmCuzhR2NtXoIcfngtQg.o-W0tHdVdwJ4suM03Quu8Q.ukGa_dCBRKkOICUOOsIx4YkninhciVmLOumOJL2ermTtreMN7tL09UUUx2l_3AZfcs-bl_ihbVG2eMQsKCTkmrQzq8a8gl4hFHr6az_f4TdWdB36dp6AdzHyCXjbdFeb8m8kkeFtJYh57yKCyYChfmUkkzsWtxV9AybT2vO9Werx-BVYjDHPa0YluRwyI-zS9-Z7-stbYjK9iuKcDiuKobAZtyzZ7rqvSVhnT_3VVNipIG3p8kDGnieblllYlPsuPzp0rKNlZXu6naAUmXSfyTSKx2T7GC_5rQcxvYbYB8U.5y7KEhIxLnmfzYx-90d5Ug";

    private static final String NOT_EXPIRED_EMPTY_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJod1BtVzJCUEtiS"
            + "GFVbE5jbkFwcFZpSWtqb2pob1BRRmhLa1dBQ3JHQ1A0IiwieSI6ImJFQnRGYnRHUGtiMTBBaFNDRFBNSE9mSm5LTU1Lcjg3VUdzWVVKTWdtZDgifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyO"
            + "ENCQy1IUzI1NiIsImFsZyI6IkVDREgtMVBVK0EyNTZLVyJ9.9i0DwPEOp-7372SJfAJniKq8KwuwK4UpR_CCOzD64v-2KAdBpQxH2w.GWbu4zIc5OFIHKjjKbaVag.ER8kHWqlFNGtWYEEW7"
            + "ZJFECkj7Tt0N6P_5zi6RkRB7wowkQMloh5VvppFmlBTXxiSuCD6EjcTwIHuXFd0_5fYYXkj4aZPkDiIwBDSY_2T5YtaFQHdnJc7IiQ59XYyZ9OHhHdIgj4KGSwzTDWLBy_CwcNEMOVTb9vIC"
            + "F4u6WoiBDZ7Z5HRq85gFXS8ZcskA1GvFttzD1Yrb7ApiEniSlw77TOMKusebL_wE89_Qx8Aqk.lpzxsMhvIyfhhbI6X3RYhA";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A128CBC_HS256 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJENmVqV1prN1NadXFRaTB"
            + "ScWt2bDl5ZzREdGxxejVuRDdhUnJzRm94djBZIiwieSI6IjI4VGJub3FtMWRRaFhjWG1CVUYzNS1MYTVIQmh6WFhEMGlnZkZLWHlOU0UifSwiY3R5IjoiSldUIiwiZW5jIjoiQTEyOENCQy1"
            + "IUzI1NiIsImFsZyI6IkVDREgtMVBVK0EyNTZLVyJ9.jMz5hn_nzF0zMRvOOFrKd-QxuzsAe5PfaWiyS7-Wzzh2Tr4dnEpHZg.RT_GeGgxXrPOGCNdiG1YOA.ibxqIUJ1HeitIFdYmlTuUk_9"
            + "n_IUuS-lMKaFeuZczfhY3CN_GFauBOwG4ZOlesE6GONjjcjyQHs3CbAQxoBxOrA-lv13BC5b353JcnmMl6K6BDnG87bHEiVa_kHiOKbgeE0I1Ilpgn0_PeR5DCr7DH1svHnMlSKe_5cZfOhm"
            + "JDvsVX_1-TM4NiB-lSk8K8y5SMJHwWxLMDsEFuoWq33VGiq_khAXKKPSfe159jGI39YvJuypbiS2gdSjt2kIa2IQ_4BJOLwnti-rUhWKHL6ti7zaSsmdAdJiD-aAjLbhNcg.IPDGScsGi5jx"
            + "TjnhTjisKg";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A192CBC_HS384 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJPdnhNbUxoV0lUeUxTMjQ"
            + "yRWFYQkVOaDNqcExBcUZYU0tKaEpPQlVESmk4IiwieSI6IkJLZmZWT002eUpDeDZZbUlvNzhabmlyN0syWHFEYmttTG9CQjE4emJzYU0ifSwiY3R5IjoiSldUIiwiZW5jIjoiQTE5MkNCQy1"
            + "IUzM4NCIsImFsZyI6IkVDREgtMVBVK0EyNTZLVyJ9.CsX967DZ5MhLW58T_lwQ2YKIhqxarzkgne9wDxftvff922W3FC7xNzYwnXqkJwmE_rfRRq5IwJA.JKzMMxijX5rSIgyArO4Mug.gaD"
            + "ihUcBx9Qz-W1rcYVj6-16RdtQRfWEXzbG7e4fqGZuf3-cjhU6r9Y9PLMW1e-rYC_mm5tlA8UVf5-XYy0qcWHNlDQSkiyK349cv6MxDGBZaOZMPNl62eCD66Y7Q-7MRnZ6QuD4JJKJkjxVhTy"
            + "wOtGHcyf4eEesSKsNz7QNknt1DFfM7Pnm43YExmg1ZKVqWGqEaG12zmmNnHD31mS4bMD3EyuXpdGdvZQH4Px1t5EvmKneFdQ6vgMTxBmXrzOkTbLvwt6sf8PeAd7uL9sJbN0IIK-D79nZ68C"
            + "D2mADdck.kbGm2XniOHrHJOEjWAi6eLX0s7-awAtW";

    private static final String NOT_EXPIRED_JWE_TOKEN_ECDH_1PU_A256KW__A256CBC_HS512 = "eyJlcGsiOnsia3R5IjoiRUMiLCJjcnYiOiJQLTI1NiIsIngiOiJIcTZZZDBRZGh5bUFOVXZ"
            + "RWmh1LXJUOTVkTFZFbUpMQWtZNEg4RXZXRHRNIiwieSI6ImhBVkRkNGtBX2pzWDkzeDZxUWNDb0MybVdBaW1XTWpQeFVoTHRWMVlXbWsifSwiY3R5IjoiSldUIiwiZW5jIjoiQTI1NkNCQy1"
            + "IUzUxMiIsImFsZyI6IkVDREgtMVBVK0EyNTZLVyJ9.JXVzoylcTRUYR6EfR3BzP-YUzSQsJepEZprxaZNk7LJ4rpiriY5Uc964bpM8qNPFNa5lLLX5syqYbKwEin7-s_gTTZw45vYG.bsbOt"
            + "o2r8AWn3xblgpMOng.0MDANitKcrN4js76XyKAjRvHQpayxA3xxQwV0wGJhVMQXNd7NZXYGVtzCGwpVuGQ7dXvcmHQ03rhTXDgUOIvB2G-m4h1B4BktPFnPmnXVHA2He1meM6136zrdcgUd7"
            + "jCNFffyGsU9JAtDuieVXCvGIY_fA5kBIKRLKgj3gbBMk50_3_PP1ifPywElt1hl9B9RXxHhdwzKeObxRfSYaiLiIfthpTQZ2_KI6FZz5iYuvrnktfKqltTVI4WAz-qTUJgUSIZWFVtQuLRhh"
            + "cULC7proljjVw1kgZTb2I2rKXjhDY.6GDELcLPvmOJGdYCZulyGSZe_cvtFnoQbNNy875PkbQ";

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

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_256__XC20P = "eyJjdHkiOiJKV1QiLCJlbmMiOiJYQzIwUCIsImFsZyI6IlJTQS1PQUVQLTI1NiJ9.BpD4PlNkENkm7zOvJ"
            + "bsduu2iTbNDjZJUU7cZgFDGkng5gO0FyNyYPbwDVmTGGOTgxEbnFuaUTUFE-W_3ZBsndyyop8SSXBM6N-rWgdBbOxrSu5hGm70noe2wnVfT-n-RxdkyGE_jq0bo8mN8hHSPQawwdK80D5WMC"
            + "TnMWcJ3M8h6G1ssxR_AbyNZxGvO-qxbGwHEOHho1ARvKNAxY-GbT6qHa1sANZQrl8nCFahrzcmPO40Nq1iPZau6jSl5rwgycByRYYJDu7TpZU1qZfrpxJd3wGrH4Y_h0buZDlEBcqJ_tNE2O"
            + "JaYhBb6h_jBDZmgoQQkMR7C7EwYelURspefsg.-ptlHRWtNzLOYUZoBOHL8ocr70V2npE2.y0Dy2jA-IuNz9aFh15vm1mTjoXfEhfEDWlyQBrYAkUXloz8KKu76YUfs-3ZsVkkZsUaAP2chV"
            + "D69PUiya9HKhrMBEZVMZcb51o2ZVbv8iQTKOJA-B1DZheZmcbbrAY-zMSARBoWbJw6l3m3yG1ypPkonBfXyfVlroJgFeOh4Bz8UT5uyyLp0jMxIcHQkbW6k5rRuEQuJUF42HAK96aRRSy06J"
            + "FyCmA78_DJId3C6_AvHYvgVwDGC2DTX5LNHnk8auBjK3WHVFbpWUnuXbwRFsnSYFX2mLLf0DMuz0tE.OE_CbPMQSQreFNH_b5MxgA";

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

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_384__XC20P = "eyJjdHkiOiJKV1QiLCJlbmMiOiJYQzIwUCIsImFsZyI6IlJTQS1PQUVQLTM4NCJ9.Ycf3Mkk6YdnjF6OYd"
            + "7pjxzOOU2i3MnsO08LpnuqF5GnsqDVbQxXuXkPA3DTAS4VjOctvTeXqDTg6oPxxnjyvd_ISRpyLTvOCH-XfB84Iq828mfgBQjbEEewhsPvp-BN_OGNEFwDt5Q9WhpnXscjEup1eMz7Qe3yCQ"
            + "WIPl3d0Zs2BS_lBHMdA1D1QA2G_slMyLgZIXKB7ZAaTAUTXZ7jK5JCmy5Vf5IkuqfiOyUWrnMMOXMFQKfbI2PFftZEOuxypwnk_8-ybc9HUS1_WwFepfpmqjDtk51Qw5tdhJYIpzOgOV4CAq"
            + "zFfFPfiEIizMx48bxLPkKHLf5yjebB6VgEV2Q.mnbfsW_DHLzMvnqFeTg9WOhcaAuWVX0a.zoWo96wXCf3RCUEJxXIUdMFP-GIluenLO35ENfipRVo360BkCJcGxW279yFVIw8Kq2txjzYmY"
            + "D7QXpuCxNUFTMEn1OO8gAekKv3Buy0-4UBFypr019g_xHOjpUEb6h5DG8PuOBCbR1amlQzTsWB2Oip-bAG70w_q25LR3Ibc6zOBCIGNOObS9ndunNG8Wn1hmqw6DHbqoGdnD8dyJ2G85AC-U"
            + "3GVvlfZ7_d7Mm-QMKoURO_lHGkffaguT1tLzDXWNiLyKznsxgNjA_FIqt2rFmRopbZAY6kXYe0E8c8.co9Tbu6byuW51a2IGxX02w";

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

    private static final String NOT_EXPIRED_JWE_TOKEN_RSA_OAEP_512__XC20P = "eyJjdHkiOiJKV1QiLCJlbmMiOiJYQzIwUCIsImFsZyI6IlJTQS1PQUVQLTUxMiJ9.S2psFImaZ4ro1KAsc"
            + "0kv8CxjCZYtHU2yy4PVIColMs66gxgRVBwYc3Fu5STvx9vFyFrrF5PmOF9kfhYWjN5W0Hp3TSsFWXB4NE1KcbbqlACRIZVt9IW8M2n1tOgO963LFQdeWmQfMkqwcNLXyVoNQHHkklIm9vu6A"
            + "Q1VMD3b7ZSJ_fLZ9KHFn79v43S3FzBYYtbcU7t_nZ3oBkPO-NFqtM-ZjBMifE2Puus8y7bY-a5CCd5e7pnvjUoNG6TWgTyFdlawBqk7E2pleIPgK5d0UIVlMtNPIonvyaKHsYaSxtv6OeBMI"
            + "XWSctFr5AdImw8DS0_zxMfDj8zKliEB5yUX5g.Mpc5VorJGynp0iSJaDt-CQE-Q3tD_ixN.CyYza9FTT2y7neVfodIt-F_gGnumNflboqhed2cgWwxe_4xcqmjEPZ4sA_Psm-f4QAxNiOtJ3"
            + "4Jimkl_AUydho8mrCXnn-WnGUgAZ72oQ58nFcGs-CCkI7LYoPxyb8xmyPM9bmEyHkNwijHAcJQdzvTe1YJHUBDERt18oIi7H7aNxP5NdCX5L02WCUM6myYQkx-Sfp_NdTVr3VIiouCFs_vYd"
            + "eCSriqIXdzMG_ksJY4QqIfOqXESz5TITbZNBA2hZZrDINlv-twRAtZG0SdND1hzwvHxdJ2C3ax_GMo.sMbcD2zXdf05R_32fLomrg";

}
