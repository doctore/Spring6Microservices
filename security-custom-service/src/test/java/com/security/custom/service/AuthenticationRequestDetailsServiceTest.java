package com.security.custom.service;

import com.security.custom.dto.AuthenticationRequestCredentialsAndChallengeDto;
import com.security.custom.enums.HashAlgorithm;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
import jakarta.validation.ConstraintViolationException;
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

import static com.security.custom.TestDataFactory.buildAuthenticationRequestCredentialsAndChallenge;
import static com.security.custom.TestDataFactory.buildAuthenticationRequestDetails;
import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthenticationRequestDetailsServiceTest {

    @Mock
    private AuthenticationRequestDetailsCacheService mockCacheService;

    @Mock
    private EncryptorService mockEncryptorService;

    private AuthenticationRequestDetailsService service;


    @BeforeEach
    public void init() {
        service = new AuthenticationRequestDetailsService(
                mockCacheService,
                mockEncryptorService
        );
    }


    static Stream<Arguments> findByAuthorizationCodeTestCases() {
        AuthenticationRequestDetails authenticationRequestDetails = buildAuthenticationRequestDetails("ItDoesNotCare");
        return Stream.of(
                //@formatter:off
                //            authorizationCode,   cacheServiceResult,             expectedException,                                     expectedResult
                Arguments.of( null,                null,                           AuthenticationRequestDetailsNotFoundException.class,   null ),
                Arguments.of( "NotFound",          null,                           AuthenticationRequestDetailsNotFoundException.class,   null ),
                Arguments.of( "FoundInCache",      authenticationRequestDetails,   null,                                                  authenticationRequestDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByAuthorizationCodeTestCases")
    @DisplayName("findByAuthorizationCode: test cases")
    public void findByAuthorizationCode_testCases(String authorizationCode,
                                                  AuthenticationRequestDetails cacheServiceResult,
                                                  Class<? extends Exception> expectedException,
                                                  AuthenticationRequestDetails expectedResult) {
        when(mockCacheService.get(eq(authorizationCode)))
                .thenReturn(ofNullable(cacheServiceResult));

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.findByAuthorizationCode(authorizationCode)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    service.findByAuthorizationCode(authorizationCode)
            );
        }
        findByAuthorizationCodeVerifyInvocations(
                authorizationCode,
                cacheServiceResult
        );
    }


    static Stream<Arguments> saveTestCases() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        AuthenticationRequestCredentialsAndChallengeDto invalidDto = buildAuthenticationRequestCredentialsAndChallenge(
                "usernameValue",
                "passwordValue",
                "NotValid"
        );
        AuthenticationRequestCredentialsAndChallengeDto validDto = buildAuthenticationRequestCredentialsAndChallenge(
                "usernameValue",
                "passwordValue",
                HashAlgorithm.SHA_384.getAlgorithm()
        );
        AuthenticationRequestDetails expectedFromValidDto = buildAuthenticationRequestDetails(
                "ItDoesNotCare",
                applicationClientId,
                validDto.getUsername(),
                "encrypted value",
                validDto.getChallenge(),
                HashAlgorithm.getByAlgorithm(validDto.getChallengeMethod()).get()
        );
        return Stream.of(
                //@formatter:off
                //            applicationClientId,   authenticationRequestDto,   expectedException,                    expectedResult
                Arguments.of( null,                  null,                       null,                                 empty() ),
                Arguments.of( "ItDoesNotCare",       null,                       null,                                 empty() ),
                Arguments.of( "ItDoesNotCare",       invalidDto,                 ConstraintViolationException.class,   null ),
                Arguments.of( applicationClientId,   validDto,                   null,                                 of(expectedFromValidDto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(String applicationClientId,
                               AuthenticationRequestCredentialsAndChallengeDto authenticationRequestCredentialsAndChallengeDto,
                               Class<? extends Exception> expectedException,
                               Optional<AuthenticationRequestDetails> expectedResult) {
        when(mockCacheService.put(anyString(), any(AuthenticationRequestDetails.class)))
                .thenReturn(true);
        if (null != expectedResult && expectedResult.isPresent()) {
            when(mockEncryptorService.encrypt(eq(authenticationRequestCredentialsAndChallengeDto.getPassword())))
                    .thenReturn("encrypted value");
        }
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.save(applicationClientId, authenticationRequestCredentialsAndChallengeDto)
            );
        }
        else {
            Optional<AuthenticationRequestDetails> result = service.save(
                    applicationClientId,
                    authenticationRequestCredentialsAndChallengeDto
            );
            assertNotNull(result);
            if (result.isPresent() && null != expectedResult && expectedResult.isPresent()) {
                compareAuthenticationRequestDetails(
                        expectedResult.get(),
                        result.get()
                );
            }
            if (result.isPresent()) {
                verify(mockCacheService, times(1))
                        .put(anyString(), any(AuthenticationRequestDetails.class));
            }
        }
    }


    private void findByAuthorizationCodeVerifyInvocations(String authorizationCode,
                                                          AuthenticationRequestDetails cacheServiceResult) {
        // No authorizationCode value provided
        if (null == authorizationCode) {
            verify(mockCacheService, times(0))
                    .get(eq(authorizationCode));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Found AuthenticationRequestDetails in cache
        } else if (null != cacheServiceResult) {
            verify(mockCacheService, times(1))
                    .get(eq(authorizationCode));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        // Not found AuthenticationRequestDetails in cache
        } else if (null == cacheServiceResult) {
            verify(mockCacheService, times(1))
                    .get(eq(authorizationCode));
            verify(mockCacheService, times(0))
                    .put(any(), any());

        } else {
            throw new RuntimeException("Not well managed use case");
        }
    }


    private void compareAuthenticationRequestDetails(AuthenticationRequestDetails expected,
                                                     AuthenticationRequestDetails actual) {
        assertNotNull(actual.getAuthorizationCode());
        assertFalse(actual.getAuthorizationCode().isEmpty());
        assertEquals(
                expected.getApplicationClientId(),
                actual.getApplicationClientId()
        );
        assertEquals(
                expected.getUsername(),
                actual.getUsername()
        );
        assertEquals(
                expected.getEncryptedPassword(),
                actual.getEncryptedPassword()
        );
        assertEquals(
                expected.getChallenge(),
                actual.getChallenge()
        );
        assertEquals(
                expected.getChallengeMethod(),
                actual.getChallengeMethod()
        );
    }

}
