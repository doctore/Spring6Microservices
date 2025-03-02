package com.security.custom.model;

import com.security.custom.enums.HashAlgorithm;
import lombok.*;

/**
 * Required data to authenticate a user using flow with PKCE.
 *
 * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
 */
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = { "authorizationCode" })
@NoArgsConstructor
public class AuthenticationRequestDetails {

    private String authorizationCode;
    private String applicationClientId;
    private String challenge;
    private HashAlgorithm challengeMethod;

}
