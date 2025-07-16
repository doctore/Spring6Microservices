package com.security.oauth.service.cache;

import com.security.oauth.configuration.cache.RegisteredClientCacheConfiguration;
import com.spring6microservices.common.spring.service.CacheService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.oauth.TestDataFactory.buildRegisteredClient;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class RegisteredClientCacheServiceTest {

    @Mock
    private RegisteredClientCacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private RegisteredClientCacheService service;


    @BeforeEach
    public void init() {
        service = new RegisteredClientCacheService(
                mockCacheConfiguration,
                mockCacheService
        );
        when(mockCacheConfiguration.getCacheName())
                .thenReturn(
                        "TestCache"
                );
    }


    static Stream<Arguments> clearTestCases() {
        return Stream.of(
                //@formatter:off
                //            cacheServiceResult,   expectedResult
                Arguments.of( false,                false ),
                Arguments.of( true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("clearTestCases")
    @DisplayName("clear: test cases")
    public void clear_testCases(boolean cacheServiceResult,
                                boolean expectedResult) {
        when(mockCacheService.clear(anyString()))
                .thenReturn(
                        cacheServiceResult
                );

        boolean result = service.clear();

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> containsTestCases() {
        String id = "123ABC";
        return Stream.of(
                //@formatter:off
                //            id,     cacheServiceResult,   expectedResult
                Arguments.of( null,   false,                false ),
                Arguments.of( id,     false,                false ),
                Arguments.of( id,     true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String id,
                                   boolean cacheServiceResult,
                                   boolean expectedResult) {
        when(mockCacheService.contains(anyString(), eq(id)))
                .thenReturn(
                        cacheServiceResult
                );

        boolean operationResult = service.contains(id);

        assertEquals(
                expectedResult,
                operationResult
        );
    }


    static Stream<Arguments> getTestCases() {
        String id = "Spring6Microservices";
        Optional<RegisteredClient> cacheServiceResult = of(
                buildRegisteredClient(
                        id,
                        id
                )
        );
        return Stream.of(
                //@formatter:off
                //            id,     cacheServiceResult,   expectedResult
                Arguments.of( null,   empty(),              empty() ),
                Arguments.of( id,     empty(),              empty() ),
                Arguments.of( id,     cacheServiceResult,   cacheServiceResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public void get_testCases(String id,
                              Optional<RegisteredClient> cacheServiceResult,
                              Optional<RegisteredClient> expectedResult) {
        when(mockCacheService.get(anyString(), eq(id)))
                .thenReturn(
                        (Optional)cacheServiceResult
                );

        Optional<RegisteredClient> operationResult = service.get(id);

        assertEquals(
                expectedResult,
                operationResult
        );
    }


    @Test
    @SneakyThrows
    @DisplayName("getCacheName: then return the name of the internal cache")
    public void getCacheName_thenReturnTheNameOfTheInternalCache() {
        String cacheName = "CacheNameTest";

        when(mockCacheConfiguration.getCacheName())
                .thenReturn(
                        cacheName
                );

        assertEquals(
                cacheName,
                service.getCacheName()
        );
    }


    static Stream<Arguments> putTestCases() {
        String id = "Spring6Microservices";
        RegisteredClient registeredClient = buildRegisteredClient(
                id,
                id
        );
        return Stream.of(
                //@formatter:off
                //            id,     registeredClient,   cacheServiceResult,   expectedResult
                Arguments.of( null,   null,               false,                false ),
                Arguments.of( id,     null,               false,                false ),
                Arguments.of( id,     null,               true,                 true ),
                Arguments.of( id,     registeredClient,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String id,
                              RegisteredClient registeredClient,
                              boolean cacheServiceResult,
                              boolean expectedResult) {
        when(mockCacheService.put(anyString(), eq(id), eq(registeredClient)))
                .thenReturn(
                        cacheServiceResult
                );

        boolean operationResult = service.put(id, registeredClient);

        assertEquals(
                expectedResult,
                operationResult
        );
    }

}
