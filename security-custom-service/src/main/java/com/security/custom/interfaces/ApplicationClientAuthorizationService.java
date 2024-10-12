package com.security.custom.interfaces;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Functionality related with the authorization process in every application.
 */
public interface ApplicationClientAuthorizationService {

    /**
     * Returns the username related to the current authorized user included in provided {@code source}.
     *
     * @param source
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Optional} with the username value if exists in {@code source},
     *         {@link Optional#empty()} otherwise.
     */
    Optional<String> getUsername(final Map<String, Object> source);


    /**
     * Returns the authorities related to the current authorized user included in provided {@code source}.
     *
     * @param source
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Set} with all authorities related to the authorized user
     */
    Set<String> getAuthorities(final Map<String, Object> source);

}
