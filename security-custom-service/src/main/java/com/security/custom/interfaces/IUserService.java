package com.security.custom.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Used to extend functionality provided by {@link UserDetailsService}
 */
public interface IUserService extends UserDetailsService {

    /**
     * Verify if the given password matches with the one belongs to {@code userDetails}.
     *
     * @param passwordToVerify
     *    Password to verify
     * @param userDetails
     *    {@link UserDetails} which password will be compared
     *
     * @return {@code true} if {@code passwordToVerify} matches with {@link UserDetails#getPassword()}, {@code false} otherwise.
     */
    boolean isValidPassword(final String passwordToVerify,
                            final UserDetails userDetails);

}
