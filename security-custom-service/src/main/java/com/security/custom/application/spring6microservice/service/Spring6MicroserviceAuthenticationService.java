package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.repository.UserRepository;
import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.interfaces.ApplicationClientAuthenticationService;
import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.security.custom.enums.token.TokenKey.AUTHORITIES;
import static com.security.custom.enums.token.TokenKey.NAME;
import static com.security.custom.enums.token.TokenKey.USERNAME;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "AuthenticationService")
public class Spring6MicroserviceAuthenticationService implements ApplicationClientAuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public Spring6MicroserviceAuthenticationService(@Lazy final UserRepository repository,
                                                    @Lazy final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(final UserDetails userDetails) {
        return ofNullable(userDetails)
                .map(user ->
                        buildAuthenticationInformation(
                                (User)user
                        )
                );
    }


    @Override
    public boolean isValidPassword(final String passwordToVerify,
                                   final UserDetails userDetails) {
        if (StringUtil.isBlank(passwordToVerify) ||
                (null == userDetails ||
                        StringUtil.isBlank(userDetails.getPassword()))) {
            return false;
        }
        return passwordEncoder.matches(
                passwordToVerify,
                userDetails.getPassword()
        );
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}.
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link UserDetails}
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database.
     *         {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the {@link Exception}s
     *         that could be thrown
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return ofNullable(username)
                .flatMap(repository::findByUsername)
                .map(u ->  {
                    new AccountStatusUserDetailsChecker()
                            .check(u);
                    return u;
                })
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                format("Username: %s not found in database",
                                        username)
                        )
                );
    }


    /**
     * Creates the {@link RawAuthenticationInformationDto} using data of provided {@link User}.
     *
     * @param user
     *    {@link User} used as source of returned {@link RawAuthenticationInformationDto}
     *
     * @return {@link RawAuthenticationInformationDto}
     */
    private RawAuthenticationInformationDto buildAuthenticationInformation(final User user) {
        return RawAuthenticationInformationDto.builder()
                .accessAuthenticationInformation(
                        getAccessAuthenticationInformation(user)
                )
                .refreshAuthenticationInformation(
                        getRefreshAuthenticationInformation(user)
                )
                .additionalAuthenticationInformation(
                        getAdditionalAuthenticationInformation(user)
                )
                .build();
    }


    /**
     *    Using given {@link User}, returns the information to include in the property
     * {@link RawAuthenticationInformationDto#getAccessAuthenticationInformation()}.
     *
     * @param user
     *    {@link User} used as source
     *
     * @return {@link Map} with the data to add in {@link RawAuthenticationInformationDto#getAccessAuthenticationInformation()}
     */
    private Map<String, Object> getAccessAuthenticationInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
            put(
                    NAME.getKey(),
                    user.getName()
            );
            put(
                    AUTHORITIES.getKey(),
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(
                                    toList()
                            )
            );
        }};
    }


    /**
     *    Using given {@link User}, returns the information to include in the property
     * {@link RawAuthenticationInformationDto#getRefreshAuthenticationInformation()}.
     *
     * @param user
     *    {@link User} used as source
     *
     * @return {@link Map} with the data to add in {@link RawAuthenticationInformationDto#getRefreshAuthenticationInformation()}
     */
    private Map<String, Object> getRefreshAuthenticationInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
        }};
    }


    /**
     *    Using given {@link User}, returns the information to include in the property
     * {@link RawAuthenticationInformationDto#getAdditionalAuthenticationInformation()}.
     *
     * @param user
     *    {@link User} used as source
     *
     * @return {@link Map} with the data to add in {@link RawAuthenticationInformationDto#getAdditionalAuthenticationInformation()}
     */
    private Map<String, Object> getAdditionalAuthenticationInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
            put(
                    AUTHORITIES.getKey(),
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(
                                    toList()
                            )
            );
        }};
    }

}
