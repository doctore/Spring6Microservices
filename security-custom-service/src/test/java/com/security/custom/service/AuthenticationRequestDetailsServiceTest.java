package com.security.custom.service;

import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.enums.HashAlgorithm;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.exception.AuthenticationRequestDetailsNotFoundException;
import com.security.custom.exception.AuthenticationRequestDetailsNotSavedException;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
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
import static com.security.custom.TestDataFactory.buildAuthenticationRequestLoginAuthorizedDto;
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


    @Test
    @DisplayName("save: when no authenticationRequestDto is provided then empty Optional is returned")
    public void save_whenNoAuthenticationRequestDtoIsProvided_thenEmptyOptionalIsReturned() {
        Optional<AuthenticationRequestDetails> result = service.save(
                "ItDoesNotCare",
                null
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockEncryptorService, never())
                .defaultEncrypt(anyString());

        verify(mockCacheService, never())
                .put(anyString(), any(AuthenticationRequestDetails.class));
    }


    @Test
    @DisplayName("save: when given authenticationRequestDto is not valid then IllegalArgumentException is thrown")
    public void save_whenGivenAuthenticationRequestDtoIsNotValid_thenIllegalArgumentExceptionIsThrown() {
        AuthenticationRequestLoginAuthorizedDto invalidDto = buildAuthenticationRequestLoginAuthorizedDto(
                "usernameValue",
                "passwordValue",
                "NotValid"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.save("ItDoesNotCare", invalidDto)
        );

        verify(mockEncryptorService, times(1))
                .defaultEncrypt(eq(invalidDto.getPassword()));

        verify(mockCacheService, never())
                .put(anyString(), any(AuthenticationRequestDetails.class));
    }


    @Test
    @DisplayName("save: when AuthenticationRequestDetails could not be stored then AuthenticationRequestDetailsNotSavedException is thrown")
    public void save_whenAuthenticationRequestDetailsCouldNotBeStored_thenAuthenticationRequestDetailsNotSavedExceptionIsThrown() {
        AuthenticationRequestLoginAuthorizedDto validDto = buildAuthenticationRequestLoginAuthorizedDto(
                "usernameValue",
                "passwordValue",
                HashAlgorithm.SHA_384.getAlgorithm()
        );

        when(mockCacheService.put(anyString(), any(AuthenticationRequestDetails.class)))
                .thenReturn(false);

        assertThrows(
                AuthenticationRequestDetailsNotSavedException.class,
                () -> service.save("ItDoesNotCare", validDto)
        );

        verify(mockEncryptorService, times(1))
                .defaultEncrypt(eq(validDto.getPassword()));

        verify(mockCacheService, times(1))
                .put(anyString(), any(AuthenticationRequestDetails.class));
    }


    @Test
    @DisplayName("save: when a valid authenticationRequestDto is provided and its related AuthenticationRequestDetails is saved then not empty Optional is returned")
    public void save_whenAValidAuthenticationRequestDtoIsProvidedAndItsRelatedAuthenticationRequestDetailsIsSaved_thenNotEmptyOptionalIsReturned() {
        String applicationClientId = SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId();
        AuthenticationRequestLoginAuthorizedDto validDto = buildAuthenticationRequestLoginAuthorizedDto(
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

        when(mockEncryptorService.defaultEncrypt(eq(validDto.getPassword())))
                .thenReturn("encrypted value");
        when(mockCacheService.put(anyString(), any(AuthenticationRequestDetails.class)))
                .thenReturn(true);

        Optional<AuthenticationRequestDetails> result = service.save(
                applicationClientId,
                validDto
        );

        assertNotNull(result);
        assertTrue(result.isPresent());
        compareAuthenticationRequestDetails(
                expectedFromValidDto,
                result.get()
        );

        verify(mockEncryptorService, times(1))
                .defaultEncrypt(eq(validDto.getPassword()));

        verify(mockCacheService, times(1))
                .put(anyString(), any(AuthenticationRequestDetails.class));
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
