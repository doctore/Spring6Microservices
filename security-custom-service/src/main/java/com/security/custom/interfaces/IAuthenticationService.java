package com.security.custom.interfaces;

import com.security.custom.dto.RawAuthenticationInformationDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Functionality related with the authentication process.
 */
public interface IAuthenticationService extends UserDetailsService {

    /**
     * Returns the data required for the authentication process.
     *
     * @param userDetails
     *    {@link UserDetails} identifier use to get the information to fill the tokens
     *
     * @return {@link Optional} of {@link RawAuthenticationInformationDto} with information to include
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exist
     */
    Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(final UserDetails userDetails);


    /**
     * Verifies if the given password matches with the one belongs to {@code userDetails}.
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
