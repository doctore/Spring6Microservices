package com.security.jwt.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.security.jwt.enums.token.TokenEncryptionAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.util.Map;

import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedRootError;
import static java.lang.String.format;

/**
 * Utility class used to manage encrypted JWS tokens (JWE).
 */
@Log4j2
@UtilityClass
public class JweUtil {

    // Required to indicate nested JWT tokens
    private static final String JWE_CONTENT_TYPE = "JWT";


    /**
     * Return if the given {@code token} is a JWE one.
     *
     * @param token
     *    {@link String} with the {@code token} to check
     *
     * @return {@code true} if the {@code token} is an JWE one, {@code false} otherwise
     */
    public boolean isJweToken(final String token) {
        try {
            Assert.hasText(token, "token cannot be null or empty");
            Base64URL[] parts = JOSEObject.split(token);
            Map<String, Object> jsonObjectProperties = JSONObjectUtils.parse(
                    parts[0].decodeToString()
            );
            Algorithm alg = Header.parseAlgorithm(jsonObjectProperties);
            return (alg instanceof JWEAlgorithm) &&
                    TokenEncryptionAlgorithm.getByAlgorithm((JWEAlgorithm) alg).isPresent();

        } catch (Exception e) {
            log.debug(
                    format("The was a problem trying to figure out the type of token: %s. %s",
                            token,
                            getFormattedRootError(e)
                    ),
                    e
            );
            return false;
        }
    }

}
