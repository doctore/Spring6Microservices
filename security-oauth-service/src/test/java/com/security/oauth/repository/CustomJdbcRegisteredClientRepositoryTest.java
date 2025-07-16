package com.security.oauth.repository;

import com.security.oauth.configuration.cache.RegisteredClientCacheConfiguration;
import com.security.oauth.service.cache.RegisteredClientCacheService;
import com.spring6microservices.common.spring.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.oauth.TestDataFactory.buildRegisteredClient;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@Import({
        CustomJdbcRegisteredClientRepository.class,
        RegisteredClientCacheService.class,
        RegisteredClientCacheConfiguration.class,
        CacheService.class
})
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/oauth2_registered_client.sql"
)
public class CustomJdbcRegisteredClientRepositoryTest {

    @Mock
    private RegisteredClientCacheService mockRegisteredClientCacheService;

    @Autowired
    private JdbcOperations jdbcOperations;

    private CustomJdbcRegisteredClientRepository repository;


    @BeforeEach
    public void init() {
        this.repository = new CustomJdbcRegisteredClientRepository(
                jdbcOperations,
                mockRegisteredClientCacheService
        );
    }


    static Stream<Arguments> findByIdTestCases() {
        String id = "Spring6Microservices";
        RegisteredClient registeredClient = buildRegisteredClient(
                id,
                "Spring6 microservices proof of concept",
                "{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW",
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                AuthorizationGrantType.AUTHORIZATION_CODE,
                "http://localhost:8181/redirect",
                "http://localhost:8181/post_logout",
                "read"
        );
        return Stream.of(
                //@formatter:off
                //            id,           cacheServiceResult,     expectedException,                expectedResult
                Arguments.of( null,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "",           null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "   ",        null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "NotFound",   empty(),                null,                             null ),
                Arguments.of( id,           empty(),                null,                             registeredClient ),
                Arguments.of( id,           of(registeredClient),   null,                             registeredClient )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(String id,
                                   Optional<RegisteredClient> cacheServiceResult,
                                   Class<? extends Exception> expectedException,
                                   RegisteredClient expectedResult) {
        when(mockRegisteredClientCacheService.get(id))
                .thenReturn(
                        cacheServiceResult
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> repository.findById(id)
            );
        }
        else {
            RegisteredClient result = repository.findById(
                    id
            );

            if (null == expectedResult) {
                assertNull(result);
            }
            else {
                assertNotNull(result);
                assertThat(
                        result,
                        samePropertyValuesAs(
                                expectedResult,
                                "clientIdIssuedAt",
                                "clientSecretExpiresAt",
                                "clientSettings",
                                "tokenSettings"
                        )
                );
            }
            verifyRegisteredClientCacheServiceInvocationsFindBy(
                    id,
                    cacheServiceResult,
                    expectedResult
            );
        }
    }


    static Stream<Arguments> findByClientIdTestCases() {
        String id = "Spring6Microservices";
        RegisteredClient registeredClient = buildRegisteredClient(
                id,
                "Spring6 microservices proof of concept",
                "{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW",
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                AuthorizationGrantType.AUTHORIZATION_CODE,
                "http://localhost:8181/redirect",
                "http://localhost:8181/post_logout",
                "read"
        );
        return Stream.of(
                //@formatter:off
                //            id,           cacheServiceResult,     expectedException,                expectedResult
                Arguments.of( null,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "",           null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "   ",        null,                   IllegalArgumentException.class,   null ),
                Arguments.of( "NotFound",   empty(),                null,                             null ),
                Arguments.of( id,           empty(),                null,                             registeredClient ),
                Arguments.of( id,           of(registeredClient),   null,                             registeredClient )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByClientIdTestCases")
    @DisplayName("findByClientId: test cases")
    public void findByClientId_testCases(String clientId,
                                         Optional<RegisteredClient> cacheServiceResult,
                                         Class<? extends Exception> expectedException,
                                         RegisteredClient expectedResult) {
        when(mockRegisteredClientCacheService.get(clientId))
                .thenReturn(
                        cacheServiceResult
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> repository.findByClientId(clientId)
            );
        }
        else {
            RegisteredClient result = repository.findByClientId(
                    clientId
            );

            if (null == expectedResult) {
                assertNull(result);
            }
            else {
                assertNotNull(result);
                assertThat(
                        result,
                        samePropertyValuesAs(
                                expectedResult,
                                "clientIdIssuedAt",
                                "clientSecretExpiresAt",
                                "clientSettings",
                                "tokenSettings"
                        )
                );
            }
            verifyRegisteredClientCacheServiceInvocationsFindBy(
                    clientId,
                    cacheServiceResult,
                    expectedResult
            );
        }
    }


    static Stream<Arguments> saveTestCases() {
        RegisteredClient existingClientId = buildRegisteredClient(
                "NewRegisteredClient",
                "Spring6Microservices"
        );
        RegisteredClient existingClientSecret = buildRegisteredClient(
                "NewRegisteredClient",
                "New registered client",
                "{bcrypt}$2a$10$eb.2YmvPM6pOSPef5f2EXevru16Sb4UN6c.wHe2a3vwExV5/BY.vW",
                ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                AuthorizationGrantType.CLIENT_CREDENTIALS,
                "http://localhost:8181/redirect2",
                "http://localhost:8181/post_logout2",
                "write"
        );
        RegisteredClient validRegisteredClient = buildRegisteredClient(
                "NewRegisteredClient",
                "New registered client",
                "{bcrypt}$2a$10$i7LFiCo1JRm87ERePQOS3OkZ3Srgub8F7GyoWu6NmUuCLDTPq8zMW",
                ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                AuthorizationGrantType.CLIENT_CREDENTIALS,
                "http://localhost:8181/redirect2",
                "http://localhost:8181/post_logout2",
                "write"
        );
        return Stream.of(
                //@formatter:off
                //            registeredClient,        expectedException,
                Arguments.of( null,                    IllegalArgumentException.class ),
                Arguments.of( existingClientId,        IllegalArgumentException.class ),
                Arguments.of( existingClientSecret,    IllegalArgumentException.class ),
                Arguments.of( validRegisteredClient,   null ),
                Arguments.of( validRegisteredClient,   null )   // To force an update
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(RegisteredClient registeredClient,
                               Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> repository.save(registeredClient)
            );
        }
        else {
            repository.save(registeredClient);
            verify(mockRegisteredClientCacheService, times(1))
                    .put(
                            eq(registeredClient.getId()),
                            any(RegisteredClient.class)
                    );
        }
    }


    private void verifyRegisteredClientCacheServiceInvocationsFindBy(String id,
                                                                     Optional<RegisteredClient> cacheServiceResult,
                                                                     RegisteredClient expectedResult) {
        verify(mockRegisteredClientCacheService, times(1))
                .get(
                        eq(id)
                );

        int putInvocations = cacheServiceResult.isPresent() || null == expectedResult
                ? 0
                : 1;

        verify(mockRegisteredClientCacheService, times(putInvocations))
                .put(
                        eq(id),
                        any(RegisteredClient.class)
                );
    }

}
