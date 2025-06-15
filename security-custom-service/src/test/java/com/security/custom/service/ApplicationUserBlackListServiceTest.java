package com.security.custom.service;

import com.security.custom.service.cache.ApplicationUserBlackListCacheService;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class ApplicationUserBlackListServiceTest {

    @Mock
    private ApplicationUserBlackListCacheService mockCacheService;

    private ApplicationUserBlackListService service;


    @BeforeEach
    public void init() {
        service = new ApplicationUserBlackListService(
                mockCacheService
        );
    }


    static Stream<Arguments> containsTestCases() {
        String applicationClientDetailsId = "test applicationClientDetailsId";
        String username = "test username";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult,
                Arguments.of( null,                         null,       null,                 false ),
                Arguments.of( applicationClientDetailsId,   null,       null,                 false ),
                Arguments.of( null,                         username,   null,                 false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String applicationClientDetailsId,
                                   String username,
                                   Boolean cacheServiceResult,
                                   boolean expectedResult) {
        if (null != cacheServiceResult) {
            when(mockCacheService.contains(eq(applicationClientDetailsId), eq(username)))
                    .thenReturn(cacheServiceResult);
        }

        assertEquals(
                expectedResult,
                service.contains(applicationClientDetailsId, username)
        );

        VerificationMode timesInvokingCacheService = cacheServiceVerifyInvocations(
                applicationClientDetailsId,
                username
        );
        verify(mockCacheService, timesInvokingCacheService)
                .contains(
                        eq(applicationClientDetailsId),
                        eq(username)
                );
    }


    static Stream<Arguments> notBlackListedOrThrowTestCases() {
        String applicationClientDetailsId = "123ABC";
        String username = "testUser";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedException
                Arguments.of( null,                         null,       false,                null ),
                Arguments.of( applicationClientDetailsId,   null,       false,                null ),
                Arguments.of( null,                         username,   false,                null ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 UnauthorizedException.class ),
                Arguments.of( applicationClientDetailsId,   username,   false,                null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("notBlackListedOrThrowTestCases")
    @DisplayName("notBlackListedOrThrow: test cases")
    public void notBlackListedOrThrow_testCases(String applicationClientDetailsId,
                                                String username,
                                                boolean cacheServiceResult,
                                                Class<? extends Exception> expectedException) {
        when(mockCacheService.contains(anyString(), anyString()))
                .thenReturn(cacheServiceResult);

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.notBlackListedOrThrow(applicationClientDetailsId, username)
            );
        }
        else {
            assertDoesNotThrow(
                    () -> service.notBlackListedOrThrow(applicationClientDetailsId, username)
            );
        }
        if (null == applicationClientDetailsId || null == username) {
            verify(mockCacheService, times(0))
                    .contains(anyString(), anyString());
        }
        else {
            verify(mockCacheService, times(1))
                    .contains(anyString(), anyString());
        }
    }


    static Stream<Arguments> removeTestCases() {
        String applicationClientDetailsId = "test applicationClientDetailsId";
        String username = "test username";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult,
                Arguments.of( null,                         null,       null,                 false ),
                Arguments.of( applicationClientDetailsId,   null,       null,                 false ),
                Arguments.of( null,                         username,   null,                 false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeTestCases")
    @DisplayName("remove: test cases")
    public void remove_testCases(String applicationClientDetailsId,
                                 String username,
                                 Boolean cacheServiceResult,
                                 boolean expectedResult) {
        if (null != cacheServiceResult) {
            when(mockCacheService.remove(eq(applicationClientDetailsId), eq(username)))
                    .thenReturn(cacheServiceResult);
        }

        assertEquals(
                expectedResult,
                service.remove(applicationClientDetailsId, username)
        );

        VerificationMode timesInvokingCacheService = cacheServiceVerifyInvocations(
                applicationClientDetailsId,
                username
        );
        verify(mockCacheService, timesInvokingCacheService)
                .remove(
                        eq(applicationClientDetailsId),
                        eq(username)
                );
    }

    static Stream<Arguments> saveTestCases() {
        String applicationClientDetailsId = "test applicationClientDetailsId";
        String username = "test username";
        return Stream.of(
                //@formatter:off
                //            applicationClientDetailsId,   username,   cacheServiceResult,   expectedResult,
                Arguments.of( null,                         null,       null,                 false ),
                Arguments.of( applicationClientDetailsId,   null,       null,                 false ),
                Arguments.of( null,                         username,   null,                 false ),
                Arguments.of( applicationClientDetailsId,   username,   false,                false ),
                Arguments.of( applicationClientDetailsId,   username,   true,                 true )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(String applicationClientDetailsId,
                               String username,
                               Boolean cacheServiceResult,
                               boolean expectedResult) {
        if (null != cacheServiceResult) {
            when(mockCacheService.put(eq(applicationClientDetailsId), eq(username)))
                    .thenReturn(cacheServiceResult);
        }

        assertEquals(
                expectedResult,
                service.save(applicationClientDetailsId, username)
        );

        VerificationMode timesInvokingCacheService = cacheServiceVerifyInvocations(
                applicationClientDetailsId,
                username
        );
        verify(mockCacheService, timesInvokingCacheService)
                .put(
                        eq(applicationClientDetailsId),
                        eq(username)
                );
    }


    private VerificationMode cacheServiceVerifyInvocations(String applicationClientDetailsId,
                                                           String username) {
        return null == applicationClientDetailsId || null == username
                ? never()
                : times(1);
    }

}
