package com.security.custom.configuration.security;

import com.security.custom.service.ApplicationClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *    Overwrites the default authentication functionality {@link UserDetailsRepositoryReactiveAuthenticationManager},
 * adding customs: {@link PasswordEncoder} and {@link ReactiveUserDetailsService}
 */
@Component
public class AuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    @Autowired
    public AuthenticationManager(@Lazy final ApplicationClientDetailsService applicationClientDetailsService,
                                 @Lazy final PasswordEncoder passwordEncoder) {
        super(applicationClientDetailsService);
        this.setPasswordEncoder(
                passwordEncoder
        );
    }

}
