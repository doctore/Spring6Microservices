package com.security.custom.service.token.provider;

import com.security.custom.configuration.security.EncryptionConfiguration;
import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.EncryptorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class EncryptedJweProviderTest {

    @Mock
    private EncryptionConfiguration mockEncryptionConfiguration;

    @Mock
    private EncryptorService mockEncryptorService;

    private EncryptedJweProvider provider;


    @BeforeEach
    public void init() {
        provider = new EncryptedJweProvider(
                mockEncryptionConfiguration,
                mockEncryptorService
        );
        when(mockEncryptionConfiguration.getCustomKey())
                .thenReturn(
                        "ItDoesNotCare"
                );
        when(mockEncryptorService.defaultDecrypt(anyString()))
                .then(
                        returnsFirstArg()
                );
    }


    @Test
    @DisplayName("generateToken: when there was an error encrypting then UnsupportedOperationException is thrown")
    public void generateToken_whenThereWasAnErrorEncrypting_thenUnsupportedOperationExceptionIsThrown() throws Exception {
        when(mockEncryptorService.encrypt(anyString(), anyString()))
                .thenThrow(
                        GeneralSecurityException.class
                );

        assertThrows(
                UnsupportedOperationException.class,
                () -> provider.generateToken(APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE, new HashMap<>(), 1)
        );

        verify(mockEncryptorService, times(1))
                .encrypt(
                        anyString(),
                        anyString()
                );
    }


    static Stream<Arguments> generateTokenNoExceptionEncryptingTestCases() {
        Map<String, Object> informationToInclude = new LinkedHashMap<>() {{
            put("username", "username value");
            put("roles", List.of("admin", "user"));
            put("name", "name value");
        }};
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,                   informationToInclude,   tokenValidityInSeconds,   expectedException
                Arguments.of( null,                                       null,                   1,                        IllegalArgumentException.class ),
                Arguments.of( null,                                       informationToInclude,   1,                        IllegalArgumentException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   null,                   1,                        TokenTypeProviderException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   informationToInclude,   1,                        TokenTypeProviderException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   null,                   1,                        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   informationToInclude,   1,                        null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenNoExceptionEncryptingTestCases")
    @DisplayName("generateToken: when encrypting no exception is thrown test cases")
    public void generateTokenNoExceptionEncrypting_testCases(ApplicationClientDetails applicationClientDetails,
                                                             Map<String, Object> informationToInclude,
                                                             int tokenValidityInSeconds,
                                                             Class<? extends Exception> expectedException) throws Exception {
        when(mockEncryptorService.encrypt(anyString(), anyString()))
                .then(
                        returnsFirstArg()
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> provider.generateToken(applicationClientDetails, informationToInclude, tokenValidityInSeconds)
            );
        }
        else {
            assertNotNull(
                    provider.generateToken(applicationClientDetails, informationToInclude, tokenValidityInSeconds)
            );
            verify(mockEncryptorService, times(1))
                    .encrypt(
                            anyString(),
                            anyString()
                    );
        }
    }


    @Test
    @DisplayName("getPayloadOfToken: when there was an error decrypting then UnsupportedOperationException is thrown")
    public void getPayloadOfToken_whenThereWasAnErrorDecrypting_thenUnsupportedOperationExceptionIsThrown() throws Exception {
        when(mockEncryptorService.decrypt(anyString(), anyString()))
                .thenThrow(
                        GeneralSecurityException.class
                );

        assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getPayloadOfToken(APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE, "ItDoesNotCare")
        );

        verify(mockEncryptorService, times(1))
                .decrypt(
                        anyString(),
                        anyString()
                );
    }


    static Stream<Arguments> getPayloadOfTokenNoExceptionDecryptingTestCases() {
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
                //            applicationClientDetails,                   token,                                      expectedException,                  expectedResult
                Arguments.of( null,                                       null,                                       IllegalArgumentException.class,     null ),
                Arguments.of( null,                                       "ItDoesNotCare",                            IllegalArgumentException.class,     null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   null,                                       TokenTypeProviderException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   "ItDoesNotCare",                            TokenTypeProviderException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   NOT_JWE_TOKEN,                              TokenInvalidException.class,        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,       TokenExpiredException.class,        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   NOT_EXPIRED_JWE_TOKEN_DIR__A128CBC_HS256,   null,                               expectedResultValidToken )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("getPayloadOfTokenNoExceptionDecryptingTestCases")
    @DisplayName("getPayloadOfToken: when decrypting no exception is thrown test cases")
    public void getPayloadOfTokenNoExceptionDecrypting_testCases(ApplicationClientDetails applicationClientDetails,
                                                                 String token,
                                                                 Class<? extends Exception> expectedException,
                                                                 Map<String, Object> expectedResult) throws Exception {
        when(mockEncryptorService.decrypt(anyString(), anyString()))
                .then(
                        returnsFirstArg()
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> provider.getPayloadOfToken(applicationClientDetails, token)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    provider.getPayloadOfToken(applicationClientDetails, token)
            );
            verify(mockEncryptorService, times(1))
                    .decrypt(
                            anyString(),
                            anyString()
                    );
        }
    }



    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE;

    private static final ApplicationClientDetails APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS;

    static {
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE = buildApplicationClientDetailsJWE("JWE");
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE.setTokenType(TokenType.ENCRYPTED_JWE);

        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS = buildApplicationClientDetailsJWS("JWS");
        APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS.setTokenType(TokenType.ENCRYPTED_JWS);
    }

    private static final String NOT_JWE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
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
