package com.security.custom.service;

import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthorizationService;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import com.security.custom.model.ApplicationClientDetails;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private TokenService mockTokenService;

    private AuthorizationService service;


    @BeforeEach
    public void init() {
        service = new AuthorizationService(
                mockApplicationContext,
                mockTokenService
        );
    }


    @Test
    @DisplayName("getAuthorities: when applicationClientDetails is null then IllegalArgumentException is thrown")
    public void getAuthorities_whenApplicationClientDetailsIsNull_thenIllegalArgumentExceptionIsThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getAuthorities(
                        null,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("getAuthorities: when applicationClientDetails is not found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void getAuthorities_whenApplicationClientIdIsNotFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("NotFound");

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.getAuthorities(
                        applicationClientDetails,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("getAuthorities: when no ApplicationClientAuthorizationService is found related with SecurityHandler then BeansException is thrown")
    public void getAuthorities_whenNoApplicationClientAuthorizationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.getAuthorities(
                        applicationClientDetails,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
    }


    static Stream<Arguments> getAuthoritiesNoExceptionThrownTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        Map<String, Object> rawAuthorizationInformation = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,   rawAuthorizationInformation,   expectedResult
                Arguments.of( applicationClientDetails,   null,                          new HashSet<>() ),
                Arguments.of( applicationClientDetails,   new HashMap<>(),               new HashSet<>() ),
                Arguments.of( applicationClientDetails,   rawAuthorizationInformation,   Set.of("admin", "user") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAuthoritiesNoExceptionThrownTestCases")
    @DisplayName("getAuthorities: no exception thrown test cases")
    public void getAuthoritiesNoExceptionThrown_testCases(ApplicationClientDetails applicationClientDetails,
                                                          Map<String, Object> rawAuthorizationInformation,
                                                          Set<String> expectedResult) {
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);
        int getAuthoritiesInvocations = null == rawAuthorizationInformation
                ? 0
                : 1;

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockAuthorizationService.getAuthorities(eq(rawAuthorizationInformation)))
                .thenReturn(
                        expectedResult
                );

        assertEquals(
                expectedResult,
                service.getAuthorities(applicationClientDetails, rawAuthorizationInformation)
        );

        verify(mockAuthorizationService, times(getAuthoritiesInvocations))
                .getAuthorities(
                        eq(rawAuthorizationInformation)
                );
    }


    @Test
    @DisplayName("getRawAuthorizationInformation: when applicationClientDetails is null then IllegalArgumentException is thrown")
    public void getRawAuthorizationInformation_whenApplicationClientDetailsIsNull_thenIllegalArgumentExceptionIsThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getRawAuthorizationInformation(
                        null,
                        "ItDoesNotCare",
                        true
                )
        );
    }


    @Test
    @DisplayName("getRawAuthorizationInformation: when there is an error with the token then TokenException is thrown")
    public void getRawAuthorizationInformation_whenThereIsAnErrorWithTheToken_thenTokenExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("ItDoesNotCare");
        String token = "ItDoesNotCare";

        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(token)))
                .thenThrow(
                        TokenException.class
                );

        assertThrows(
                TokenException.class,
                () -> service.getRawAuthorizationInformation(
                        applicationClientDetails,
                        token,
                        true
                )
        );

        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(token)
                );
    }


    @Test
    @DisplayName("getRawAuthorizationInformation: when isAccessToken and given token does not match then TokenInvalidException is thrown")
    public void getRawAuthorizationInformation_whenIsAccessTokenAndGivenTokenDoesNotMatch_thenTokenInvalidExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("ItDoesNotCare");
        String token = "ItDoesNotCare";

        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(token)))
                .thenReturn(
                        new HashMap<>()
                );

        assertThrows(
                TokenInvalidException.class,
                () -> service.getRawAuthorizationInformation(
                        applicationClientDetails,
                        token,
                        false
                )
        );

        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(token)
                );
    }


    static Stream<Arguments> getRawAuthorizationInformationNoExceptionThrownTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        String token = "ItDoesNotCare";
        Map<String, Object> rawAuthorizationInformationAccessToken = new LinkedHashMap<>() {{
            put("jti", "12345");
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        Map<String, Object> rawAuthorizationInformationRefreshToken = new LinkedHashMap<>() {{
            put("jti", "12345");
            put("ati", "12345");
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,   token,   isAccessToken,   rawAuthorizationInformationResult,         expectedResult
                Arguments.of( applicationClientDetails,   token,   true,            rawAuthorizationInformationAccessToken,    rawAuthorizationInformationAccessToken ),
                Arguments.of( applicationClientDetails,   token,   false,           rawAuthorizationInformationRefreshToken,   rawAuthorizationInformationRefreshToken )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRawAuthorizationInformationNoExceptionThrownTestCases")
    @DisplayName("getRawAuthorizationInformation: no exception thrown test cases")
    public void getRawAuthorizationInformationNoExceptionThrown_testCases(ApplicationClientDetails applicationClientDetails,
                                                                          String token,
                                                                          boolean isAccessToken,
                                                                          Map<String, Object> rawAuthorizationInformationResult,
                                                                          Map<String, Object> expectedResult) {
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(token)))
                .thenReturn(
                        rawAuthorizationInformationResult
                );

        assertEquals(
                expectedResult,
                service.getRawAuthorizationInformation(applicationClientDetails, token, isAccessToken)
        );

        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(token)
                );
    }


    @Test
    @DisplayName("getUsername: when applicationClientDetails is null then IllegalArgumentException is thrown")
    public void getUsername_whenApplicationClientDetailsIsNull_thenIllegalArgumentExceptionIsThrown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getUsername(
                        null,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("getUsername: when applicationClientDetails is not found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void getUsername_whenApplicationClientIdIsNotFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("NotFound");

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.getUsername(
                        applicationClientDetails,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("getUsername: when no ApplicationClientAuthorizationService is found related with SecurityHandler then BeansException is thrown")
    public void getUsername_whenNoApplicationClientAuthorizationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.getUsername(
                        applicationClientDetails,
                        new HashMap<>()
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
    }


    @Test
    @DisplayName("getUsername: when rawAuthorizationInformation does not contain username value then UsernameNotFoundException is thrown")
    public void getUsername_whenRawAuthorizationInformationDoesNotContainUsernameValue_thenUsernameNotFoundExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        Map<String, Object> rawAuthorizationInformation = new HashMap<>();

        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockAuthorizationService.getUsername(eq(rawAuthorizationInformation)))
                .thenReturn(
                        empty()
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.getUsername(
                        applicationClientDetails,
                        rawAuthorizationInformation
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(rawAuthorizationInformation)
                );
    }


    static Stream<Arguments> getUsernameNoExceptionThrownTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        Map<String, Object> rawAuthorizationInformation = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            applicationClientDetails,   rawAuthorizationInformation,   expectedResult
                Arguments.of( applicationClientDetails,   rawAuthorizationInformation,   "username value" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getUsernameNoExceptionThrownTestCases")
    @DisplayName("getUsername: no exception thrown test cases")
    public void getUsernameNoExceptionThrown_testCases(ApplicationClientDetails applicationClientDetails,
                                                       Map<String, Object> rawAuthorizationInformation,
                                                       String expectedResult) {
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockAuthorizationService.getUsername(eq(rawAuthorizationInformation)))
                .thenReturn(
                        ofNullable(expectedResult)
                );

        assertEquals(
                expectedResult,
                service.getUsername(applicationClientDetails, rawAuthorizationInformation)
        );

        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(rawAuthorizationInformation)
                );
    }

}
