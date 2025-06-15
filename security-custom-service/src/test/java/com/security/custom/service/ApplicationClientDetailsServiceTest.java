package com.security.custom.service;

import com.security.custom.enums.token.TokenType;
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
import org.mockito.verification.VerificationMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWE;
import static com.security.custom.TestDataFactory.buildApplicationClientDetailsJWS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
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


    static Stream<Arguments> findByIdThrowingExceptionTestCases() {
        ApplicationClientDetails invalidApplicationClientDetails = buildApplicationClientDetailsJWE("JWE");
        invalidApplicationClientDetails.setTokenType(TokenType.JWS);
        return Stream.of(
                //@formatter:off
                //            id,                      repositoryResult,                      cacheServiceResult,                expectedException,
                Arguments.of( null,                    empty(),                               null,                              ApplicationClientNotFoundException.class ),
                Arguments.of( "NotFound",              empty(),                               null,                              ApplicationClientNotFoundException.class ),
                Arguments.of( "FoundOnlyInDatabase",   of(invalidApplicationClientDetails),   null,                              UnsupportedOperationException.class ),
                Arguments.of( "FoundInCache",          empty(),                               invalidApplicationClientDetails,   UnsupportedOperationException.class )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdThrowingExceptionTestCases")
    @DisplayName("findById: throwing exception test cases")
    public void findByIdThrowingException_testCases(String id,
                                                    Optional<ApplicationClientDetails> repositoryResult,
                                                    ApplicationClientDetails cacheServiceResult,
                                                    Class<? extends Exception> expectedException) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);
        when(mockCacheService.get(eq(id)))
                .thenReturn(ofNullable(cacheServiceResult));

        assertThrows(
                expectedException,
                () -> service.findById(id)
        );

        findByIdVerifyInvocations(
                id,
                repositoryResult,
                cacheServiceResult
        );
    }


    static Stream<Arguments> findByIdValidTestCases() {
        ApplicationClientDetails applicationClientDetailsJWS = buildApplicationClientDetailsJWS("ItDoesNotCare");
        ApplicationClientDetails applicationClientDetailsJWE = buildApplicationClientDetailsJWE("ItDoesNotCare");
        return Stream.of(
                //@formatter:off
                //            id,                      repositoryResult,                  cacheServiceResult,            expectedResult
                Arguments.of( "FoundOnlyInDatabase",   of(applicationClientDetailsJWS),   null,                          applicationClientDetailsJWS ),
                Arguments.of( "FoundInCache",          empty(),                           applicationClientDetailsJWE,   applicationClientDetailsJWE )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdValidTestCases")
    @DisplayName("findById: valid test cases")
    public void findByIdValid_testCases(String id,
                                        Optional<ApplicationClientDetails> repositoryResult,
                                        ApplicationClientDetails cacheServiceResult,
                                        ApplicationClientDetails expectedResult) {

        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);
        when(mockCacheService.get(eq(id)))
                .thenReturn(ofNullable(cacheServiceResult));

        assertEquals(
                expectedResult,
                service.findById(id)
        );

        findByIdVerifyInvocations(
                id,
                repositoryResult,
                cacheServiceResult
        );
    }


    static Stream<Arguments> findByUsernameThrowingExceptionTestCases() {
        ApplicationClientDetails invalidApplicationClientDetails = buildApplicationClientDetailsJWS("JWS");
        invalidApplicationClientDetails.setTokenType(TokenType.JWE);
        return Stream.of(
                //@formatter:off
                //            id,           repositoryResult,                      expectedException
                Arguments.of( null,         empty(),                               ApplicationClientNotFoundException.class ),
                Arguments.of( "NotFound",   empty(),                               ApplicationClientNotFoundException.class ),
                Arguments.of( "Found",      of(invalidApplicationClientDetails),   UnsupportedOperationException.class )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByUsernameThrowingExceptionTestCases")
    @DisplayName("findByUsername: throwing exception test cases")
    public void findByUsernameThrowingException_testCases(String id,
                                                          Optional<ApplicationClientDetails> repositoryResult,
                                                          Class<? extends Exception> expectedException) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);

        assertThrows(
                expectedException,
                () -> service.findByUsername(id)
        );

        VerificationMode times = null == id
                ? never()
                : times(1);

        verify(mockRepository, times)
                .findById(eq(id));
    }


    static Stream<Arguments> findByUsernameValidTestCases() {
        ApplicationClientDetails applicationClientDetails = buildApplicationClientDetailsJWE("ItDoesNotCare");
        return Stream.of(
                //@formatter:off
                //            id,        repositoryResult,               expectedResult
                Arguments.of( "Found",   of(applicationClientDetails),   applicationClientDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByUsernameValidTestCases")
    @DisplayName("findByUsername: valid test cases")
    public void findByUsernameValid_testCases(String id,
                                              Optional<ApplicationClientDetails> repositoryResult,
                                              ApplicationClientDetails expectedResult) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);

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


    private void findByIdVerifyInvocations(final String id,
                                           final Optional<ApplicationClientDetails> repositoryResult,
                                           final ApplicationClientDetails cacheServiceResult) {
        // No id value provided
        if (null == id) {
            verify(mockRepository, times(0))
                    .findById(eq(id));
            verify(mockCacheService, times(0))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Found ApplicationClientDetails only in database
        }
        else if (repositoryResult.isPresent() && null == cacheServiceResult) {
            verify(mockRepository, times(1))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(1))
                    .put(eq(id), eq(repositoryResult.get()));

        // Found ApplicationClientDetails in cache
        }
        else if (null != cacheServiceResult) {
            verify(mockRepository, times(0))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Not found ApplicationClientDetails neither in cache nor database
        }
        else if (repositoryResult.isEmpty() && null == cacheServiceResult) {
            verify(mockRepository, times(1))
                    .findById(eq(id));
            verify(mockCacheService, times(1))
                    .get(eq(id));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        }
        else {
            throw new RuntimeException("Not well managed use case");
        }
    }

}
