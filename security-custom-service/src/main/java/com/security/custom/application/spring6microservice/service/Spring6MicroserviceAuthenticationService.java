package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.repository.UserRepository;
import com.security.custom.dto.RawAuthenticationInformationDto;
import com.security.custom.interfaces.IAuthenticationService;
import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "AuthenticationService")
public class Spring6MicroserviceAuthenticationService implements IAuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public Spring6MicroserviceAuthenticationService(@Lazy final UserRepository repository,
                                                    @Lazy final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    // TODO: TEMPORARY
    @Override
    public Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(final UserDetails userDetails) {
        return Optional.empty();
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
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database
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

}
