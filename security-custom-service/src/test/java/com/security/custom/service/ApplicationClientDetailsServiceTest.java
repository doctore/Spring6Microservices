package com.security.custom.service;

import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.repository.ApplicationClientDetailsRepository;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildDefaultApplicationClientDetails;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ApplicationClientDetailsServiceTest {

    @Mock
    private ApplicationClientDetailsCacheService mockCacheService;

    @Mock
    private ApplicationClientDetailsRepository mockRepository;

    private ApplicationClientDetailsService service;


    @BeforeEach
    public void init() {
        service = new ApplicationClientDetailsService(
                mockCacheService,
                mockRepository
        );
    }


    static Stream<Arguments> findByIdTestCases() {
        ApplicationClientDetails applicationClientDetails = buildDefaultApplicationClientDetails("ItDoesNotCare");
        return Stream.of(
                //@formatter:off
                //            id,                      repositoryResult,               cacheServiceResult,         expectedException,                          expectedResult
                Arguments.of( null,                    empty(),                        null,                       ApplicationClientNotFoundException.class,   null ),
                Arguments.of( "NotFound",              empty(),                        null,                       ApplicationClientNotFoundException.class,   null ),
                Arguments.of( "FoundOnlyInDatabase",   of(applicationClientDetails),   null,                       null,                                       applicationClientDetails ),
                Arguments.of( "FoundInCache",          empty(),                        applicationClientDetails,   null,                                       applicationClientDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(String id,
                                   Optional<ApplicationClientDetails> repositoryResult,
                                   ApplicationClientDetails cacheServiceResult,
                                   Class<? extends Exception> expectedException,
                                   ApplicationClientDetails expectedResult) {

        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);
        when(mockCacheService.get(eq(id)))
                .thenReturn(ofNullable(cacheServiceResult));

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.findById(id)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    service.findById(id)
            );
        }
        findByIdVerifyInvocations(
                id,
                repositoryResult,
                cacheServiceResult
        );
    }


    static Stream<Arguments> findByUsernameTestCases() {
        ApplicationClientDetails applicationClientDetails = buildDefaultApplicationClientDetails("ItDoesNotCare");
        return Stream.of(
                //@formatter:off
                //            id,                repositoryResult,       expectedException,               expectedResult
                Arguments.of( null,                    empty(),                ApplicationClientNotFoundException.class,   null ),
                Arguments.of( "NotFound",              empty(),                ApplicationClientNotFoundException.class,   null ),
                Arguments.of( "Found",                 of(applicationClientDetails),   null,                            applicationClientDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByUsernameTestCases")
    @DisplayName("findByUsername: test cases")
    public void findByUsername_testCases(String id,
                                         Optional<ApplicationClientDetails> repositoryResult,
                                         Class<? extends Exception> expectedException,
                                         ApplicationClientDetails expectedResult) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.findByUsername(id)
            );
        }
        else {
            Mono<UserDetails> result = service.findByUsername(id);
            StepVerifier.create(result)
                    .expectNextMatches(userDetails -> {
                        assertEquals(
                                expectedResult,
                                userDetails
                        );
                        verify(mockRepository, times(1))
                                .findById(eq(id));
                        verify(mockCacheService, times(1))
                                .get(eq(id));
                        return true;
                    })
                    .verifyComplete();
        }
    }


    private void findByIdVerifyInvocations(String id,
                                           Optional<ApplicationClientDetails> repositoryResult,
                                           ApplicationClientDetails cacheServiceResult) {
        // No id value provided
        if (null == id) {
            verify(mockRepository, times(0))
                    .findById(eq(id));
            verify(mockCacheService, times(0))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Found ApplicationClientDetails only in database
        } else if (repositoryResult.isPresent() && null == cacheServiceResult) {
            verify(mockRepository, times(1))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(1))
                    .put(eq(id), eq(repositoryResult.get()));

        // Found ApplicationClientDetails in cache
        } else if (null != cacheServiceResult) {
            verify(mockRepository, times(0))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Not found ApplicationClientDetails neither in cache nor database
        } else if (repositoryResult.isEmpty() && null == cacheServiceResult) {
            verify(mockRepository, times(1))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        } else {
            throw new RuntimeException("Not well managed use case");
        }
    }

}
