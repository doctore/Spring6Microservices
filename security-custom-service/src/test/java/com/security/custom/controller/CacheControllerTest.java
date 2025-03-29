package com.security.custom.controller;

import com.security.custom.SecurityCustomServiceApplication;
import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.ClearCacheRequestDto;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
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

import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildClearCacheRequestDto;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = SecurityCustomServiceApplication.class)
public class CacheControllerTest extends BaseControllerTest {

    @MockitoBean
    private ApplicationClientDetailsCacheService mockApplicationClientDetailsCacheService;

    @MockitoBean
    private AuthenticationRequestDetailsCacheService mockAuthenticationRequestDetailsCacheService;

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
        ClearCacheRequestDto clearNothingRequest = buildClearCacheRequestDto(
                false,
                false
        );
        ClearCacheRequestDto clearOnlyApplicationClientDetailsCacheRequest = buildClearCacheRequestDto(
                true,
                false
        );
        ClearCacheRequestDto clearOnlyAuthenticationRequestDetailsCacheRequest = buildClearCacheRequestDto(
                false,
                true
        );
        ClearCacheRequestDto clearEverythingRequest = buildClearCacheRequestDto(
                true,
                true
        );
        return Stream.of(
                //@formatter:off
                //            clearCacheRequestDto,                                cacheServiceResult,   expectedResultHttpCode
                Arguments.of( clearNothingRequest,                                 false,                OK ),
                Arguments.of( clearNothingRequest,                                 true,                 OK ),
                Arguments.of( clearOnlyApplicationClientDetailsCacheRequest,       false,                OK ),
                Arguments.of( clearOnlyApplicationClientDetailsCacheRequest,       true,                 OK ),
                Arguments.of( clearOnlyAuthenticationRequestDetailsCacheRequest,   false,                OK ),
                Arguments.of( clearOnlyAuthenticationRequestDetailsCacheRequest,   true,                 OK ),
                Arguments.of( clearEverythingRequest,                              false,                OK ),
                Arguments.of( clearEverythingRequest,                              true,                 OK )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("clear_validTestCases")
    @DisplayName("clear: when given basic authentication is given then the suitable Http code is returned")
    @WithMockUser
    public void clear_whenGivenBasicAuthIsGiven_thenSuitableHttpCodeIsReturned(ClearCacheRequestDto clearCacheRequestDto,
                                                                               boolean cacheServiceResult,
                                                                               HttpStatus expectedResultHttpCode) {
        when(mockApplicationClientDetailsCacheService.clear())
                .thenReturn(cacheServiceResult);

        when(mockAuthenticationRequestDetailsCacheService.clear())
                .thenReturn(cacheServiceResult);

        webTestClient.put()
                .uri(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR)
                .body(
                        Mono.just(clearCacheRequestDto),
                        ClearCacheRequestDto.class
                )
                .exchange()
                .expectStatus()
                .isEqualTo(expectedResultHttpCode);

        verifyCacheServiceClearInvocations(
                clearCacheRequestDto
        );
    }


    private void verifyCacheServiceClearInvocations(ClearCacheRequestDto clearCacheRequestDto) {
        int applicationClientDetailsCacheServiceInvocations = clearCacheRequestDto.isApplicationClientDetails()
                ? 1
                : 0;

        int authenticationRequestDetailsCacheService = clearCacheRequestDto.isAuthenticationRequestDetails()
                ? 1
                : 0;

        verify(mockApplicationClientDetailsCacheService, times(applicationClientDetailsCacheServiceInvocations))
                .clear();

        verify(mockAuthenticationRequestDetailsCacheService, times(authenticationRequestDetailsCacheService))
                .clear();
    }

}
