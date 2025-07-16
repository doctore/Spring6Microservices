package com.security.oauth.service;

import com.security.oauth.model.User;
import com.security.oauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository mockRepository;

    private UserService service;


    @BeforeEach
    public void init() {
        service = new UserService(
                mockRepository
        );
    }


    static Stream<Arguments> loadUserByUsernameTestCases() {
        User inactiveUser = User.builder().username("inactiveUser").active(false).build();
        User activeUser = User.builder().username("activeUser").active(true).build();
        return Stream.of(
                //@formatter:off
                //            username,                     repositoryResult,   expectedException,                 expectedResult
                Arguments.of( null,                         empty(),            UsernameNotFoundException.class,   null ),
                Arguments.of( "NotFound",                   empty(),            UsernameNotFoundException.class,   null ),
                Arguments.of( inactiveUser.getUsername(),   of(inactiveUser),   LockedException.class,             null ),
                Arguments.of( activeUser.getUsername(),     of(activeUser),     null,                              activeUser )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loadUserByUsernameTestCases")
    @DisplayName("loadUserByUsername: test cases")
    public void loadUserByUsername_testCases(String username,
                                             Optional<User> repositoryResult,
                                             Class<? extends Exception> expectedException,
                                             UserDetails expectedResult) {
        when(mockRepository.findByUsername(username))
                .thenReturn(
                        repositoryResult
                );

        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.loadUserByUsername(username)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    service.loadUserByUsername(username)
            );
        }
    }

}
