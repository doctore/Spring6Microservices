package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.interfaces.IUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "UserDetailsService")
public class Spring6MicroserviceUserService implements IUserService {

    // TODO: TEMPORARY
    @Override
    public boolean isValidPassword(String passwordToVerify, UserDetails userDetails) {
        return false;
    }

    // TODO: TEMPORARY
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }




    /* TODO: PENGIND TO TO => PENDING TO CHANGE TO USE Spring JDBC Template
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(@Lazy final UserRepository repository,
                       @Lazy final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link UserDetails}
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones.
     */
    /*
    @Override
    public UserDetails loadUserByUsername(final String username) {
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


    @Override
    public boolean passwordsMatch(final String passwordToVerify,
                                  final UserDetails userDetails) {
        if (!StringUtils.hasText(passwordToVerify) ||
                (null == userDetails ||
                        !StringUtils.hasText(userDetails.getPassword()))) {
            return false;
        }
        return passwordEncoder.matches(
                passwordToVerify,
                userDetails.getPassword()
        );
    }
     */

}
