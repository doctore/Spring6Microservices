package com.security.custom.interfaces;

/**
 * Functionality related with the authorization process.
 */
public interface IAuthorizationService {


    // TODO: PENDING TO STANDARIZE

    /**
     * Returns the username related with authorized user.
     *
     * @return {@link String}
     */
    //String getUsernameKey();
    //<T> String getUsername(T source);

    /**
     * Return the key in the access token used to store the {@code roles} information.
     *
     * @return {@link String}
     */
    //Set<String> getAuthoritiesKey();
    //<T> Set<String> getAuthorities(T source);

}
