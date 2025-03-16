package com.security.custom.service.token;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.token.provider.EncryptedJweProvider;
import com.security.custom.service.token.provider.JweProvider;
import com.security.custom.service.token.provider.JwsProvider;
import com.security.custom.service.token.provider.TokenTypeProviderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWS;
import static com.security.custom.TestDataFactory.buildRawAuthenticationInformationDto;
import static com.security.custom.enums.token.TokenKey.JWT_ID;
import static com.security.custom.enums.token.TokenKey.REFRESH_JWT_ID;
import static com.security.custom.enums.token.TokenKey.USERNAME;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TokenServiceTest {

    @Mock
    private TokenTypeProviderRegistry mockTokenTypeProviderRegistry;

    @Mock
    private JwsProvider mockJwsProvider;

    @Mock
    private JweProvider mockJweProvider;

    @Mock
    private EncryptedJweProvider mockEncryptedJweProvider;

    private TokenService service;


    @BeforeEach
    public void init() {
        service = new TokenService(
                mockTokenTypeProviderRegistry
        );
    }


    @Test
    @DisplayName("createAccessToken: when no valid ITokenTypeProvider is found then TokenTypeProviderException is thrown")
    public void createAccessToken_whenNoValidITokenTypeProviderIsFound_thenTokenTypeProviderExceptionIsThrown() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";

        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        empty()
                );

        assertThrows(
                TokenTypeProviderException.class,
                () -> service.createAccessToken(APPLICATION_CLIENT_DETAILS_JWS, rawAuthenticationInformation, tokenIdentifier)
        );

        verify(mockTokenTypeProviderRegistry, times(1))
                .getTokenTypeProvider(
                        any()
                );
    }


    static Stream<Arguments> createAccessTokenFoundITokenTypeProviderTestCases() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,         rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                             null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                             rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                             null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                             rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,   null,                           null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,   rawAuthenticationInformation,   null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,   null,                           tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWS,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createAccessTokenFoundITokenTypeProviderTestCases")
    @DisplayName("createAccessToken: found ITokenProvider test cases")
    public void createAccessTokenFoundITokenTypeProvider_testCases(ApplicationClientDetails applicationClientDetails,
                                                                   RawAuthenticationInformationDto rawAuthenticationInformation,
                                                                   String tokenIdentifier,
                                                                   Class<? extends Exception> expectedException) {
        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        of(mockJwsProvider)
                );
        when(mockJwsProvider.generateToken(eq(applicationClientDetails), anyMap(), anyInt()))
                .thenReturn(
                        NOT_EXPIRED_JWS_TOKEN_HS256
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

            verify(mockTokenTypeProviderRegistry, times(1))
                    .getTokenTypeProvider(
                            any()
                    );
            verify(mockJwsProvider, times(1))
                    .generateToken(
                            eq(applicationClientDetails),
                            anyMap(),
                            anyInt()
                    );
        }
    }


    @Test
    @DisplayName("createRefreshToken: when no valid ITokenTypeProvider is found then TokenTypeProviderException is thrown")
    public void createRefreshToken_whenNoValidITokenTypeProviderIsFound_thenTokenTypeProviderExceptionIsThrown() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";

        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        empty()
                );

        assertThrows(
                TokenTypeProviderException.class,
                () -> service.createRefreshToken(APPLICATION_CLIENT_DETAILS_JWE, rawAuthenticationInformation, tokenIdentifier)
        );

        verify(mockTokenTypeProviderRegistry, times(1))
                .getTokenTypeProvider(
                        any()
                );
    }


    static Stream<Arguments> createRefreshTokenFoundITokenTypeProviderTestCases() {
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,         rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                             null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                             rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                             null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                             rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,   null,                           null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,   rawAuthenticationInformation,   null,              null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,   null,                           tokenIdentifier,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_JWE,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createRefreshTokenFoundITokenTypeProviderTestCases")
    @DisplayName("createRefreshToken: found ITokenProvider test cases")
    public void createRefreshTokenFoundITokenTypeProvider_testCases(ApplicationClientDetails applicationClientDetails,
                                                                    RawAuthenticationInformationDto rawAuthenticationInformation,
                                                                    String tokenIdentifier,
                                                                    Class<? extends Exception> expectedException) {
        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        of(mockJweProvider)
                );
        when(mockJweProvider.generateToken(eq(applicationClientDetails), anyMap(), anyInt()))
                .thenReturn(
                        NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256
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

            verify(mockTokenTypeProviderRegistry, times(1))
                    .getTokenTypeProvider(
                            any()
                    );
            verify(mockJweProvider, times(1))
                    .generateToken(
                            eq(applicationClientDetails),
                            anyMap(),
                            anyInt()
                    );
        }
    }


    @Test
    @DisplayName("getNewIdentifier: test cases")
    public void getNewIdentifier_testCases() {
        String result = service.getNewIdentifier();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    @DisplayName("getPayloadOfToken: when no valid ITokenTypeProvider is found then TokenTypeProviderException is thrown")
    public void getPayloadOfToken_whenNoValidITokenTypeProviderIsFound_thenTokenTypeProviderExceptionIsThrown() {
        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        empty()
                );

        assertThrows(
                TokenTypeProviderException.class,
                () -> service.getPayloadOfToken(APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS, "ItDoesNotCare")
        );

        verify(mockTokenTypeProviderRegistry, times(1))
                .getTokenTypeProvider(
                        any()
                );
    }


    static Stream<Arguments> getPayloadOfTokenFoundITokenTypeProviderTestCases() {
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
                Arguments.of( null,                                       NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,   IllegalArgumentException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,   null,                             expectedResultValidToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadOfTokenFoundITokenTypeProviderTestCases")
    @DisplayName("getPayloadOfToken: found ITokenProvider test cases")
    public void getPayloadOfTokenFoundITokenTypeProvider_testCases(ApplicationClientDetails applicationClientDetails,
                                                                   String token,
                                                                   Class<? extends Exception> expectedException,
                                                                   Map<String, Object> expectedResult) {
        when(mockTokenTypeProviderRegistry.getTokenTypeProvider(any()))
                .thenReturn(
                        of(mockEncryptedJweProvider)
                );
        when(mockEncryptedJweProvider.getPayloadOfToken(eq(applicationClientDetails), eq(token)))
                .thenReturn(
                        expectedResult
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

            verify(mockTokenTypeProviderRegistry, times(1))
                    .getTokenTypeProvider(
                            any()
                    );
            verify(mockEncryptedJweProvider, times(1))
                    .getPayloadOfToken(
                            eq(applicationClientDetails),
                            eq(token)
                    );
        }
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

    private static final String NOT_EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

    private static final String NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256 = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..f5--WfcLKFYYxkmYjc"
            + "7SBw.lRtxRB5HysExK4G_DPhNOo8i4p0Fk0RfuaqIRpJc9uwW4ukLyGfjJMVGEZxEfoYqCCd0-_2pOgJlkndwlpguzT78OguBzm2SLc5A8IzavsNzIjL6Aua_snqIlaFC5SsfzB63Z66soKN"
            + "r4Duw5bCsQRmeA2To_Quu1Qtr_l3e96dYow50HzQwxO9Bn96pFIx59g23KFbxCAsoTd5jJUqZIO1WhpYs_Dk6kvMVzjZEr0ELu5AbNmMs2EPaYzXPIuc8EG-r1pG-tR7zE8a45Mj6QuVhls6"
            + "ka45vCEJpex0yMNs.zFFs8uq5E3clFESX0Cqa0g";

}
