package com.security.jwt.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.security.jwt.enums.token.TokenSignatureAlgorithm;
import com.security.jwt.exception.token.TokenException;
import com.security.jwt.exception.token.TokenExpiredException;
import com.security.jwt.exception.token.TokenInvalidException;
import com.spring6microservices.common.core.functional.either.Either;
import com.spring6microservices.common.core.functional.either.Left;
import com.spring6microservices.common.core.functional.either.Right;
import com.spring6microservices.common.core.util.DateTimeUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.spring6microservices.common.core.functional.either.Either.left;
import static com.spring6microservices.common.core.functional.either.Either.right;
import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedRootError;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

/**
 * Utility class used to manage signed JWT tokens (JWS).
 * <p>
 *    In the <strong>methods used to generate the tokens</strong>, depending on selected {@link TokenSignatureAlgorithm},
 * the expected value of signature secrets will be different:
 * <p>
 * <ul>
 *   <li>
 *      {@link TokenSignatureAlgorithm#HS256}, {@link TokenSignatureAlgorithm#HS384}, {@link TokenSignatureAlgorithm#HS512}:
 *      a <i>raw</i> {@link String} with the secret value.
 *   </li>
 *   <li>
 *      {@link TokenSignatureAlgorithm#RS256}, {@link TokenSignatureAlgorithm#RS384}, {@link TokenSignatureAlgorithm#RS512}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PRIVATE KEY-----
 *        ...
 *        -----END PRIVATE KEY-----
 *      </pre>
 *   </li>
 * </ul>
 * <p>
 *    In the <strong>methods used to verify tokens and extract their content</strong>, depending on selected
 * {@link TokenSignatureAlgorithm}, the expected value of signature secrets will be different:
 * <p>
 * <ul>
 *   <li>
 *      {@link TokenSignatureAlgorithm#HS256}, {@link TokenSignatureAlgorithm#HS384}, {@link TokenSignatureAlgorithm#HS512}:
 *      a <i>raw</i> {@link String} with the secret value.
 *   </li>
 *   <li>
 *      {@link TokenSignatureAlgorithm#RS256}, {@link TokenSignatureAlgorithm#RS384}, {@link TokenSignatureAlgorithm#RS512}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *      </pre>
 *   </li>
 * </ul>
 */
@Log4j2
@UtilityClass
public class JwsUtil {

    /**
     *    Using the given {@code informationToInclude} generates a valid JWS token (signed JWT) signed with the selected
     * {@link TokenSignatureAlgorithm} and {@code signatureSecret}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param signatureAlgorithm
     *    {@link TokenSignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return {@link String} with the JWS
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm} or {@code signatureSecret} are {@code null}
     * @throws TokenException if there was a problem generating the JWS token
     */
    public static String generateToken(final Map<String, Object> informationToInclude,
                                       final TokenSignatureAlgorithm signatureAlgorithm,
                                       final String signatureSecret,
                                       final long expirationTimeInSeconds) {
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        JWTClaimsSet claimsSet = addClaims(
                informationToInclude,
                expirationTimeInSeconds
        );
        SignedJWT signedJWT = getSignedJWT(
                signatureAlgorithm,
                signatureSecret,
                claimsSet
        );
        return signedJWT.serialize();
    }


    /**
     * Extract from the given {@code jwsToken} all the information included in the payload.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload in {@code jwsToken}
     *
     * @throws IllegalArgumentException if {@code jwsToken} or {@code signatureSecret} are {@code null}
     * @throws TokenInvalidException if {@code jwsToken} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jwsToken} is valid but has expired
     * @throws TokenException if there was a problem getting claims of {@code jwsToken}
     */
    public static Map<String, Object> getAllClaimsFromToken(final String jwsToken,
                                                            final String signatureSecret) {
        Assert.hasText(jwsToken, "jwsToken cannot be null or empty");
        Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        try {
            if (!isJwsToken(jwsToken)) {
                throw new TokenInvalidException(
                        format("The token: %s is not a JWS one",
                                jwsToken
                        )
                );
            }
            SignedJWT signedJWT = SignedJWT.parse(jwsToken);
            JWSVerifier verifier = getSuitableVerifier(
                    signedJWT,
                    signatureSecret
            );
            if (!signedJWT.verify(verifier)) {
                throw new TokenInvalidException(
                        format("The JWS token: %s does not match with the provided signatureSecret",
                                jwsToken
                        )
                );
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (null == expirationTime || expirationTime.before(new Date())) {
                throw new TokenExpiredException(
                        format("The JWT token: %s has expired at %s",
                                jwsToken,
                                expirationTime
                        )
                );
            }
            return signedJWT.getJWTClaimsSet().getClaims();

        } catch (TokenException e) {
            throw e;

        } catch (Exception e) {
            throw new TokenException(
                    format("The was an error getting information included in JWS token: %s. %s",
                            jwsToken,
                            getFormattedRootError(e)
                    ),
                    e
            );
        }
    }


    /**
     *    Returns an {@link Right} with all the information included in the payload of {@code jwsToken}. {@link Left}
     * with the {@link Exception} if there was an error trying to extract such payload.
     *
     * @apiNote
     *    The difference between this method and {@link JwsUtil#getAllClaimsFromToken(String, String)} is: this method
     * does not throw any exception, if there is a problem extracting the payload the {@link Exception} will be added
     * in the {@link Left} element of returned {@link Either}.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link Right} with {@link Map} of {@link String}-{@link Object} with contain of the payload in {@code jwsToken}.
     *         {@link Left} with the {@link Exception} is there was an error during the process.
     */
    public static Either<Exception, Map<String, Object>> getSafeAllClaimsFromToken(final String jwsToken,
                                                                                   final String signatureSecret) {
        try {
            return right(
                    getAllClaimsFromToken(
                            jwsToken,
                            signatureSecret
                    )
            );

        } catch (Exception e) {
            log.debug(
                    format("The was an error getting information included in JWS token: %s. %s",
                            jwsToken,
                            getFormattedRootError(e)
                    ),
                    e
            );
            return left(e);
        }
    }


    /**
     * Get the information included in the given JWS {@code jwsToken} that match with the given {@code keysToInclude}.
     *
     * @apiNote
     *    If {@code keysToInclude} is {@code null} or empty, then an empty {@link Map} is returned.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToInclude
     *    {@link Set} of {@link String} with the {@code key}s to extract from Jwt token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload in {@code jwsToken} that matches
     *         with requested {@code keysToInclude}
     *
     * @throws IllegalArgumentException if {@code jwsToken} or {@code signatureSecret} are {@code null}
     * @throws TokenInvalidException if {@code jwsToken} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jwsToken} is valid but has expired
     * @throws TokenException if there was a problem getting claims of {@code jwsToken}
     */
    public static Map<String, Object> getPayloadKeys(final String jwsToken,
                                                     final String signatureSecret,
                                                     final Set<String> keysToInclude) {
        final Set<String> finalKeysToInclude = getOrElse(
                keysToInclude,
                new HashSet<>()
        );
        return getAllClaimsFromToken(jwsToken, signatureSecret)
                .entrySet().stream()
                .filter(e ->
                        finalKeysToInclude.contains(e.getKey())
                )
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        )
                );
    }


    /**
     * Get the information included in the given JWS {@code jwsToken} except the given {@code keysToExclude}.
     *
     * @apiNote
     *    If {@code keysToExclude} is {@code null} or empty, then a {@link Map} containing all data of given {@code jwsToken}
     * is returned.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToExclude
     *    {@link Set} of {@link String} with the {@code key}s to exclude from JWS token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload in {@code jwsToken} that does not
     *         match with {@code keysToExclude}
     *
     * @throws IllegalArgumentException if {@code jwsToken} or {@code signatureSecret} are {@code null}
     * @throws TokenInvalidException if {@code jwsToken} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jwsToken} is valid but has expired
     * @throws TokenException if there was a problem getting claims of {@code jwsToken}
     */
    public static Map<String, Object> getPayloadExceptKeys(final String jwsToken,
                                                           final String signatureSecret,
                                                           final Set<String> keysToExclude) {
        final Set<String> finalKeysToExclude = getOrElse(
                keysToExclude,
                new HashSet<>()
        );
        return getAllClaimsFromToken(jwsToken, signatureSecret)
                .entrySet().stream()
                .filter(e ->
                        !finalKeysToExclude.contains(e.getKey())
                )
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        )
                );
    }


    /**
     * Return if the given {@code token} is a JWS one.
     *
     * @param token
     *    {@link String} with the {@code token} to check
     *
     * @return {@code true} if the {@code token} is an JWS one, {@code false} otherwise
     */
    public static boolean isJwsToken(final String token) {
        try {
            Assert.hasText(token, "token cannot be null or empty");
            Base64URL[] parts = JOSEObject.split(token);
            Map<String, Object> jsonObjectProperties = JSONObjectUtils.parse(
                    parts[0].decodeToString()
            );
            Algorithm alg = Header.parseAlgorithm(jsonObjectProperties);
            return (alg instanceof JWSAlgorithm) &&
                    TokenSignatureAlgorithm.getByAlgorithm((JWSAlgorithm) alg).isPresent();

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


    /**
     * Generate the information to include in the JWT token.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWT toke will be valid
     *
     * @return {@link JWTClaimsSet}
     */
    private static JWTClaimsSet addClaims(final Map<String, Object> informationToInclude,
                                          final long expirationTimeInSeconds) {
        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        if (null != informationToInclude) {
            informationToInclude.forEach(claimsSet::claim);
        }
        Date now = new Date();
        Date expirationDate = DateTimeUtil.plus(
                now,
                expirationTimeInSeconds,
                ChronoUnit.SECONDS
        );
        return claimsSet
                .issueTime(now)
                .expirationTime(expirationDate)
                .build();
    }


    /**
     * Generate the signed JWT token (a JWS one).
     *
     * @param signatureAlgorithm
     *    {@link TokenSignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param claimsSet
     *    {@link JWTClaimsSet} with the information to include
     *
     * @return {@link SignedJWT}
     *
     * @throws TokenException it there was a problem creating the JWS token
     */
    private static SignedJWT getSignedJWT(final TokenSignatureAlgorithm signatureAlgorithm,
                                          final String signatureSecret,
                                          final JWTClaimsSet claimsSet) {
        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(signatureAlgorithm.getAlgorithm()),
                    claimsSet
            );
            signedJWT.sign(
                    getSuitableSigner(
                            signatureAlgorithm,
                            signatureSecret
                    )
            );
            return signedJWT;

        } catch (Exception e) {
            throw new TokenException(
                    format("The was a problem trying to create a new JWS token using the algorithm: %s. %s",
                            signatureAlgorithm.name(),
                            getFormattedRootError(e)
                    ),
                    e
            );
        }
    }


    /**
     * Return the suitable {@link JWSSigner} taking into account the {@link TokenSignatureAlgorithm} used to sing the given JWS token.
     *
     * @param signatureAlgorithm
     *    {@link TokenSignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link JWSSigner}
     *
     * @throws IllegalArgumentException if it was not possible to find a suitable {@link JWSSigner}
     * @throws TokenException if there was an error creating the {@link JWSSigner}
     */
    private static JWSSigner getSuitableSigner(final TokenSignatureAlgorithm signatureAlgorithm,
                                               final String signatureSecret) {
        try {
            return switch (signatureAlgorithm) {
                case HS256, HS384, HS512 ->
                        new MACSigner(signatureSecret);

                case RS256, RS384, RS512 ->
                        new RSASSASigner(
                                RSAKey.parseFromPEMEncodedObjects(signatureSecret)
                                        .toRSAKey()
                        );

                case null, default ->
                        throw new IllegalArgumentException(
                                format("It was not possible to find a suitable signer for the signature algorithm: %s",
                                        ofNullable(signatureAlgorithm).map(Enum::name).orElse("null")
                                )
                        );
            };

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new TokenException(
                    format("The was a problem trying to create the suitable signer for the signature algorithm: %s. %s",
                            ofNullable(signatureAlgorithm).map(Enum::name).orElse("null"),
                            getFormattedRootError(e)
                    ),
                    e
            );
        }
    }


    /**
     * Return the suitable {@link JWSVerifier} taking into account the {@link JWSAlgorithm} used to sing the given JWS token.
     *
     * @param signedJWT
     *    {@link SignedJWT} with JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link JWSVerifier}
     *
     * @throws IllegalArgumentException if it was not possible to find a suitable {@link JWSVerifier}
     * @throws TokenException if there was an error creating the {@link JWSVerifier}
     */
    private static JWSVerifier getSuitableVerifier(final SignedJWT signedJWT,
                                                   final String signatureSecret) {
        TokenSignatureAlgorithm signatureAlgorithm = TokenSignatureAlgorithm.getByAlgorithm(
                signedJWT.getHeader().getAlgorithm()
        ).orElse(null);

        try {
            return switch (signatureAlgorithm) {
                case HS256, HS384, HS512 ->
                        new MACVerifier(signatureSecret);

                case RS256, RS384, RS512 ->
                        new RSASSAVerifier(
                                RSAKey.parseFromPEMEncodedObjects(signatureSecret)
                                        .toRSAKey()
                        );

                case null, default ->
                        throw new IllegalArgumentException(
                                format("It was not possible to find a suitable verifier for the signature algorithm: %s",
                                        ofNullable(signatureAlgorithm).map(Enum::name).orElse("null")
                                )
                        );
            };

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new TokenException(
                    format("The was a problem trying to create the suitable verifier for the signature algorithm: %s. %s",
                            ofNullable(signatureAlgorithm).map(Enum::name).orElse("null"),
                            getFormattedRootError(e)
                    ),
                    e
            );
        }
    }

}
