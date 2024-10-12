package com.security.custom.service;

import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.model.ApplicationClientDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetails;
import static com.security.custom.TestDataFactory.buildRawAuthenticationInformationDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TokenServiceTest {

    @Mock
    private EncryptorService mockEncryptorService;

    private TokenService service;


    @BeforeEach
    public void init() {
        service = new TokenService(
                mockEncryptorService
        );
    }


    static Stream<Arguments> createAccessTokenTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetails("ItDoesNotCare");
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,   rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                       null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                       rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                       null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                       rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( applicationClientDetails,   null,                           null,              null ),
                Arguments.of( applicationClientDetails,   rawAuthenticationInformation,   null,              null ),
                Arguments.of( applicationClientDetails,   null,                           tokenIdentifier,   null ),
                Arguments.of( applicationClientDetails,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createAccessTokenTestCases")
    @DisplayName("createAccessToken: test cases")
    public void createAccessToken_testCases(ApplicationClientDetails applicationClientDetails,
                                            RawAuthenticationInformationDto rawAuthenticationInformation,
                                            String tokenIdentifier,
                                            Class<? extends Exception> expectedException) {
        when(mockEncryptorService.decrypt(anyString()))
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
    }


    static Stream<Arguments> createRefreshTokenTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetails("ItDoesNotCare");
        RawAuthenticationInformationDto rawAuthenticationInformation = buildRawAuthenticationInformationDto(
                "ItDoesNotCare",
                List.of()
        );
        String tokenIdentifier = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,   rawAuthenticationInformation,   tokenIdentifier,   expectedException
                Arguments.of( null,                       null,                           null,              IllegalArgumentException.class ),
                Arguments.of( null,                       rawAuthenticationInformation,   null,              IllegalArgumentException.class ),
                Arguments.of( null,                       null,                           tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( null,                       rawAuthenticationInformation,   tokenIdentifier,   IllegalArgumentException.class ),
                Arguments.of( applicationClientDetails,   null,                           null,              null ),
                Arguments.of( applicationClientDetails,   rawAuthenticationInformation,   null,              null ),
                Arguments.of( applicationClientDetails,   null,                           tokenIdentifier,   null ),
                Arguments.of( applicationClientDetails,   rawAuthenticationInformation,   tokenIdentifier,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createRefreshTokenTestCases")
    @DisplayName("createRefreshToken: test cases")
    public void createRefreshToken_testCases(ApplicationClientDetails applicationClientDetails,
                                             RawAuthenticationInformationDto rawAuthenticationInformation,
                                             String tokenIdentifier,
                                             Class<? extends Exception> expectedException) {
        when(mockEncryptorService.decrypt(anyString()))
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
    }

}
