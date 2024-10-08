package com.security.custom.service;

import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.security.custom.TestDataFactory.buildDefaultApplicationClientDetails;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private ApplicationClientDetailsService mockApplicationClientDetailsService;

    @Mock
    private SecurityService mockSecurityService;

    private AuthenticationService service;


    @BeforeEach
    public void init() {
        service = new AuthenticationService(
                mockApplicationContext,
                mockApplicationClientDetailsService,
                mockSecurityService
        );
    }


    @Test
    @DisplayName("login: when no applicationClientId is found in SecurityHandler then ApplicationClientNotFoundException is thrown")
    public void login_whenNoApplicationClientIdIfFoundInSecurityHandler_thenApplicationClientNotFoundExceptionIsThrown() {
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
        verify(mockApplicationClientDetailsService, times(0))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("login: when no applicationClientId is found in database then ApplicationClientNotFoundException is thrown")
    public void login_whenNoApplicationClientIdIfFoundInDatabase_thenApplicationClientNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenThrow(ApplicationClientNotFoundException.class);

        assertThrows(
                ApplicationClientNotFoundException.class,
                () -> service.login(
                        applicationClientId,
                        username,
                        password
                )
        );
        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(0))
                .getBean(
                        eq(ApplicationClientAuthenticationService.class)
                );
    }


    @Test
    @DisplayName("login: when no ApplicationClientAuthenticationService is found related with in applicationClientId then BeansException is thrown")
    public void login_whenNoApplicationClientAuthenticationServiceIfFoundRelatedWithApplicationClientId_thenBeansExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildDefaultApplicationClientDetails(applicationClientId)
                );

        when(mockApplicationContext.getBean(any(Class.class)))
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

        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(1))
                .getBean(
                        any(Class.class)
                );
    }


}
