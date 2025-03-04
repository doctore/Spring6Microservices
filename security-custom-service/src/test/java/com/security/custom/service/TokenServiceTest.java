package com.security.custom.service;

import com.security.custom.configuration.security.EncryptionConfiguration;
import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.model.ApplicationClientDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWS;
import static com.security.custom.TestDataFactory.buildRawAuthenticationInformationDto;
import static com.security.custom.enums.token.TokenKey.JWT_ID;
import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;
import static com.security.custom.enums.token.TokenKey.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TokenServiceTest {

    @Mock
    private EncryptionConfiguration mockEncryptionConfiguration;

    @Mock
    private EncryptorService mockEncryptorService;

    private TokenService service;


    @BeforeEach
    public void init() {
        service = new TokenService(
                mockEncryptionConfiguration,
                mockEncryptorService
        );
        when(mockEncryptionConfiguration.getCustomKey())
                .thenReturn("ItDoesNotCare");
    }


    static Stream<Arguments> createAccessTokenTestCases() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,                   rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                                       null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                                       rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                                       null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                                       rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             null,                           null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             rawAuthenticationInformation,   null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             null,                           tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,             rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createAccessTokenTestCases")
    @DisplayName("createAccessToken: test cases")
    public void createAccessToken_testCases(ApplicationClientDetails applicationClientDetails,
                                            RawAuthenticationInformationDto rawAuthenticationInformation,
                                            String tokenIdentifier,
                                            Class<? extends Exception> expectedException) throws Exception {
        when(mockEncryptorService.defaultDecrypt(anyString()))
                .then(
                        returnsFirstArg()
                );
        when(mockEncryptorService.encrypt(anyString(), anyString()))
                .then(
                        returnsFirstArg()
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.createAccessToken(applicationClientDetails, rawAuthenticationInformation, tokenIdentifier)
            );
        }
        else {
            assertNotNull(
                    service.createAccessToken(applicationClientDetails, rawAuthenticationInformation, tokenIdentifier)
            );
        }

        verifyEncryptInvocations(applicationClientDetails);
    }


    static Stream<Arguments> createRefreshTokenTestCases() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,                   rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                                       null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                                       rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                                       null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                                       rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             null,                           null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             rawAuthenticationInformation,   null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             null,                           tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,             rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   rawAuthenticationInformation,   tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createRefreshTokenTestCases")
    @DisplayName("createRefreshToken: test cases")
    public void createRefreshToken_testCases(ApplicationClientDetails applicationClientDetails,
                                             RawAuthenticationInformationDto rawAuthenticationInformation,
                                             String tokenIdentifier,
                                             Class<? extends Exception> expectedException) throws Exception {
        when(mockEncryptorService.defaultDecrypt(anyString()))
                .then(
                        returnsFirstArg()
                );
        when(mockEncryptorService.encrypt(anyString(), anyString()))
                .then(
                        returnsFirstArg()
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.createRefreshToken(applicationClientDetails, rawAuthenticationInformation, tokenIdentifier)
            );
        }
        else {
            assertNotNull(
                    service.createRefreshToken(applicationClientDetails, rawAuthenticationInformation, tokenIdentifier)
            );
        }

        verifyEncryptInvocations(applicationClientDetails);
    }


    @Test
    @DisplayName("getNewIdentifier: test cases")
    public void getNewIdentifier_testCases() {
        String result = service.getNewIdentifier();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    static Stream<Arguments> getPayloadOfTokenTestCases() {
        Map<String, Object> expectedResultValidToken = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("roles", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,                   token,                                      expectedException,                expectedResult
                Arguments.of( null,                                       null,                                       IllegalArgumentException.class,   null ),
                Arguments.of( null,                                       "ItDoesNotCare",                            IllegalArgumentException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             NOT_JWE_TOKEN,                              TokenInvalidException.class,      null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,       TokenExpiredException.class,      null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,             NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,   null,                             expectedResultValidToken ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,             NOT_JWS_TOKEN,                              TokenInvalidException.class,      null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,             EXPIRED_JWS_TOKEN_HS256,                    TokenExpiredException.class,      null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,             NOT_EXPIRED_JWS_TOKEN_HS256,                null,                             expectedResultValidToken ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,   null,                             expectedResultValidToken ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   NOT_EXPIRED_JWS_TOKEN_HS256,                null,                             expectedResultValidToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadOfTokenTestCases")
    @DisplayName("getPayloadOfToken: test cases")
    public void getPayloadOfToken_testCases(ApplicationClientDetails applicationClientDetails,
                                            String token,
                                            Class<? extends Exception> expectedException,
                                            Map<String, Object> expectedResult) throws Exception {
        when(mockEncryptorService.defaultDecrypt(anyString()))
                .then(
                        returnsFirstArg()
                );
        when(mockEncryptorService.decrypt(anyString(), anyString()))
                .then(
                        returnsFirstArg()
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.getPayloadOfToken(applicationClientDetails, token)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    service.getPayloadOfToken(applicationClientDetails, token)
            );
        }

        verifyDecryptInvocations(applicationClientDetails);
    }


    static Stream<Arguments> isPayloadRelatedWithAccessTokenTestCases() {
        Map<String, Object> payloadAccessToken = new HashMap<>() {{
           put(USERNAME.name(), "username value");
            put(JWT_ID.name(), "token identifier");
        }};
        Map<String, Object> payloadRefreshToken = new HashMap<>() {{
            put(USERNAME.name(), "username value");
            put(JWT_ID.name(), "token identifier");
            put(REFRESH_JWT_ID.name(), "token identifier");
        }};
        return Stream.of(
                //@formatter:off
                //            payload,               expectedResult
                Arguments.of( null,                  true ),
                Arguments.of( new HashMap<>(),       true ),
                Arguments.of( payloadAccessToken,    true ),
                Arguments.of( payloadRefreshToken,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isPayloadRelatedWithAccessTokenTestCases")
    @DisplayName("isPayloadRelatedWithAccessToken: test cases")
    public void isPayloadRelatedWithAccessToken_testCases(Map<String, Object> payload,
                                                          boolean expectedResult) {
        assertEquals(
                expectedResult,
                service.isPayloadRelatedWithAccessToken(payload)
        );
    }


    private void verifyDecryptInvocations(ApplicationClientDetails applicationClientDetails) throws Exception {
        if (null == applicationClientDetails || !TokenType.getEncryptedTokenTypes().contains(applicationClientDetails.getTokenType())) {
            verify(mockEncryptorService, never())
                    .decrypt(
                            anyString(),
                            anyString()
                    );
        }
        if (null != applicationClientDetails && TokenType.getEncryptedTokenTypes().contains(applicationClientDetails.getTokenType())) {
            verify(mockEncryptorService, times(1))
                    .decrypt(
                            anyString(),
                            anyString()
                    );
        }
    }


    private void verifyEncryptInvocations(ApplicationClientDetails applicationClientDetails) throws Exception {
        if (null == applicationClientDetails || !TokenType.getEncryptedTokenTypes().contains(applicationClientDetails.getTokenType())) {
            verify(mockEncryptorService, never())
                    .encrypt(
                            anyString(),
                            anyString()
                    );
        }
        if (null != applicationClientDetails && TokenType.getEncryptedTokenTypes().contains(applicationClientDetails.getTokenType())) {
            verify(mockEncryptorService, times(1))
                    .encrypt(
                            anyString(),
                            anyString()
                    );
        }
    }



    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_JWE = buildApplicationClientDetailsJWE("JWE");

    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_JWS = buildApplicationClientDetailsJWS("JWS");

    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE;

    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS;

    static {
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE = buildApplicationClientDetailsJWE("JWE");
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE.setTokenType(TokenType.ENCRYPTED_JWE);

        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS = buildApplicationClientDetailsJWS("JWS");
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS.setTokenType(TokenType.ENCRYPTED_JWS);
    }

    private static final String NOT_JWS_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
            + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
            + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";

    private static final String NOT_JWE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

    private static final String EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY1OTU3LCJpYXQiOjE3MjQ2NjU5NTcsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.IPhRr7D68LHqWOkK763CLmtdvpDSV_b93GA5aWNHMqI";

    private static final String NOT_EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

    private static final String EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..q6ShzxgjvhRmqApkYZgU2w"
            + ".RDP2BYn9PD-qoxBoeCv24C2WKPyreNA_WSjFY-zHH3b6_oB-NiUSJtr1YJ1lPXJhIqB7svWKTe28KxIGEWbjud9P0NPhzN3j-rnQkJztHETHd_RHnn3PCHb0oHdisCocx7lDg5d_kTHrlgT"
            + "tecvYXkXd3HGtjChqK7HyEYKUgk_LD6Jdyh7K9hMLbD6gmE9JYPue1FLW6oHC8QycPblbb9G5Dl9Z9oUJlLC_i8UAgSAkll1Cox5qCaLHGkuH5oODf8ZI3NGYuuId7ZFqdxgWwhebd2wpo9c"
            + "K9pkHoY2otoo.rDV2sPBllhY3PZtlq44b5Q";

    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..f5--WfcLKFYYxkmYjc"
            + "7SBw.lRtxRB5HysExK4G_DPhNOo8i4p0Fk0RfuaqIRpJc9uwW4ukLyGfjJMVGEZxEfoYqCCd0-_2pOgJlkndwlpguzT78OguBzm2SLc5A8IzavsNzIjL6Aua_snqIlaFC5SsfzB63Z66soKN"
            + "r4Duw5bCsQRmeA2To_Quu1Qtr_l3e96dYow50HzQwxO9Bn96pFIx59g23KFbxCAsoTd5jJUqZIO1WhpYs_Dk6kvMVzjZEr0ELu5AbNmMs2EPaYzXPIuc8EG-r1pG-tR7zE8a45Mj6QuVhls6"
            + "ka45vCEJpex0yMNs.zFFs8uq5E3clFESX0Cqa0g";

}
