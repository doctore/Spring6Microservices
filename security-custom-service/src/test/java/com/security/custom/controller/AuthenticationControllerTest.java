package com.security.custom.controller;

import com.security.custom.SecurityCustomServiceApplication;
import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.dto.AuthenticationRequestLoginDto;
import com.security.custom.dto.AuthenticationRequestLoginTokenDto;
import com.security.custom.service.AuthenticationService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationAuthorizationCodeDto;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.enums.HashAlgorithm;
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

import static com.security.custom.TestDataFactory.*;
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
        AuthenticationRequestLoginDto authenticationRequest = buildAuthenticationRequestLoginDto("usernameValue", "passwordValue");

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequestLoginDto.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> login_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(150, "a"));

        AuthenticationRequestLoginDto nullUsernameRequest = buildAuthenticationRequestLoginDto(null, "passwordValue");
        AuthenticationRequestLoginDto nullPasswordRequest = buildAuthenticationRequestLoginDto("usernameValue", null);
        AuthenticationRequestLoginDto notValidUsernameRequest = buildAuthenticationRequestLoginDto(longString, "passwordValue");
        AuthenticationRequestLoginDto notValidPasswordRequest = buildAuthenticationRequestLoginDto("usernameValue", longString);

        String nullUsernameRequestError = "Field error in object 'authenticationRequestLoginDto' on field 'username' due to: must not be null";
        String nullPasswordRequestError = "Field error in object 'authenticationRequestLoginDto' on field 'password' due to: must not be null";
        String notValidUsernameRequestError = "Field error in object 'authenticationRequestLoginDto' on field 'username' due to: size must be between 1 and 64";
        String notValidPasswordRequestError = "Field error in object 'authenticationRequestLoginDto' on field 'password' due to: size must be between 1 and 128";
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
    public void login_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestLoginDto invalidAuthenticationRequestDto,
                                                                                                                     String expectedErrors) {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of(expectedErrors)
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(invalidAuthenticationRequestDto),
                        AuthenticationRequestLoginDto.class
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
        AuthenticationRequestLoginDto authenticationRequestDto = buildAuthenticationRequestLoginDto("usernameValue", "passwordValue");

        when(mockAuthenticationService.login(applicationClientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword()))
                .thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
                .body(
                        Mono.just(authenticationRequestDto),
                        AuthenticationRequestLoginDto.class
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
    @DisplayName("loginAuthorized: when no basic authentication is provided then unauthorized code is returned")
    public void loginAuthorized_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        AuthenticationRequestLoginAuthorizedDto authenticationRequest = buildAuthenticationRequestLoginAuthorizedDto(
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_AUTHORIZED)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequestLoginAuthorizedDto.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> loginAuthorized_invalidParametersTestCases() {
        AuthenticationRequestLoginAuthorizedDto nullChallengeRequest = buildAuthenticationRequestLoginAuthorizedDto(
                null,
                HashAlgorithm.SHA_384.getAlgorithm()
        );
        AuthenticationRequestLoginAuthorizedDto emptyChallengeRequest = buildAuthenticationRequestLoginAuthorizedDto(
                "",
                HashAlgorithm.SHA_384.getAlgorithm()
        );
        AuthenticationRequestLoginAuthorizedDto notFoundChallengeMethodRequest = buildAuthenticationRequestLoginAuthorizedDto(
                "NotFound"
        );

        String nullChallengeRequestError = "Field error in object 'authenticationRequestLoginAuthorizedDto' on field 'challenge' due to: must not be null";
        String emptyChallengeRequestError = "Field error in object 'authenticationRequestLoginAuthorizedDto' on field 'challenge' due to: size must be between 1 and 2147483647";
        String notFoundChallengeMethodRequestError = "Field error in object 'authenticationRequestLoginAuthorizedDto' on field 'challengeMethod' due to: must be one of the values included in [MD5, SHA-256, SHA-384, SHA-512, SHA3-256, SHA3-384, SHA3-512]";
        return Stream.of(
                //@formatter:off
                //            invalidAuthenticationRequestDto,   expectedError
                Arguments.of( nullChallengeRequest,              nullChallengeRequestError    ),
                Arguments.of( emptyChallengeRequest,             emptyChallengeRequestError    ),
                Arguments.of( notFoundChallengeMethodRequest,    notFoundChallengeMethodRequestError    )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("loginAuthorized_invalidParametersTestCases")
    @DisplayName("loginAuthorized: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void loginAuthorized_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestLoginAuthorizedDto invalidAuthenticationRequestDto,
                                                                                                                               String expectedErrors) {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of(expectedErrors)
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_AUTHORIZED)
                .body(
                        Mono.just(invalidAuthenticationRequestDto),
                        AuthenticationRequestLoginAuthorizedDto.class
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> loginAuthorized_validParametersTestCases() {
        AuthenticationInformationAuthorizationCodeDto authenticationInformation = buildAuthenticationInformationAuthorizationCodeDto(
                "authorizationCodeValue"
        );
        return Stream.of(
                //@formatter:off
                //            AuthenticationServiceResult,     expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                         UNPROCESSABLE_ENTITY,     null ),
                Arguments.of( of(authenticationInformation),   OK,                       authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("loginAuthorized_validParametersTestCases")
    @DisplayName("loginAuthorized: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void loginAuthorized_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationAuthorizationCodeDto> authenticationInformation,
                                                                                                    HttpStatus expectedResultHttpCode,
                                                                                                    AuthenticationInformationAuthorizationCodeDto expectedBodyResult) {
        String applicationClientId = "ItDoesNotCare";
        AuthenticationRequestLoginAuthorizedDto authenticationRequestDto = buildAuthenticationRequestLoginAuthorizedDto(
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        when(mockAuthenticationService.loginAuthorized(applicationClientId, authenticationRequestDto))
                .thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_AUTHORIZED)
                .body(
                        Mono.just(authenticationRequestDto),
                        AuthenticationRequestLoginAuthorizedDto.class
                )
                .exchange();

        response.expectStatus().isEqualTo(expectedResultHttpCode);
        if (null == expectedBodyResult) {
            response.expectBody().isEmpty();
        }
        else {
            response.expectBody(AuthenticationInformationAuthorizationCodeDto.class)
                    .isEqualTo(expectedBodyResult);
        }
        verify(mockAuthenticationService, times(1))
                .loginAuthorized(
                        applicationClientId,
                        authenticationRequestDto
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("loginToken: when no basic authentication is provided then unauthorized code is returned")
    public void loginToken_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        AuthenticationRequestLoginTokenDto authenticationRequest = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                "authorizationCodeValue",
                "verifierValue"
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_TOKEN)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequestLoginTokenDto.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> loginToken_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(150, "a"));

        AuthenticationRequestLoginTokenDto nullUsername = buildAuthenticationRequestLoginTokenDto(
                null,
                "passwordValue",
                "authorizationCodeValue",
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto notValidUsername = buildAuthenticationRequestLoginTokenDto(
                longString,
                "passwordValue",
                "authorizationCodeValue",
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto nullPassword = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                null,
                "authorizationCodeValue",
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto notValidPassword = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                longString,
                "authorizationCodeValue",
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto nullAuthorizationCode = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                null,
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto emptyAuthorizationCode = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                "",
                "verifierValue"
        );
        AuthenticationRequestLoginTokenDto nullVerifier = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                "authorizationCodeValue",
                null
        );
        AuthenticationRequestLoginTokenDto emptyVerifier = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                "authorizationCodeValue",
                ""
        );
        String nullUsernameRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'username' due to: must not be null";
        String notValidUsernameRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'username' due to: size must be between 1 and 64";
        String nullPasswordRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'password' due to: must not be null";
        String notValidPasswordRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'password' due to: size must be between 1 and 128";
        String nullAuthorizationCodeRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'authorizationCode' due to: must not be null";
        String emptyAuthorizationCodeRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'authorizationCode' due to: size must be between 1 and 2147483647";
        String nullVerifierRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'verifier' due to: must not be null";
        String emptyVerifierRequestError = "Field error in object 'authenticationRequestLoginTokenDto' on field 'verifier' due to: size must be between 1 and 2147483647";
        return Stream.of(
                //@formatter:off
                //            invalidAuthenticationRequestDto,   expectedError
                Arguments.of( nullUsername,                      nullUsernameRequestError ),
                Arguments.of( notValidUsername,                  notValidUsernameRequestError ),
                Arguments.of( nullPassword,                      nullPasswordRequestError ),
                Arguments.of( notValidPassword,                  notValidPasswordRequestError ),
                Arguments.of( nullAuthorizationCode,             nullAuthorizationCodeRequestError ),
                Arguments.of( emptyAuthorizationCode,            emptyAuthorizationCodeRequestError ),
                Arguments.of( nullVerifier,                      nullVerifierRequestError ),
                Arguments.of( emptyVerifier,                     emptyVerifierRequestError )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("loginToken_invalidParametersTestCases")
    @DisplayName("loginToken: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void loginToken_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestLoginTokenDto invalidAuthenticationRequestDto,
                                                                                                                          String expectedErrors) {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of(expectedErrors)
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_TOKEN)
                .body(
                        Mono.just(invalidAuthenticationRequestDto),
                        AuthenticationRequestLoginTokenDto.class
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockAuthenticationService);
    }


    static Stream<Arguments> loginToken_validParametersTestCases() {
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
    @MethodSource("loginToken_validParametersTestCases")
    @DisplayName("loginToken: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void loginToken_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                               HttpStatus expectedResultHttpCode,
                                                                                               AuthenticationInformationDto expectedBodyResult) {
        String applicationClientId = "ItDoesNotCare";
        AuthenticationRequestLoginTokenDto authenticationRequestDto = buildAuthenticationRequestLoginTokenDto(
                "usernameValue",
                "passwordValue",
                "authorizationCodeValue",
                "verifierValue"
        );

        when(mockAuthenticationService.loginToken(applicationClientId, authenticationRequestDto))
                .thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN_TOKEN)
                .body(
                        Mono.just(authenticationRequestDto),
                        AuthenticationRequestLoginTokenDto.class
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
                .loginToken(
                        applicationClientId,
                        authenticationRequestDto
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
