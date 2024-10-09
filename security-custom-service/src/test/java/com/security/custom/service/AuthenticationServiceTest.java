package com.security.custom.service;

import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.application.spring6microservice.service.Spring6MicroserviceAuthenticationService;
import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetails;
import static com.security.custom.TestDataFactory.buildRawAuthenticationInformationDto;
import static com.security.custom.TestDataFactory.buildUser;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
    private EncryptorService mockEncryptorService;

    private AuthenticationService service;


    @BeforeEach
    public void init() {
        service = new AuthenticationService(
                mockApplicationContext,
                mockApplicationClientDetailsService,
                mockEncryptorService
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
    @DisplayName("login: when no ApplicationClientDetails is found in database then ApplicationClientNotFoundException is thrown")
    public void login_whenNoApplicationClientDetailsIsFoundInDatabase_thenApplicationClientNotFoundExceptionIsThrown() {
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
    @DisplayName("login: when no ApplicationClientAuthenticationService is found related with SecurityHandler then BeansException is thrown")
    public void login_whenNoApplicationClientAuthenticationServiceIsFoundRelatedWithSecurityHandler_thenBeansExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetails(applicationClientId)
                );
        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()))
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
                        ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()
                );
    }


    @Test
    @DisplayName("login: when a User with given username is not found in database then UsernameNotFoundException is thrown")
    public void login_whenAUserWithGivenUsernameIsNotFoundInDatabase_thenUsernameNotFoundExceptionIsThrown() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetails(applicationClientId)
                );
        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
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

        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()
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

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetails(applicationClientId)
                );
        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
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

        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()
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

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetails(applicationClientId)
                );
        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
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

        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()
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
        return Stream.of(
                //@formatter:off
                //            rawAuthenticationInformation,   isExpectedResultEmpty
                Arguments.of( empty(),                        true ),
                Arguments.of( rawAuthenticationInformation,   false )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("loginNoExceptionThrownTestCases")
    @DisplayName("login: no exception thrown test cases")
    public void loginNoExceptionThrown_testCases(Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                 boolean isExpectedResultEmpty) {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        String username = "username value";
        String password = "password value";
        Spring6MicroserviceAuthenticationService mockAuthenticationService = mock(Spring6MicroserviceAuthenticationService.class);
        User user = buildUser(username, password, true);

        when(mockApplicationClientDetailsService.findById(eq(applicationClientId)))
                .thenReturn(
                        buildApplicationClientDetails(applicationClientId)
                );
        when(mockApplicationContext.getBean(ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()))
                .thenReturn(
                        mockAuthenticationService
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
        when(mockEncryptorService.decrypt(anyString()))
                .then(
                        returnsFirstArg()
                );

        Optional<AuthenticationInformationDto> result = service.login(
                applicationClientId,
                username,
                password
        );

        if (isExpectedResultEmpty) {
            assertTrue(result.isEmpty());

            verify(mockEncryptorService, times(0))
                    .decrypt(
                            anyString()
                    );
        } else {
            assertTrue(result.isPresent());

            verify(mockEncryptorService, times(4))
                    .decrypt(
                            anyString()
                    );
        }

        verify(mockApplicationClientDetailsService, times(1))
                .findById(
                        eq(applicationClientId)
                );
        verify(mockApplicationContext, times(1))
                .getBean(
                        ArgumentMatchers.<Class<ApplicationClientAuthenticationService>>any()
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
    }

}
