package com.security.custom.service;

import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthenticationService;
import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.dto.AuthenticationRequestLoginTokenDto;
import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientMismatchException;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotSavedException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.interfaces.IApplicationClientAuthenticationService;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.token.TokenService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationAuthorizationCodeDto;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.enums.HashAlgorithm;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private ApplicationClientDetailsService mockApplicationClientDetailsService;

    @Mock
    private AuthenticationRequestDetailsService mockAuthenticationRequestDetailsService;

    @Mock
    private AuthorizationService mockAuthorizationService;

    @Mock
    private TokenService mockTokenService;

    private AuthenticationService service;


    @BeforeEach
    public void init() {
        service = new AuthenticationService(
                mockApplicationContext,
                mockApplicationClientDetailsService,
                mockAuthenticationRequestDetailsService,
                mockAuthorizationService,
                mockTokenService
        );
    }


    @Test
    @DisplayName("login: when no applicationClientId is found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void login_whenNoApplicationClientIdIsFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = "NotFound";
        String username = "username value";
        String password = "password value";

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, never())
                .getBean(
                        eq(IApplicationClientAuthenticationService.class)
                );
        verify(mockApplicationClientDetailsService, never())
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("login: when no ApplicationClientAuthenticationService is found related with SecurityHandler then BeansException is thrown")
    public void login_whenNoApplicationClientAuthenticationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
    }


    @Test
    @DisplayName("login: when no ApplicationClientDetails is found in database then ApplicationClientNotFoundException is thrown")
    public void login_whenNoApplicationClientDetailsIsFoundInDatabase_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenThrow(
                        ApplicationClientNotFoundException.class
                );

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, never())
                .getBean(
                        eq(IApplicationClientAuthenticationService.class)
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("login: when a User with given username is not found in database then UsernameNotFoundException is thrown")
    public void login_whenAUserWithGivenUsernameIsNotFoundInDatabase_thenUsernameNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetailsJWE(applicationClientId)
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenThrow(
                        UsernameNotFoundException.class
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
    }


    @Test
    @DisplayName("login: when User with given username is found in database but inactive then AccountStatusException is thrown")
    public void login_whenUserWithGivenUsernameIsFoundInDatabaseButInactive_thenAccountStatusExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetailsJWE(applicationClientId)
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenThrow(
                        LockedException.class
                );

        assertThrows(
                AccountStatusException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
    }


    @Test
    @DisplayName("login: when User with given username is found in database but passwords do not match then UnauthorizedException is thrown")
    public void login_whenUserWithGivenUsernameIsFoundInDatabaseButPasswordsDoNotMatch_thenUnauthorizedExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);
        User user = buildUser(username, password + "V2", true);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetailsJWE(applicationClientId)
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenReturn(
                        user
                );
        when(mockAuthenticationService.isValidPassword(eq(password), eq(user)))
                .thenReturn(
                        false
                );

        assertThrows(
                UnauthorizedException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
        verify(mockAuthenticationService, times(1))
                .isValidPassword(
                        eq(password),
                        eq(user)
                );
    }


    static Stream<Arguments> loginNoExceptionThrownTestCases() {
        Optional<RawAuthenticationInformationDto> rawAuthenticationInformation = of(
                buildRawAuthenticationInformationDto(
                        "username value",
                        List.of(
                                RoleEnum.ROLE_ADMIN.name()
                        )
                )
        );
        AuthenticationInformationDto authenticationInformation = buildAuthenticationInformationDto("11");
        return Stream.of(
                //@formatter:off
                //            rawAuthenticationInformation,   expectedResult
                Arguments.of( empty(),                        empty() ),
                Arguments.of( rawAuthenticationInformation,   of(authenticationInformation) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loginNoExceptionThrownTestCases")
    @DisplayName("login: no exception thrown test cases")
    public void loginNoExceptionThrown_testCases(Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                 Optional<AuthenticationInformationDto> expectedResult) {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        String username = "username value";
        String password = "password value";
        User user = buildUser(username, password, true);
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(
                Spring6MicroserviceAuthenticationService.class
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenReturn(
                        user
                );
        when(mockAuthenticationService.isValidPassword(eq(password), eq(user)))
                .thenReturn(
                        true
                );
        when(mockAuthenticationService.getRawAuthenticationInformation(eq(user)))
                .thenReturn(
                        rawAuthenticationInformation
                );
        if (expectedResult.isPresent()) {
            when(mockTokenService.getNewIdentifier())
                    .thenReturn(
                            expectedResult.get().getId()
                    );
            when(mockTokenService.createAccessToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getAccessToken()
                    );
            when(mockTokenService.createRefreshToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getRefreshToken()
                    );
        }

        Optional<AuthenticationInformationDto> result = service.login(
                applicationClientId,
                username,
                password
        );

        int createTokensInvocations = 0;
        if (expectedResult.isEmpty()) {
            assertTrue(result.isEmpty());
        } else {
            assertTrue(result.isPresent());
            assertEquals(
                    expectedResult,
                    result
            );
            createTokensInvocations = 1;
        }

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
        verify(mockAuthenticationService, times(1))
                .isValidPassword(
                        eq(password),
                        eq(user)
                );
        verify(mockAuthenticationService, times(1))
                .getRawAuthenticationInformation(
                        eq(user)
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createAccessToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createRefreshToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
    }


    @Test
    @DisplayName("loginAuthorized: when no applicationClientId is provided then ApplicationClientNotFoundException is thrown")
    public void loginAuthorized_whenNoApplicationClientIdIsProvided_thenApplicationClientNotFoundExceptionIsThrown() {
        AuthenticationRequestLoginAuthorizedDto authenticationRequest = buildAuthenticationRequestLoginAuthorizedDto(
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.loginAuthorized(
                        null,
                        authenticationRequest
                )
        );

        verify(mockAuthenticationRequestDetailsService, never())
                .save(
                        any(),
                        any(AuthenticationRequestLoginAuthorizedDto.class)
                );
    }


    @Test
    @DisplayName("loginAuthorized: when authentication request could not be stored then AuthenticationRequestDetailsNotSavedException is thrown")
    public void loginAuthorized_whenAuthenticationRequestCouldNotBeStored_thenAuthenticationRequestDetailsNotSavedExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        AuthenticationRequestLoginAuthorizedDto authenticationRequest = buildAuthenticationRequestLoginAuthorizedDto(
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        when(mockAuthenticationRequestDetailsService.save(eq(applicationClientId), any(AuthenticationRequestLoginAuthorizedDto.class)))
                .thenThrow(
                        AuthenticationRequestDetailsNotSavedException.class
                );

        assertThrows(
                AuthenticationRequestDetailsNotSavedException.class,
                () -> service.loginAuthorized(
                        applicationClientId,
                        authenticationRequest
                )
        );

        verify(mockAuthenticationRequestDetailsService, times(1))
                .save(
                        eq(applicationClientId),
                        any(AuthenticationRequestLoginAuthorizedDto.class)
                );
    }


    static Stream<Arguments> loginAuthorizedNoExceptionThrownTestCases() {
        AuthenticationRequestDetails authenticationRequestDetails = buildAuthenticationRequestDetails("authorizationCodeValue");
        AuthenticationInformationAuthorizationCodeDto authenticationInformation = buildAuthenticationInformationAuthorizationCodeDto(
                authenticationRequestDetails.getAuthorizationCode()
        );
        return Stream.of(
                //@formatter:off
                //            saveResult,                         expectedResult
                Arguments.of( empty(),                            empty() ),
                Arguments.of( of(authenticationRequestDetails),   of(authenticationInformation) )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("loginAuthorizedNoExceptionThrownTestCases")
    @DisplayName("loginAuthorized: no exception thrown test cases")
    public void loginAuthorizedNoExceptionThrown_testCases(Optional<AuthenticationRequestDetails> saveResult,
                                                           Optional<AuthenticationInformationAuthorizationCodeDto> expectedResult) {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        AuthenticationRequestLoginAuthorizedDto authenticationRequest = buildAuthenticationRequestLoginAuthorizedDto(
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        when(mockAuthenticationRequestDetailsService.save(eq(applicationClientId), any(AuthenticationRequestLoginAuthorizedDto.class)))
                .thenReturn(
                        saveResult
                );

        Optional<AuthenticationInformationAuthorizationCodeDto> result = service.loginAuthorized(
                applicationClientId,
                authenticationRequest
        );

        if (expectedResult.isEmpty()) {
            assertTrue(result.isEmpty());
        } else {
            assertTrue(result.isPresent());
            assertEquals(
                    expectedResult,
                    result
            );
        }

        verify(mockAuthenticationRequestDetailsService, times(1))
                .save(
                        eq(applicationClientId),
                        any(AuthenticationRequestLoginAuthorizedDto.class)
                );
    }


    @Test
    @DisplayName("loginToken: when given authenticationRequestLoginToken is null then IllegalArgumentException is thrown")
    public void loginToken_whenGivenAuthenticationRequestLoginTokenIsNull_thenIllegalArgumentExceptionIsThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.loginToken(
                        "ItDoesNotCare",
                        null
                )
        );
    }


    @Test
    @DisplayName("loginToken: when given authorizationCode does not exist in the cache then AuthenticationRequestDetailsNotFoundException is thrown")
    public void loginToken_whenGivenAuthorizationCodeDoesNotExistInTheCache_thenAuthenticationRequestDetailsNotFoundExceptionIsThrown() {
        String authorizationCode = "NotFound";
        AuthenticationRequestLoginTokenDto authenticationRequestLoginToken = buildAuthenticationRequestLoginTokenDto(
                "username value",
                "password value",
                authorizationCode,
                "ItDoesNotCare"
        );

        when(mockAuthenticationRequestDetailsService.findByAuthorizationCode(eq(authorizationCode)))
                .thenThrow(
                        AuthenticationRequestDetailsNotFoundException.class
                );

        assertThrows(
                AuthenticationRequestDetailsNotFoundException.class,
                () -> service.loginToken(
                        "ItDoesNotCare",
                        authenticationRequestLoginToken
                )
        );

        verify(mockAuthenticationRequestDetailsService, times(1))
                .findByAuthorizationCode(
                        eq(authorizationCode)
                );
    }


    static Stream<Arguments> loginTokenApplicationClientMismatchExceptionTestCases() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();

        AuthenticationRequestDetails withoutApplicationClientId = buildAuthenticationRequestDetails(
                "ItDoesNotCare",
                null
        );
        AuthenticationRequestDetails differentApplicationClientId = buildAuthenticationRequestDetails(
                "ItDoesNotCare",
                applicationClientId + "v2"
        );
        return Stream.of(
                //@formatter:off
                //            applicationClientId,   authenticationRequestDetails
                Arguments.of( null,                  withoutApplicationClientId ),
                Arguments.of( applicationClientId,   withoutApplicationClientId ),
                Arguments.of( applicationClientId,   differentApplicationClientId )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loginTokenApplicationClientMismatchExceptionTestCases")
    @DisplayName("loginToken: ApplicationClientMismatchException test cases")
    public void loginTokenApplicationClientMismatchException_testCases(String applicationClientId,
                                                                       AuthenticationRequestDetails authenticationRequestDetails) {
        String authorizationCode = "Found";
        AuthenticationRequestLoginTokenDto authenticationRequestLoginToken = buildAuthenticationRequestLoginTokenDto(
                "username value",
                "password value",
                authorizationCode,
                "ItDoesNotCare"
        );

        when(mockAuthenticationRequestDetailsService.findByAuthorizationCode(eq(authorizationCode)))
                .thenReturn(
                        authenticationRequestDetails
                );

        assertThrows(
                ApplicationClientMismatchException.class,
                () -> service.loginToken(
                        applicationClientId,
                        authenticationRequestLoginToken
                )
        );
    }


    @Test
    @DisplayName("loginToken: when given verifier does not match with stored challenge then UnauthorizedException is thrown")
    public void loginToken_whenGivenVerifierDoesNotMatchWithStoredChallenge_thenUnauthorizedExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String authorizationCode = "Found";
        AuthenticationRequestLoginTokenDto authenticationRequestLoginToken = buildAuthenticationRequestLoginTokenDto(
                "username value",
                "password value",
                authorizationCode,
                "ItDoesNotMatch"
        );
        AuthenticationRequestDetails authenticationRequestDetails = buildAuthenticationRequestDetails(
                authorizationCode,
                applicationClientId
        );

        when(mockAuthenticationRequestDetailsService.findByAuthorizationCode(eq(authorizationCode)))
                .thenReturn(
                        authenticationRequestDetails
                );

        assertThrows(
                UnauthorizedException.class,
                () -> service.loginToken(
                        applicationClientId,
                        authenticationRequestLoginToken
                )
        );

        verify(mockAuthenticationRequestDetailsService, times(1))
                .findByAuthorizationCode(
                        eq(authorizationCode)
                );
    }


    static Stream<Arguments> loginTokenNoExceptionThrownTestCases() {
        Optional<RawAuthenticationInformationDto> rawAuthenticationInformation = of(
                buildRawAuthenticationInformationDto(
                        "username value",
                        List.of(
                                RoleEnum.ROLE_ADMIN.name()
                        )
                )
        );
        AuthenticationInformationDto authenticationInformation = buildAuthenticationInformationDto("11");
        return Stream.of(
                //@formatter:off
                //            rawAuthenticationInformation,   expectedResult
                Arguments.of( empty(),                        empty() ),
                Arguments.of( rawAuthenticationInformation,   of(authenticationInformation) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loginTokenNoExceptionThrownTestCases")
    @DisplayName("loginToken: no exception thrown test cases")
    public void loginTokenNoExceptionThrown_testCases(Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                      Optional<AuthenticationInformationDto> expectedResult) {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String authorizationCode = "Found";
        String verifier = "verifier value";
        String username = "username value";
        String password = "password value";
        AuthenticationRequestLoginTokenDto authenticationRequestLoginToken = buildAuthenticationRequestLoginTokenDto(
                username,
                password,
                authorizationCode,
                verifier
        );
        AuthenticationRequestDetails authenticationRequestDetails = buildAuthenticationRequestDetails(
                authorizationCode,
                applicationClientId,
                "f58dd405627d81b1bb902a0dac75848e8f9bc937f1f2f0f111ac781ba2c187f24c2611772b1ffc7a02efb1935958d673",
                HashAlgorithm.SHA_384
        );
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        User user = buildUser(username, password, true);
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(
                Spring6MicroserviceAuthenticationService.class
        );

        when(mockAuthenticationRequestDetailsService.findByAuthorizationCode(eq(authorizationCode)))
                .thenReturn(
                        authenticationRequestDetails
                );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenReturn(
                        user
                );
        when(mockAuthenticationService.isValidPassword(eq(password), eq(user)))
                .thenReturn(
                        true
                );
        when(mockAuthenticationService.getRawAuthenticationInformation(eq(user)))
                .thenReturn(
                        rawAuthenticationInformation
                );
        if (expectedResult.isPresent()) {
            when(mockTokenService.getNewIdentifier())
                    .thenReturn(
                            expectedResult.get().getId()
                    );
            when(mockTokenService.createAccessToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getAccessToken()
                    );
            when(mockTokenService.createRefreshToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getRefreshToken()
                    );
        }

        Optional<AuthenticationInformationDto> result = service.loginToken(
                applicationClientId,
                authenticationRequestLoginToken
        );

        int createTokensInvocations = 0;
        if (expectedResult.isEmpty()) {
            assertTrue(result.isEmpty());
        } else {
            assertTrue(result.isPresent());
            assertEquals(
                    expectedResult,
                    result
            );
            createTokensInvocations = 1;
        }

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
        verify(mockAuthenticationService, times(1))
                .isValidPassword(
                        eq(password),
                        eq(user)
                );
        verify(mockAuthenticationService, times(1))
                .getRawAuthenticationInformation(
                        eq(user)
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createAccessToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createRefreshToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
    }


    @Test
    @DisplayName("refresh: when no applicationClientId is found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void refresh_whenNoApplicationClientIdIsFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = "NotFound";
        String refreshToken = "ItDoesNotCare";

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, never())
                .getBean(
                        eq(IApplicationClientAuthenticationService.class)
                );
        verify(mockApplicationClientDetailsService, never())
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("refresh: when no ApplicationClientAuthenticationService is found related with SecurityHandler then BeansException is thrown")
    public void refresh_whenNoApplicationClientAuthenticationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "ItDoesNotCare";

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
    }


    @Test
    @DisplayName("refresh: when no ApplicationClientDetails is found in database then ApplicationClientNotFoundException is thrown")
    public void refresh_whenNoApplicationClientDetailsIsFoundInDatabase_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "ItDoesNotCare";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenThrow(
                        ApplicationClientNotFoundException.class
                );

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, never())
                .getBean(
                        eq(IApplicationClientAuthenticationService.class)
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("refresh: when there is a problem getting content of refreshToken then TokenException is thrown")
    public void refresh_whenThereIsAProblemGettingContentOfRefreshToken_thenTokenExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "NotValidToken";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mock(Spring6MicroserviceAuthenticationService.class)
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthorizationService.checkRefreshToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenThrow(
                        TokenException.class
                );

        assertThrows(
                TokenException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthorizationService, times(1))
                .checkRefreshToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
    }


    @Test
    @DisplayName("refresh: when refreshToken does not contain username value then UsernameNotFoundException is thrown")
    public void refresh_whenRefreshTokenDoesNotContainUsernameValue_thenUsernameNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "ItDoesNotCare";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthorizationService.checkRefreshToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenThrow(
                        UsernameNotFoundException.class
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthorizationService, times(1))
                .checkRefreshToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
    }


    @Test
    @DisplayName("refresh: when a User with given username is not found in database then UsernameNotFoundException is thrown")
    public void refresh_whenAUserWithGivenUsernameIsNotFoundInDatabase_thenUsernameNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "ItDoesNotCare";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);
        String username = "username value";
        AuthorizationInformationDto authorizationInformation = AuthorizationInformationDto.builder()
                .username(username)
                .build();

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthorizationService.checkRefreshToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenReturn(
                        authorizationInformation
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenThrow(
                        UsernameNotFoundException.class
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthorizationService, times(1))
                .checkRefreshToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
    }


    @Test
    @DisplayName("refresh: when User with given username is found in database but inactive then AccountStatusException is thrown")
    public void refresh_whenUserWithGivenUsernameIsFoundInDatabaseButInactive_thenAccountStatusExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String refreshToken = "ItDoesNotCare";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);
        String username = "username value";
        AuthorizationInformationDto authorizationInformation = AuthorizationInformationDto.builder()
                .username(username)
                .build();

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthorizationService.checkRefreshToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenReturn(
                        authorizationInformation
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenThrow(
                        LockedException.class
                );

        assertThrows(
                AccountStatusException.class,
                () -> service.refresh(
                        applicationClientId,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthorizationService, times(1))
                .checkRefreshToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
    }


    static Stream<Arguments> refreshNoExceptionThrownTestCases() {
        Optional<RawAuthenticationInformationDto> rawAuthenticationInformation = of(
                buildRawAuthenticationInformationDto(
                        "username value",
                        List.of(
                                RoleEnum.ROLE_ADMIN.name()
                        )
                )
        );
        AuthenticationInformationDto authenticationInformation = buildAuthenticationInformationDto("11");
        return Stream.of(
                //@formatter:off
                //            rawAuthenticationInformation,   expectedResult
                Arguments.of( empty(),                        empty() ),
                Arguments.of( rawAuthenticationInformation,   of(authenticationInformation) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("refreshNoExceptionThrownTestCases")
    @DisplayName("refresh: no exception thrown test cases")
    public void refreshNoExceptionThrown_testCases(Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                   Optional<AuthenticationInformationDto> expectedResult) {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        String refreshToken = "ItDoesNotCare";
        String username = "username value";
        String password = "password value";
        AuthorizationInformationDto authorizationInformation = AuthorizationInformationDto.builder()
                .username(username)
                .authorities(
                        Set.of(RoleEnum.ROLE_ADMIN.name())
                )
                .additionalInformation(new HashMap<>())
                .build();

        User user = buildUser(username, password, true);

        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(
                Spring6MicroserviceAuthenticationService.class
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockAuthorizationService.checkRefreshToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenReturn(
                        authorizationInformation
                );
        when(mockAuthenticationService.loadUserByUsername(eq(username)))
                .thenReturn(
                        user
                );
        when(mockAuthenticationService.getRawAuthenticationInformation(eq(user)))
                .thenReturn(
                        rawAuthenticationInformation
                );
        if (expectedResult.isPresent()) {
            when(mockTokenService.getNewIdentifier())
                    .thenReturn(
                            expectedResult.get().getId()
                    );
            when(mockTokenService.createAccessToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getAccessToken()
                    );
            when(mockTokenService.createRefreshToken(eq(applicationClientDetails), eq(rawAuthenticationInformation.get()), eq(expectedResult.get().getId())))
                    .thenReturn(
                            expectedResult.get().getRefreshToken()
                    );
        }

        Optional<AuthenticationInformationDto> result = service.refresh(
                applicationClientId,
                refreshToken
        );

        int createTokensInvocations = 0;
        if (expectedResult.isEmpty()) {
            assertTrue(result.isEmpty());
        } else {
            assertTrue(result.isPresent());
            assertEquals(
                    expectedResult,
                    result
            );
            createTokensInvocations = 1;
        }

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<IApplicationClientAuthenticationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockAuthorizationService, times(1))
                .checkRefreshToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
        verify(mockAuthenticationService, times(1))
                .loadUserByUsername(
                        eq(username)
                );
        verify(mockAuthenticationService, times(1))
                .getRawAuthenticationInformation(
                        eq(user)
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createAccessToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
        verify(mockTokenService, times(createTokensInvocations))
                .createRefreshToken(
                        eq(applicationClientDetails),
                        any(),
                        anyString()
                );
    }

}
