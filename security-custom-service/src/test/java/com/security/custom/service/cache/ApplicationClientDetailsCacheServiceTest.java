package com.security.custom.service.cache;

import com.security.custom.configuration.cache.ApplicationClientDetailsCacheConfiguration;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetails;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ApplicationClientDetailsCacheServiceTest {

    @Mock
    private ApplicationClientDetailsCacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private ApplicationClientDetailsCacheService service;


    @BeforeEach
    public void init() {
        service = new ApplicationClientDetailsCacheService(
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
                .thenReturn(cacheServiceResult);

        boolean operationResult = service.contains(id);

        assertEquals(
                expectedResult,
                operationResult
        );
    }


    static Stream<Arguments> getTestCases() {
        String id = "Spring6Microservices";
        Optional<ApplicationClientDetails> cacheServiceResult = of(
                buildApplicationClientDetails(id)
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
                              Optional<ApplicationClientDetails> cacheServiceResult,
                              Optional<ApplicationClientDetails> expectedResult) {
        when(mockCacheService.get(anyString(), eq(id)))
                .thenReturn((Optional)cacheServiceResult);
        Optional<ApplicationClientDetails> operationResult = service.get(id);

        assertEquals(
                expectedResult,
                operationResult
        );
    }


    static Stream<Arguments> putTestCases() {
        String id = "Spring6Microservices";
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetails(id);
        return Stream.of(
                //@formatter:off
                //            id,     applicationClientDetails,   cacheServiceResult,   expectedResult
                Arguments.of( null,   null,                       false,                false ),
                Arguments.of( id,     null,                       false,                false ),
                Arguments.of( id,     null,                       true,                 true ),
                Arguments.of( id,     applicationClientDetails,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String id,
                              ApplicationClientDetails applicationClientDetails,
                              boolean cacheServiceResult,
                              boolean expectedResult) {
        when(mockCacheService.put(anyString(), eq(id), eq(applicationClientDetails)))
                .thenReturn(cacheServiceResult);

        boolean operationResult = service.put(id, applicationClientDetails);

        assertEquals(
                expectedResult,
                operationResult
        );
    }

}