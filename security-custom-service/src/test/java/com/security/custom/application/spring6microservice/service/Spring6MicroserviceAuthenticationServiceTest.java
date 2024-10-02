package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.model.enums.PermissionEnum;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.repository.UserRepository;
import com.security.custom.dto.RawAuthenticationInformationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.security.custom.enums.token.TokenKeys.AUTHORITIES;
import static com.security.custom.enums.token.TokenKeys.NAME;
import static com.security.custom.enums.token.TokenKeys.USERNAME;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class Spring6MicroserviceAuthenticationServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    private Spring6MicroserviceAuthenticationService service;


    @BeforeEach
    public void init() {
        service = new Spring6MicroserviceAuthenticationService(
                mockUserRepository,
                mockPasswordEncoder
        );
    }


    @Test
    @DisplayName("getRawAuthenticationInformation: when not existing user is given then Optional empty is returned")
    public void getRawAuthenticationInformation_whenNotExistingUserIsGiven_thenOptionalEmptyIsReturned() {
        Optional<RawAuthenticationInformationDto> rawTokenInformation = service.getRawAuthenticationInformation(null);

        assertNotNull(rawTokenInformation);
        assertFalse(rawTokenInformation.isPresent());
    }


    @Test
    @DisplayName("getRawAuthenticationInformation: when an existing user is given then related information is returned")
    public void getRawAuthenticationInformation_whenAnExistingUserIsGiven_thenRelatedInformationIsReturned() {
        Role role = new Role(
                1,
                RoleEnum.ROLE_ADMIN.name()
        );
        role.addPermission(PermissionEnum.CREATE_ORDER);
        role.addPermission(PermissionEnum.GET_ORDER);

        User user = User.builder()
                .username("test username")
                .name("test name")
                .roles(new HashSet<>(List.of(role)))
                .build();

        Optional<RawAuthenticationInformationDto> rawTokenInformation = service.getRawAuthenticationInformation(user);

        assertTrue(rawTokenInformation.isPresent());
        checkRawAuthenticationInformation(
                rawTokenInformation.get(),
                user
        );
    }


    static Stream<Arguments> loadUserByUsernameTestCases() {
        User inactiveUser = User.builder()
                .username("inactiveUser")
                .active(false)
                .build();
        User activeUser = User.builder()
                .username("activeUser")
                .active(true)
                .build();
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
        when(mockUserRepository.findByUsername(username))
                .thenReturn(repositoryResult);

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


    static Stream<Arguments> isValidPasswordTestCases() {
        return Stream.of(
                //@formatter:off
                //            rawPassword,     encodedPassword,     passwordEncoderResult,   expectedResult
                Arguments.of( null,            null,                false,                   false ),
                Arguments.of( "",              null,                false,                   false ),
                Arguments.of( "",              "",                  false,                   false ),
                Arguments.of( "rawPassword",   "encodedPassword",   false,                   false ),
                Arguments.of( "rawPassword",   "encodedPassword",   true,                    true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isValidPasswordTestCases")
    @DisplayName("isValidPassword: test cases")
    public void isValidPassword_testCases(String rawPassword,
                                          String encodedPassword,
                                          boolean passwordEncoderResult,
                                          boolean expectedResult) {
        User user = User.builder()
                .username("username")
                .active(true)
                .password(encodedPassword)
                .build();

        when(mockPasswordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(passwordEncoderResult);

        assertEquals(
                expectedResult,
                service.isValidPassword(rawPassword, user)
        );
    }


    private void checkRawAuthenticationInformation(RawAuthenticationInformationDto rawAuthenticationInformation,
                                                   User user) {
        assertNotNull(rawAuthenticationInformation);
        assertNotNull(rawAuthenticationInformation.getAccessAuthenticationInformation());
        assertNotNull(rawAuthenticationInformation.getRefreshAuthenticationInformation());
        assertNotNull(rawAuthenticationInformation.getAdditionalAuthenticationInformation());

        // AccessAuthenticationInformation
        assertEquals(
                user.getUsername(),
                rawAuthenticationInformation.getAccessAuthenticationInformation().get(USERNAME.getKey())
        );
        assertEquals(
                user.getName(),
                rawAuthenticationInformation.getAccessAuthenticationInformation().get(NAME.getKey())
        );
        assertEquals(
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()),
                rawAuthenticationInformation.getAccessAuthenticationInformation().get(AUTHORITIES.getKey())
        );

        // RefreshAuthenticationInformation
        assertEquals(
                user.getUsername(),
                rawAuthenticationInformation.getRefreshAuthenticationInformation().get(USERNAME.getKey())
        );

        // AdditionalAuthenticationInformation
        assertEquals(
                user.getUsername(),
                rawAuthenticationInformation.getAdditionalAuthenticationInformation().get(USERNAME.getKey())
        );
        assertEquals(
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()),
                rawAuthenticationInformation.getAdditionalAuthenticationInformation().get(AUTHORITIES.getKey())
        );
    }

}
