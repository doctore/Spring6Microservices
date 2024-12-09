package com.security.custom.controller;

import com.security.custom.SecurityCustomServiceApplication;
import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
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

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = SecurityCustomServiceApplication.class)
public class CacheControllerTest extends BaseControllerTest {

    @MockitoBean
    private ApplicationClientDetailsCacheService mockApplicationClientDetailsCacheService;

    private WebTestClient webTestClient;


    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }


    @Test
    @SneakyThrows
    @DisplayName("clear: when no basic authentication is provided then unauthorized code is returned")
    public void clear_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.put()
                .uri(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockApplicationClientDetailsCacheService);
    }


    static Stream<Arguments> clear_validTestCases() {
        return Stream.of(
                //@formatter:off
                //            ApplicationClientDetailsCacheServiceResult,   expectedResultHttpCode
                Arguments.of( false,                                        NOT_FOUND ),
                Arguments.of( true,                                         OK )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("clear_validTestCases")
    @DisplayName("clear: when given basic authentication is given then the suitable Http code is returned")
    @WithMockUser
    public void clear_whenGivenBasicAuthIsGiven_thenSuitableHttpCodeIsReturned(boolean cacheServiceResult,
                                                                               HttpStatus expectedResultHttpCode) {
        when(mockApplicationClientDetailsCacheService.clear())
                .thenReturn(cacheServiceResult);

        webTestClient.put()
                .uri(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR)
                .exchange()
                .expectStatus()
                .isEqualTo(expectedResultHttpCode);

        verify(mockApplicationClientDetailsCacheService, times(1))
                .clear();
    }

}
