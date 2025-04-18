package com.security.oauth.configuration.security;

import com.security.oauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *    Overwrites the default authentication functionality {@link DaoAuthenticationProvider},
 * adding customs: {@link PasswordEncoder} and {@link UserService}
 */
@Component
public class AuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public AuthenticationProvider(final UserService userService,
                                  final PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.setUserDetailsService(
                userService
        );
    }

}
