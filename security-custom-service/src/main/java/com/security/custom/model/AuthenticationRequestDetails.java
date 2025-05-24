package com.security.custom.model;

import com.spring6microservices.common.spring.enums.HashAlgorithm;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Required data to authenticate a user using flow with PKCE.
 *
 * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
 */
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(
        of = { "authorizationCode" }
)
@NoArgsConstructor
public class AuthenticationRequestDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = -321365387728349369L;

    private String authorizationCode;
    private String applicationClientId;
    private String challenge;
    private HashAlgorithm challengeMethod;

}
