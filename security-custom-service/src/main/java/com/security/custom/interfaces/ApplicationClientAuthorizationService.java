package com.security.custom.interfaces;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Functionality related with the authorization process in every application.
 */
public interface ApplicationClientAuthorizationService {

    /**
     *    Returns the additional information related to the current authorized user included in provided
     * {@code rawAuthorizationInformation}, different from: username and authorities.
     *
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Map} with additional information related to the authorized user
     */
    Map<String, Object> getAdditionalAuthorizationInformation(final Map<String, Object> rawAuthorizationInformation);


    /**
     * Returns the authorities related to the current authorized user included in provided {@code rawAuthorizationInformation}.
     *
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Set} with all authorities related to the authorized user
     */
    Set<String> getAuthorities(final Map<String, Object> rawAuthorizationInformation);


    /**
     * Returns the username related to the current authorized user included in provided {@code rawAuthorizationInformation}.
     *
     * @param rawAuthorizationInformation
     *    {@link Map} containing all data related to the current authorized user
     *
     * @return {@link Optional} with the username value if exists in {@code rawAuthorizationInformation},
     *         {@link Optional#empty()} otherwise.
     */
    Optional<String> getUsername(final Map<String, Object> rawAuthorizationInformation);

}
