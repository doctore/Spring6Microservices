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
public class EncryptedJwsProviderTest {

    @Mock
    private EncryptionConfiguration mockEncryptionConfiguration;

    @Mock
    private EncryptorService mockEncryptorService;

    private EncryptedJwsProvider provider;


    @BeforeEach
    public void init() {
        provider = new EncryptedJwsProvider(
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
                () -> provider.generateToken(APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS, new HashMap<>(), 1)
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
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   null,                   1,                        TokenTypeProviderException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   informationToInclude,   1,                        TokenTypeProviderException.class ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   null,                   1,                        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   informationToInclude,   1,                        null )
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
                () -> provider.getPayloadOfToken(APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS, "ItDoesNotCare")
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
                //            applicationClientDetails,                   token,                         expectedException,                  expectedResult
                Arguments.of( null,                                       null,                          IllegalArgumentException.class,     null ),
                Arguments.of( null,                                       "ItDoesNotCare",               IllegalArgumentException.class,     null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   null,                          TokenTypeProviderException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWE,   "ItDoesNotCare",               TokenTypeProviderException.class,   null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   NOT_JWS_TOKEN,                 TokenInvalidException.class,        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   EXPIRED_JWS_TOKEN_HS256,       TokenExpiredException.class,        null ),
                Arguments.of( APPLICATION_CLIENT_DETAILS_ENCRYPTED_JWS,   NOT_EXPIRED_JWS_TOKEN_HS256,   null,                               expectedResultValidToken )
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

    private static final String NOT_JWS_TOKEN = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
            + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
            + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";

    private static final String EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NzI0NjY1OTU3LCJpYXQiOjE3MjQ2NjU5NTcsImFnZSI"
            + "6MjMsInJvbGVzIjpbImFkbWluIiwidXNlciJdLCJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIn0.IPhRr7D68LHqWOkK763CLmtdvpDSV_b93GA5aWNHMqI";

    private static final String NOT_EXPIRED_JWS_TOKEN_HS256 = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibmFtZSB2YWx1ZSIsImV4cCI6NTAwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwLCJ"
            + "hZ2UiOjIzLCJyb2xlcyI6WyJhZG1pbiIsInVzZXIiXSwidXNlcm5hbWUiOiJ1c2VybmFtZSB2YWx1ZSJ9.xhFgeEc5bGDJ_EOhxcefDQ4olqViOzPCxjjFH2NIGhk";

}
