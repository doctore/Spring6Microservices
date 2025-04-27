package com.security.custom.service.cache;

import com.security.custom.configuration.cache.AuthenticationRequestDetailsCacheConfiguration;
import com.security.custom.model.AuthenticationRequestDetails;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildAuthenticationRequestDetails;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationRequestDetailsCacheServiceTest {

    @Mock
    private AuthenticationRequestDetailsCacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private AuthenticationRequestDetailsCacheService service;


    @BeforeEach
    public void init() {
        service = new AuthenticationRequestDetailsCacheService(
                mockCacheConfiguration,
                mockCacheService
        );
        when(mockCacheConfiguration.getCacheName())
                .thenReturn("TestCache");
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
                .thenReturn(cacheServiceResult);

        boolean result = service.clear();

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> containsTestCases() {
        String authorizationCode = "123ABC";
        return Stream.of(
                //@formatter:off
                //            authorizationCode,   cacheServiceResult,   expectedResult
                Arguments.of( null,                false,                false ),
                Arguments.of( authorizationCode,   false,                false ),
                Arguments.of( authorizationCode,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String id,
                                   boolean cacheServiceResult,
                                   boolean expectedResult) {
        when(mockCacheService.contains(anyString(), eq(id)))
                .thenReturn(cacheServiceResult);

        boolean result = service.contains(id);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> getTestCases() {
        String authorizationCode = "123ABC";
        Optional<AuthenticationRequestDetails> cacheServiceResult = of(
                buildAuthenticationRequestDetails(authorizationCode)
        );
        return Stream.of(
                //@formatter:off
                //            authorizationCode,   cacheServiceResult,   expectedResult
                Arguments.of( null,                empty(),              empty() ),
                Arguments.of( authorizationCode,   empty(),              empty() ),
                Arguments.of( authorizationCode,   cacheServiceResult,   cacheServiceResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public void get_testCases(String authorizationCode,
                              Optional<AuthenticationRequestDetails> cacheServiceResult,
                              Optional<AuthenticationRequestDetails> expectedResult) {
        when(mockCacheService.get(anyString(), eq(authorizationCode)))
                .thenReturn((Optional)cacheServiceResult);

        Optional<AuthenticationRequestDetails> result = service.get(authorizationCode);

        assertEquals(
                expectedResult,
                result
        );
    }


    @Test
    @SneakyThrows
    @DisplayName("getCacheName: then return the name of the internal cache")
    public void getCacheName_thenReturnTheNameOfTheInternalCache() {
        String cacheName = "CacheNameTest";

        when(mockCacheConfiguration.getCacheName())
                .thenReturn(cacheName);

        assertEquals(
                cacheName,
                service.getCacheName()
        );
    }


    static Stream<Arguments> putTestCases() {
        String authorizationCode = "123ABC";
        AuthenticationRequestDetails authenticationRequestDetails = buildAuthenticationRequestDetails(authorizationCode);
        return Stream.of(
                //@formatter:off
                //            authorizationCode,   authenticationRequestDetails,   cacheServiceResult,   expectedResult
                Arguments.of( null,                null,                           false,                false ),
                Arguments.of( authorizationCode,   null,                           false,                false ),
                Arguments.of( authorizationCode,   null,                           true,                 true ),
                Arguments.of( authorizationCode,   authenticationRequestDetails,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String authorizationCode,
                              AuthenticationRequestDetails authenticationRequestDetails,
                              boolean cacheServiceResult,
                              boolean expectedResult) {
        when(mockCacheService.put(anyString(), eq(authorizationCode), eq(authenticationRequestDetails)))
                .thenReturn(cacheServiceResult);

        boolean result = service.put(authorizationCode, authenticationRequestDetails);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> removeTestCases() {
        return Stream.of(
                //@formatter:off
                //            authorizationCode,   cacheServiceResult,   expectedResult
                Arguments.of( "ItDoesBotCare",     false,                false ),
                Arguments.of( "ItDoesBotCare",     true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeTestCases")
    @DisplayName("remove: test cases")
    public void remove_testCases(String authorizationCode,
                                 boolean cacheServiceResult,
                                 boolean expectedResult) {
        when(mockCacheService.remove(anyString(), eq(authorizationCode)))
                .thenReturn(cacheServiceResult);

        boolean result = service.remove(authorizationCode);

        assertEquals(
                expectedResult,
                result
        );
    }

}
