package com.security.custom.controller;

import com.security.custom.SecurityCustomServiceApplication;
import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.AuthenticationRequestCredentialsDto;
import com.security.custom.service.AuthenticationService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildAuthenticationInformationDto;
import static com.security.custom.TestDataFactory.buildAuthenticationRequestCredentials;
import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(classes = SecurityCustomServiceApplication.class)
public class AuthenticationControllerTest extends BaseControllerTest {

    @MockitoBean
    private AuthenticationService mockAuthenticationService;

    private WebTestClient webTestClient;


    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }


    @Test
    @SneakyThrows
    @DisplayName("login: when no basic authentication is provided then unauthorized code is returned")
    public void login_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        AuthenticationRequestCredentialsDto authenticationRequest = buildAuthenticationRequestCredentials("usernameValue", "passwordValue");

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequestCredentialsDto.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> login_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(150, "a"));

        AuthenticationRequestCredentialsDto nullUsernameRequest = buildAuthenticationRequestCredentials(null, "passwordValue");
        AuthenticationRequestCredentialsDto nullPasswordRequest = buildAuthenticationRequestCredentials("usernameValue", null);
        AuthenticationRequestCredentialsDto notValidUsernameRequest = buildAuthenticationRequestCredentials(longString, "passwordValue");
        AuthenticationRequestCredentialsDto notValidPasswordRequest = buildAuthenticationRequestCredentials("usernameValue", longString);

        String nullUsernameRequestError = "Field error in object 'authenticationRequestDto' on field 'username' due to: must not be null";
        String nullPasswordRequestError = "Field error in object 'authenticationRequestDto' on field 'password' due to: must not be null";
        String notValidUsernameRequestError = "Field error in object 'authenticationRequestDto' on field 'username' due to: size must be between 1 and 64";
        String notValidPasswordRequestError = "Field error in object 'authenticationRequestDto' on field 'password' due to: size must be between 1 and 128";
        return Stream.of(
                //@formatter:off
                //            invalidAuthenticationRequestDto,   expectedError
                Arguments.of( nullUsernameRequest,               nullUsernameRequestError ),
                Arguments.of( nullPasswordRequest,               nullPasswordRequestError ),
                Arguments.of( notValidUsernameRequest,           notValidUsernameRequestError    ),
                Arguments.of( notValidPasswordRequest,           notValidPasswordRequestError    )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("login_invalidParametersTestCases")
    @DisplayName("login: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void login_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestCredentialsDto invalidAuthenticationRequestDto,
                                                                                                                     String expectedErrors) {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of(expectedErrors)
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(invalidAuthenticationRequestDto),
                        AuthenticationRequestCredentialsDto.class
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> login_validParametersTestCases() {
        AuthenticationInformationDto authenticationInformation = buildAuthenticationInformationDto("test");
        return Stream.of(
                //@formatter:off
                //            AuthenticationServiceResult,     expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                         UNPROCESSABLE_ENTITY,     null ),
                Arguments.of( of(authenticationInformation),   OK,                       authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("login_validParametersTestCases")
    @DisplayName("login: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void login_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                          HttpStatus expectedResultHttpCode,
                                                                                          AuthenticationInformationDto expectedBodyResult) {
        String applicationClientId = "ItDoesNotCare";
        AuthenticationRequestCredentialsDto authenticationRequestDto = buildAuthenticationRequestCredentials("usernameValue", "passwordValue");

        when(mockAuthenticationService.login(applicationClientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword()))
                .thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(authenticationRequestDto),
                        AuthenticationRequestCredentialsDto.class
                )
                .exchange();

        response.expectStatus().isEqualTo(expectedResultHttpCode);
        if (null == expectedBodyResult) {
            response.expectBody().isEmpty();
        }
        else {
            response.expectBody(AuthenticationInformationDto.class)
                    .isEqualTo(expectedBodyResult);
        }
        verify(mockAuthenticationService, times(1))
                .login(
                        applicationClientId,
                        authenticationRequestDto.getUsername(),
                        authenticationRequestDto.getPassword()
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("refresh: when no basic authentication is provided then unauthorized code is returned")
    public void refresh_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.REFRESH)
                .body(
                        Mono.just("ItDoesNotCare"),
                        String.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthenticationService);
    }


    @Test
    @SneakyThrows
    @DisplayName("refresh: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void refresh_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of("refreshToken: size must be between 1 and 2147483647")
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.REFRESH)
                .body(
                        Mono.just(""),
                        String.class
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> refresh_validParametersTestCases() {
        AuthenticationInformationDto authenticationInformation = buildAuthenticationInformationDto("test");
        return Stream.of(
                //@formatter:off
                //            AuthenticationServiceResult,     expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                         UNAUTHORIZED,             null ),
                Arguments.of( of(authenticationInformation),   OK,                       authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("refresh_validParametersTestCases")
    @DisplayName("refresh: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void refresh_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                            HttpStatus expectedResultHttpCode,
                                                                                            AuthenticationInformationDto expectedBodyResult) {
        String applicationClientId = "ItDoesNotCare";
        String refreshToken = "refreshToken";

        when(mockAuthenticationService.refresh(applicationClientId, refreshToken))
                .thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.REFRESH)
                .body(
                        Mono.just(refreshToken),
                        String.class
                )
                .exchange();

        response.expectStatus().isEqualTo(expectedResultHttpCode);
        if (null == expectedBodyResult) {
            response.expectBody().isEmpty();
        }
        else {
            response.expectBody(AuthenticationInformationDto.class)
                    .isEqualTo(expectedBodyResult);
        }
        verify(mockAuthenticationService, times(1))
                .refresh(
                        applicationClientId,
                        refreshToken
                );
    }

}
