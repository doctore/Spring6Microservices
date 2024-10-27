package com.security.custom.controller;

import com.security.custom.SecurityCustomServiceApplication;
import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.service.AuthorizationService;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.security.custom.TestDataFactory.buildAuthorizationInformationDto;
import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SecurityCustomServiceApplication.class)
public class AuthorizationControllerTest extends BaseControllerTest {

    @MockBean
    private AuthorizationService mockAuthorizationService;

    private WebTestClient webTestClient;


    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }


    @Test
    @SneakyThrows
    @DisplayName("checkToken: when no basic authentication is provided then unauthorized code is returned")
    public void checkToken_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.post()
                .uri(RestRoutes.AUTHORIZATION.ROOT + RestRoutes.AUTHORIZATION.CHECK_TOKEN)
                .body(
                        Mono.just("ItDoesNotCare"),
                        String.class
                )
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockAuthorizationService);
    }


    @Test
    @SneakyThrows
    @DisplayName("checkToken: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void checkToken_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of("accessToken: size must be between 1 and 2147483647")
        );

        webTestClient.post()
                .uri(RestRoutes.AUTHORIZATION.ROOT + RestRoutes.AUTHORIZATION.CHECK_TOKEN)
                .body(
                        Mono.just(""),
                        String.class
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockAuthorizationService);
    }


    @Test
    @DisplayName("checkToken: when given authentication request verifies the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void checkToken_whenParametersVerifyValidations_thenSuitableHttpCodeIsReturned() {
        String applicationClientId = "ItDoesNotCare";
        String accessToken = "accessToken";
        AuthorizationInformationDto authorizationInformation = buildAuthorizationInformationDto(
                "username",
                Set.of("admin"),
                new HashMap<>()
        );

        when(mockAuthorizationService.checkAccessToken(accessToken, applicationClientId))
                .thenReturn(authorizationInformation);

        webTestClient.post()
                .uri(RestRoutes.AUTHORIZATION.ROOT + RestRoutes.AUTHORIZATION.CHECK_TOKEN)
                .body(Mono.just(accessToken), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorizationInformationDto.class)
                .isEqualTo(authorizationInformation);

        verify(mockAuthorizationService, times(1))
                .checkAccessToken(
                        accessToken,
                        applicationClientId
                );
    }

}
