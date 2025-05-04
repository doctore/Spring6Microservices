package com.security.custom.application.spring6microservice.repository;

import com.security.custom.application.spring6microservice.model.enums.PermissionEnum;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@Import(UserRepository.class)
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/spring6microservice_security.sql"
)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;


    static Stream<Arguments> findByUsernameTestCases() {
        Role role = new Role(
                1,
                RoleEnum.ROLE_ADMIN.name()
        );
        role.addPermission(
                PermissionEnum.CREATE_ORDER
        );
        role.addPermission(
                PermissionEnum.GET_ORDER
        );
        User existingUser = User.builder()
                .id(1L)
                .name("Test user name")
                .username("Test user username")
                .password("Test user password")
                .active(true)
                .roles(
                        Set.of(
                                role
                        )
                )
                .build();

        return Stream.of(
                //@formatter:off
                //            id,                           expectedResult
                Arguments.of( null,                         empty() ),
                Arguments.of( "ItDoesNotExist",             empty() ),
                Arguments.of( existingUser.getUsername(),   of(existingUser) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByUsernameTestCases")
    @DisplayName("findByUsername: test cases")
    public void findByUsername_testCases(String username,
                                         Optional<User> expectedResult) {
        Optional<User> result = repository.findByUsername(username);
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
