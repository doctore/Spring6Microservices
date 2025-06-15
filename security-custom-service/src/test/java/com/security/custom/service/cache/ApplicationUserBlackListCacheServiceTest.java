package com.security.custom.service.cache;

import com.security.custom.configuration.cache.ApplicationUserBlackListCacheConfiguration;
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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class ApplicationUserBlackListCacheServiceTest {

    @Mock
    private ApplicationUserBlackListCacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private ApplicationUserBlackListCacheService service;


    @BeforeEach
    public void init() {
        service = new ApplicationUserBlackListCacheService(
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
        String applicationClientDetailsId = "123ABC";
        String username = "testUser";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult
                Arguments.of( null,                         null,       false,                false ),
                Arguments.of( applicationClientDetailsId,   null,       false,                false ),
                Arguments.of( null,                         username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String applicationClientDetailsId,
                                   String username,
                                   boolean cacheServiceResult,
                                   boolean expectedResult) {
        when(mockCacheService.contains(anyString(), anyString()))
                .thenReturn(cacheServiceResult);

        boolean result = service.contains(applicationClientDetailsId, username);

        assertEquals(
                expectedResult,
                result
        );

        if (null == applicationClientDetailsId || null == username) {
            verify(mockCacheService, times(0))
                    .contains(anyString(), anyString());
        }
        else {
            verify(mockCacheService, times(1))
                    .contains(anyString(), anyString());
        }
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
        String applicationClientDetailsId = "123ABC";
        String username = "testUser";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult
                Arguments.of( null,                         null,       false,                false ),
                Arguments.of( applicationClientDetailsId,   null,       false,                false ),
                Arguments.of( null,                         username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String applicationClientDetailsId,
                              String username,
                              boolean cacheServiceResult,
                              boolean expectedResult) {
        when(mockCacheService.put(anyString(), anyString(), eq(true)))
                .thenReturn(cacheServiceResult);

        boolean result = service.put(applicationClientDetailsId, username);

        assertEquals(
                expectedResult,
                result
        );

        if (null == applicationClientDetailsId || null == username) {
            verify(mockCacheService, times(0))
                    .put(anyString(), anyString(), eq(true));
        }
        else {
            verify(mockCacheService, times(1))
                    .put(anyString(), anyString(), eq(true));
        }
    }



    static Stream<Arguments> removeTestCases() {
        String applicationClientDetailsId = "123ABC";
        String username = "testUser";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult
                Arguments.of( null,                         null,       false,                false ),
                Arguments.of( applicationClientDetailsId,   null,       false,                false ),
                Arguments.of( null,                         username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeTestCases")
    @DisplayName("remove: test cases")
    public void remove_testCases(String applicationClientDetailsId,
                                 String username,
                                 boolean cacheServiceResult,
                                 boolean expectedResult) {
        when(mockCacheService.remove(anyString(), anyString()))
                .thenReturn(cacheServiceResult);

        boolean result = service.remove(applicationClientDetailsId, username);

        assertEquals(
                expectedResult,
                result
        );

        if (null == applicationClientDetailsId || null == username) {
            verify(mockCacheService, times(0))
                    .remove(anyString(), anyString());
        }
        else {
            verify(mockCacheService, times(1))
                    .remove(anyString(), anyString());
        }
    }

}
