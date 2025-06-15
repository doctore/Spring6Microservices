package com.security.custom.repository;

import com.security.custom.configuration.persistence.PersistenceConfiguration;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.enums.token.TokenType;
import com.security.custom.model.ApplicationClientDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@DataJpaTest
@Import(
        PersistenceConfiguration.class
)
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/application_client_details.sql"
)
public class ApplicationClientDetailsRepositoryTest {

    @Autowired
    private ApplicationClientDetailsRepository repository;


    static Stream<Arguments> findByIdTestCases() {
        ApplicationClientDetails existingAPC = ApplicationClientDetails.builder()
                .id("Spring6Microservices")
                .applicationClientSecret("Spring6Microservices-application_client_secret")
                .signatureAlgorithm(TokenSignatureAlgorithm.HS256)
                .signatureSecret("hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k")
                .securityHandler(SecurityHandler.SPRING6_MICROSERVICES)
                .tokenType(TokenType.JWE)
                .encryptionAlgorithm(TokenEncryptionAlgorithm.DIR)
                .encryptionMethod(TokenEncryptionMethod.A128CBC_HS256)
                .encryptionSecret("dirEncryptionSecret##9991a2(jwe)")
                .accessTokenValidityInSeconds(900)
                .refreshTokenValidityInSeconds(3600)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();

        return Stream.of(
                //@formatter:off
                //            id,                    expectedResult
                Arguments.of( null,                  empty() ),
                Arguments.of( "ItDoesNotExist",      empty() ),
                Arguments.of( existingAPC.getId(),   of(existingAPC) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(String id,
                                   Optional<ApplicationClientDetails> expectedResult) {
        Optional<ApplicationClientDetails> result = repository.findById(id);
        if (expectedResult.isEmpty()) {
            assertNotNull(result);
            assertFalse(result.isPresent());
        }
        else {
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertThat(
                    result.get(),
                    samePropertyValuesAs(
                            expectedResult.get(),
                            "createdAt"
                    )
            );
        }
    }

}
