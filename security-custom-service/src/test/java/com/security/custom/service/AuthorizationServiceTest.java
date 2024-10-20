package com.security.custom.service;

import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthorizationService;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.exception.token.TokenException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildAuthorizationInformationDto;
import static com.security.custom.enums.token.TokenKey.AUTHORITIES;
import static com.security.custom.enums.token.TokenKey.USERNAME;
import static java.util.Optional.of;
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
    private ApplicationClientDetailsService mockApplicationClientDetailsService;

    @Mock
    private TokenService mockTokenService;

    private AuthorizationService service;


    @BeforeEach
    public void init() {
        service = new AuthorizationService(
                mockApplicationContext,
                mockApplicationClientDetailsService,
                mockTokenService
        );
    }


    @Test
    @DisplayName("checkAccessToken: when no applicationClientId is found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void checkAccessToken_whenNoApplicationClientIdIsFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = "NotFound";
        String accessToken = "ItDoesNotCare";

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthorizationService.class)
                );
        verify(mockApplicationClientDetailsService, times(0))
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("checkAccessToken: when no ApplicationClientAuthenticationService is found related with SecurityHandler then BeansException is thrown")
    public void checkAccessToken_whenNoApplicationClientAuthenticationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String accessToken = "ItDoesNotCare";

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
    }


    @Test
    @DisplayName("checkAccessToken: when no ApplicationClientDetails is found in database then ApplicationClientNotFoundException is thrown")
    public void checkAccessToken_whenNoApplicationClientDetailsIsFoundInDatabase_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String accessToken = "ItDoesNotCare";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenThrow(
                        ApplicationClientNotFoundException.class
                );

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
    }


    @Test
    @DisplayName("checkAccessToken: when there is a problem getting content of accessToken then TokenException is thrown")
    public void checkAccessToken_whenThereIsAProblemGettingContentOfAccessToken_thenTokenExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String accessToken = "NotValidToken";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mock(Spring6MicroserviceAuthorizationService.class)
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(accessToken)))
                .thenThrow(
                        TokenException.class
                );

        assertThrows(
                TokenException.class,
                () -> service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(accessToken)
                );
    }


    @Test
    @DisplayName("checkAccessToken: when accessToken does not contain username value then UsernameNotFoundException is thrown")
    public void checkAccessToken_whenAccessTokenDoesNotContainUsernameValue_thenUsernameNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String accessToken = "ItDoesNotCare";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);
        Map<String, Object> tokenPayload = new HashMap<>();

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(accessToken)))
                .thenReturn(
                        tokenPayload
                );
        when(mockTokenService.isPayloadRelatedWithAccessToken(eq(tokenPayload)))
                .thenReturn(
                        true
                );
        when(mockAuthorizationService.getUsername(eq(tokenPayload)))
                .thenThrow(
                        UsernameNotFoundException.class
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(accessToken)
                );
        verify(mockTokenService, times(1))
                .isPayloadRelatedWithAccessToken(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(tokenPayload)
                );
    }


    @Test
    @DisplayName("checkAccessToken: no exception thrown test cases")
    public void checkAccessTokenNoExceptionThrown_testCases() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String accessToken = "ItDoesNotCare";
        String username = "username value";
        Set<String> authorities = Set.of(
                RoleEnum.ROLE_ADMIN.name()
        );
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(applicationClientId);
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);
        Map<String, Object> tokenPayload = new HashMap<>() {{
            put(USERNAME.getKey(), username);
            put(AUTHORITIES.getKey(), authorities);
        }};
        AuthorizationInformationDto authorizationInformation = buildAuthorizationInformationDto(
                username,
                authorities,
                new HashMap<>()
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        applicationClientDetails
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(accessToken)))
                .thenReturn(
                        tokenPayload
                );
        when(mockTokenService.isPayloadRelatedWithAccessToken(eq(tokenPayload)))
                .thenReturn(
                        true
                );
        when(mockAuthorizationService.getUsername(eq(tokenPayload)))
                .thenReturn(
                        of(username)
                );
        when(mockAuthorizationService.getAuthorities(eq(tokenPayload)))
                .thenReturn(
                        authorities
                );
        when(mockAuthorizationService.getAdditionalAuthorizationInformation(eq(tokenPayload)))
                .thenReturn(
                        new HashMap<>()
                );

        assertEquals(
                authorizationInformation,
                service.checkAccessToken(
                        applicationClientId,
                        accessToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(accessToken)
                );
        verify(mockTokenService, times(1))
                .isPayloadRelatedWithAccessToken(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getAuthorities(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getAdditionalAuthorizationInformation(
                        eq(tokenPayload)
                );
    }


    @Test
    @DisplayName("checkRefreshToken: when applicationClientDetails is null then IllegalArgumentException is thrown")
    public void checkRefreshToken_whenApplicationClientIsNull_thenIllegalArgumentExceptionIsThrown() {
        String refreshToken = "ItDoesNotCare";

        assertThrows(
                IllegalArgumentException.class,
                () -> service.checkRefreshToken(
                        null,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthorizationService.class)
                );
    }


    @Test
    @DisplayName("checkRefreshToken: when no applicationClientId is found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void checkRefreshToken_whenNoApplicationClientIdIsFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("NotFound");
        String refreshToken = "ItDoesNotCare";

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.checkRefreshToken(
                        applicationClientDetails,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthorizationService.class)
                );
        verify(mockApplicationClientDetailsService, times(0))
                .findById(
                        eq(applicationClientDetails.getId())
                );
    }


    @Test
    @DisplayName("checkRefreshToken: when no ApplicationClientAuthenticationService is found related with SecurityHandler then BeansException is thrown")
    public void checkRefreshToken_whenNoApplicationClientAuthenticationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        String refreshToken = "ItDoesNotCare";

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenThrow(
                        NoSuchBeanDefinitionException.class
                );

        assertThrows(
                BeansException.class,
                () -> service.checkRefreshToken(
                        applicationClientDetails,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
    }


    @Test
    @DisplayName("checkRefreshToken: when there is a problem getting content of refreshToken then TokenException is thrown")
    public void checkRefreshToken_whenThereIsAProblemGettingContentOfAccessToken_thenTokenExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        String refreshToken = "NotValidToken";

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mock(Spring6MicroserviceAuthorizationService.class)
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenThrow(
                        TokenException.class
                );

        assertThrows(
                TokenException.class,
                () -> service.checkRefreshToken(
                        applicationClientDetails,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
    }


    @Test
    @DisplayName("checkRefreshToken: when refreshToken does not contain username value then UsernameNotFoundException is thrown")
    public void checkRefreshToken_whenRefreshTokenDoesNotContainUsernameValue_thenUsernameNotFoundExceptionIsThrown() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        String refreshToken = "ItDoesNotCare";
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);
        Map<String, Object> tokenPayload = new HashMap<>();

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenReturn(
                        tokenPayload
                );
        when(mockTokenService.isPayloadRelatedWithAccessToken(eq(tokenPayload)))
                .thenReturn(
                        false
                );
        when(mockAuthorizationService.getUsername(eq(tokenPayload)))
                .thenThrow(
                        UsernameNotFoundException.class
                );

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.checkRefreshToken(
                        applicationClientDetails,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
        verify(mockTokenService, times(1))
                .isPayloadRelatedWithAccessToken(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(tokenPayload)
                );
    }


    @Test
    @DisplayName("checkRefreshToken: no exception thrown test cases")
    public void checkRefreshTokenNoExceptionThrown_testCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE(
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
        );
        String refreshToken = "ItDoesNotCare";
        String username = "username value";
        Set<String> authorities = Set.of(
                RoleEnum.ROLE_ADMIN.name()
        );
        Spring6MicroserviceAuthorizationService mockAuthorizationService = mock(Spring6MicroserviceAuthorizationService.class);
        Map<String, Object> tokenPayload = new HashMap<>() {{
            put(USERNAME.getKey(), username);
            put(AUTHORITIES.getKey(), authorities);
        }};
        AuthorizationInformationDto authorizationInformation = buildAuthorizationInformationDto(
                username,
                authorities,
                new HashMap<>()
        );

        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()))
                .thenReturn(
                        mockAuthorizationService
                );
        when(mockTokenService.getPayloadOfToken(eq(applicationClientDetails), eq(refreshToken)))
                .thenReturn(
                        tokenPayload
                );
        when(mockTokenService.isPayloadRelatedWithAccessToken(eq(tokenPayload)))
                .thenReturn(
                        false
                );
        when(mockAuthorizationService.getUsername(eq(tokenPayload)))
                .thenReturn(
                        of(username)
                );
        when(mockAuthorizationService.getAuthorities(eq(tokenPayload)))
                .thenReturn(
                        authorities
                );
        when(mockAuthorizationService.getAdditionalAuthorizationInformation(eq(tokenPayload)))
                .thenReturn(
                        new HashMap<>()
                );

        assertEquals(
                authorizationInformation,
                service.checkRefreshToken(
                        applicationClientDetails,
                        refreshToken
                )
        );

        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthorizationService>>any()
                );
        verify(mockTokenService, times(1))
                .getPayloadOfToken(
                        eq(applicationClientDetails),
                        eq(refreshToken)
                );
        verify(mockTokenService, times(1))
                .isPayloadRelatedWithAccessToken(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getUsername(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getAuthorities(
                        eq(tokenPayload)
                );
        verify(mockAuthorizationService, times(1))
                .getAdditionalAuthorizationInformation(
                        eq(tokenPayload)
                );
    }

}
