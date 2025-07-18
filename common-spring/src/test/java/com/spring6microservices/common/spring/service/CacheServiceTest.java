package com.spring6microservices.common.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CacheServiceTest {

    @Mock
    private CacheManager mockCacheManager;


    private CacheService service;


    @BeforeEach
    public void init() {
        service = new CacheService(mockCacheManager);
    }


    static Stream<Arguments> clearTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        return Stream.of(
                //@formatter:off
                //            cacheName,         cacheManagerResult,   expectedResult
                Arguments.of( null,              null,                 false ),
                Arguments.of( null,              mockCache,            false ),
                Arguments.of( "NotFoundCache",   null,                 false ),
                Arguments.of( "FoundCache",      mockCache,            true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("clearTestCases")
    @DisplayName("clear: test cases")
    public void clear_testCases(String cacheName,
                                Cache cacheManagerResult,
                                boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            doNothing()
                    .when(cacheManagerResult)
                    .clear();
        }

        boolean result = service.clear(cacheName);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> containsTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,                 null,            false ),
                Arguments.of( null,              "ItDoesNotCare",   mockCache,            returnedValue,   false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   null,                 null,            false ),
                Arguments.of( "FoundCache",      "NotFoundKey",     mockCache,            null,            false ),
                Arguments.of( "FoundCache",      "FoundKey",        mockCache,            returnedValue,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String cacheName,
                                   String key,
                                   Cache cacheManagerResult,
                                   SimpleValueWrapper cacheResult,
                                   boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            when(cacheManagerResult.get(key))
                    .thenReturn(
                            cacheResult
                    );
        }

        boolean result = service.contains(cacheName, key);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> getTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,                 null,            empty() ),
                Arguments.of( null,              "ItDoesNotCare",   mockCache,            returnedValue,   empty() ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   null,                 null,            empty() ),
                Arguments.of( "FoundCache",      "NotFoundKey",     mockCache,            null,            empty() ),
                Arguments.of( "FoundCache",      "FoundKey",        mockCache,            returnedValue,   of(returnedValue.get()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public void get_testCases(String cacheName,
                              String key,
                              Cache cacheManagerResult,
                              SimpleValueWrapper cacheResult,
                              Optional<String> expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            when(cacheManagerResult.get(key))
                    .thenReturn(
                            cacheResult
                    );
        }

        Optional<String> result = service.get(cacheName, key);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> getCacheTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        return Stream.of(
                //@formatter:off
                //            cacheName,         cacheManagerResult,   expectedResult
                Arguments.of( null,              null,                 empty() ),
                Arguments.of( "NotFoundCache",   null,                 empty() ),
                Arguments.of( "FoundCache",      mockCache,            of(mockCache) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getCacheTestCases")
    @DisplayName("getCache: test cases")
    public void getCache_testCases(String cacheName,
                                   Cache cacheManagerResult,
                                   Optional<Cache> expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );

        Optional<Cache> result = service.getCache(cacheName);

        assertEquals(
                expectedResult.isPresent(),
                result.isPresent()
        );
        result.ifPresent(
                cache ->
                        assertEquals(
                                expectedResult.get(),
                                cache
                        )
        );
    }


    static Stream<Arguments> putTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               value,             cacheManagerResult,    expectedResult
                Arguments.of( null,              null,              null,              null,                  false ),
                Arguments.of( null,              "ItDoesNotCare",   "ItDoesNotCare",   mockCache,             false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   "ItDoesNotCare",   null,                  false ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String cacheName,
                              String key,
                              String value,
                              Cache cacheManagerResult,
                              boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            doNothing()
                    .when(cacheManagerResult)
                    .put(
                            key,
                            value
                    );
        }

        boolean result = service.put(cacheName, key, value);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> putIfAbsentTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               value,             cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,              null,                 null,            false ),
                Arguments.of( null,              "ItDoesNotCare",   "ItDoesNotCare",   mockCache,            null,            false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   "ItDoesNotCare",   null,                 null,            false ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,            null,            true ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,            returnedValue,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putIfAbsentTestCases")
    @DisplayName("putIfAbsent: test cases")
    public void putIfAbsent_testCases(String cacheName,
                                      String key,
                                      String value,
                                      Cache cacheManagerResult,
                                      SimpleValueWrapper cacheResult,
                                      boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            when(cacheManagerResult.get(key))
                    .thenReturn(
                            cacheResult
                    );
            doNothing()
                    .when(cacheManagerResult)
                    .put(
                            key,
                            value
                    );
        }

        boolean result = service.putIfAbsent(cacheName, key, value);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> putIfPresentTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               value,             cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,              null,                 null,            false ),
                Arguments.of( null,              "ItDoesNotCare",   "ItDoesNotCare",   mockCache,            null,            false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   "ItDoesNotCare",   null,                 null,            false ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,            null,            false ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,            returnedValue,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putIfPresentTestCases")
    @DisplayName("putIfPresent: test cases")
    public void putIfPresent_testCases(String cacheName,
                                       String key,
                                       String value,
                                       Cache cacheManagerResult,
                                       SimpleValueWrapper cacheResult,
                                       boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );
        if (null != cacheManagerResult) {
            when(cacheManagerResult.get(key))
                    .thenReturn(
                            cacheResult
                    );
            doNothing()
                    .when(cacheManagerResult)
                    .put(
                            key,
                            value
                    );
        }

        boolean result = service.putIfPresent(cacheName, key, value);

        assertEquals(
                expectedResult,
                result
        );
    }


    static Stream<Arguments> removeTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               cacheManagerResult,   expectedResult
                Arguments.of( null,              null,              null,                 false ),
                Arguments.of( null,              "ItDoesNotCare",   mockCache,            false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   null,                 false ),
                Arguments.of( "FoundCache",      "FoundKey",        mockCache,            true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeTestCases")
    @DisplayName("remove: test cases")
    public void remove_testCases(String cacheName,
                                 String key,
                                 Cache cacheManagerResult,
                                 boolean expectedResult) {
        when(mockCacheManager.getCache(cacheName))
                .thenReturn(
                        cacheManagerResult
                );

        boolean result = service.remove(cacheName, key);

        assertEquals(
                expectedResult,
                result
        );
    }

}
