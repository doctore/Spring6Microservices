package com.security.custom.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDH1PUDecrypter;
import com.nimbusds.jose.crypto.ECDH1PUEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.EncryptedJWT;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.exception.token.TokenException;
import com.security.custom.exception.token.TokenExpiredException;
import com.security.custom.exception.token.TokenInvalidException;
import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.core.functional.either.Either;
import com.spring6microservices.common.core.functional.either.Left;
import com.spring6microservices.common.core.functional.either.Right;
import com.spring6microservices.common.core.util.AssertUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.spring6microservices.common.core.functional.either.Either.left;
import static com.spring6microservices.common.core.functional.either.Either.right;
import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedCurrentAndRootError;
import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedRootError;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

/**
 * Utility class used to manage encrypted JWS tokens (JWE).
 * <p>
 * In <strong>methods used to generate encrypted tokens</strong>:
 * <ul>
 *   <li>{@link JweUtil#generateToken(String, TokenEncryptionAlgorithm, TokenEncryptionMethod, String)}</li>
 *   <li>{@link JweUtil#generateToken(Map, TokenEncryptionAlgorithm, TokenEncryptionMethod, String, TokenSignatureAlgorithm, String, long)}</li>
 * </ul>
 * <p>
 * Depending on selected {@link TokenEncryptionAlgorithm}, the expected value of encryption secrets will be different:
 * <p>
 * <ul>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#DIR}:
 *      a <i>raw</i> {@link String} with the secret value.
 *   </li>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#RSA_OAEP_256}, {@link TokenEncryptionAlgorithm#RSA_OAEP_384}, {@link TokenEncryptionAlgorithm#RSA_OAEP_512}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *      </pre>
 *   </li>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#ECDH_1PU_A128KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A192KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A256KW}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *        -----BEGIN EC PRIVATE KEY-----
 *        ...
 *        -----END EC PRIVATE KEY-----
 *      </pre>
 *   </li>
 * </ul>
 * <p>
 * In the <strong>methods used to decrypt tokens and extract their content</strong>:
 * <ul>
 *   <li>{@link JweUtil#getAllClaimsFromToken(String, String, String)}</li>
 *   <li>{@link JweUtil#getSafeAllClaimsFromToken(String, String, String)}</li>
 *   <li>{@link JweUtil#getPayloadKeys(String, String, String, Set)}</li>
 *   <li>{@link JweUtil#getPayloadExceptKeys(String, String, String, Set)}</li>
 * </ul>
 * <p>
 * Depending on selected {@link TokenEncryptionAlgorithm}, the expected value of encryption secrets will be different:
 * <p>
 * <ul>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#DIR}:
 *      a <i>raw</i> {@link String} with the secret value.
 *   </li>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#RSA_OAEP_256}, {@link TokenEncryptionAlgorithm#RSA_OAEP_384}, {@link TokenEncryptionAlgorithm#RSA_OAEP_512}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PRIVATE KEY-----
 *        ...
 *        -----END PRIVATE KEY-----
 *      </pre>
 *   </li>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#ECDH_1PU_A128KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A192KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A256KW}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *        -----BEGIN EC PRIVATE KEY-----
 *        ...
 *        -----END EC PRIVATE KEY-----
 *      </pre>
 *   </li>
 * </ul>
 * <p>
 *    If you are going to use only one location to store the encryption secret, for example, in
 * {@link ApplicationClientDetails#getEncryptionSecret()} then, depending on selected {@link TokenEncryptionAlgorithm},
 * the expected value when it uses public/private keys will be different:
 * <p>
 * <ul>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#RSA_OAEP_256}, {@link TokenEncryptionAlgorithm#RSA_OAEP_384}, {@link TokenEncryptionAlgorithm#RSA_OAEP_512}:
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *        -----BEGIN PRIVATE KEY-----
 *        ...
 *        -----END PRIVATE KEY-----
 *      </pre>
 *   </li>
 *   <li>
 *      {@link TokenEncryptionAlgorithm#ECDH_1PU_A128KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A192KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A256KW}::
 *      an {@link String} with a format similar to:
 *      <pre>
 *        -----BEGIN PUBLIC KEY-----
 *        ...
 *        -----END PUBLIC KEY-----
 *        -----BEGIN EC PRIVATE KEY-----
 *        ...
 *        -----END EC PRIVATE KEY-----
 *      </pre>
 *   </li>
 * </ul>
 */
@Log4j2
@UtilityClass
public class JweUtil {

    // Required to indicate nested JWT tokens
    private static final String JWE_CONTENT_TYPE = "JWT";


    /**
     * Using the given {@code jwsToken} generates a valid JWE token (encrypted JWS).
     *
     * @param jwsToken
     *    JWS token to encrypt
     * @param encryptionAlgorithm
     *    {@link TokenEncryptionAlgorithm} used to encrypt the JWS token
     * @param encryptionMethod
     *    {@link TokenEncryptionMethod} used to encrypt the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link String} with the JWE with {@code jwsToken} as nested one
     *
     * @throws IllegalArgumentException if {@code encryptionAlgorithm}, {@code encryptionMethod} are {@code null}
     *                                  if {@code encryptionSecret} is {@code null} or empty.
     * @throws TokenInvalidException it the given {@code jwsToken} is not a JWS one
     * @throws TokenException if there was a problem generating the JWE token
     */
    public String generateToken(final String jwsToken,
                                final TokenEncryptionAlgorithm encryptionAlgorithm,
                                final TokenEncryptionMethod encryptionMethod,
                                final String encryptionSecret) {
        AssertUtil.notNull(encryptionAlgorithm, "encryptionAlgorithm must be not null");
        AssertUtil.notNull(encryptionMethod, "encryptionMethod must be not null");
        AssertUtil.hasText(encryptionSecret, "encryptionSecret must be not null or empty");
        return encryptJwsToken(
                jwsToken,
                encryptionAlgorithm,
                encryptionMethod,
                encryptionSecret
        );
    }


    /**
     * Using the given {@code informationToInclude} generates a valid nested JWS inside JWE token (signed + encrypted JWT):
     * <ul>
     *   <li><strong>1.</strong> JWS token using provided: {@link TokenSignatureAlgorithm} and {@code signatureSecret}</li>
     *   <li><strong>2.</strong> JWE token using provided: {@link TokenEncryptionAlgorithm}, {@link TokenEncryptionMethod} and {@code encryptionSecret}</li>
     * </ul>
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param encryptionAlgorithm
     *    {@link TokenEncryptionAlgorithm} used to encrypt the JWS token
     * @param encryptionMethod
     *    {@link TokenEncryptionMethod} used to encrypt the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     * @param signatureAlgorithm
     *    {@link TokenSignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS token will be valid
     *
     * @return {@link String} with the JWE
     *
     * @throws IllegalArgumentException if {@code encryptionAlgorithm}, {@code encryptionMethod} or {@code signatureAlgorithm} are {@code null}
     *                                  if {@code encryptionSecret} or {@code signatureSecret} are {@code null} or empty.
     * @throws TokenException if there was a problem generating the JWE token
     */
    public String generateToken(final Map<String, Object> informationToInclude,
                                final TokenEncryptionAlgorithm encryptionAlgorithm,
                                final TokenEncryptionMethod encryptionMethod,
                                final String encryptionSecret,
                                final TokenSignatureAlgorithm signatureAlgorithm,
                                final String signatureSecret,
                                final long expirationTimeInSeconds) {
        String jwsToken = JwsUtil.generateToken(
                informationToInclude,
                signatureAlgorithm,
                signatureSecret,
                expirationTimeInSeconds
        );
        return generateToken(
          jwsToken,
          encryptionAlgorithm,
          encryptionMethod,
          encryptionSecret
        );
    }


    /**
     * Extracts from the given {@code jweToken} all the information included in the payload of the nested JWS one.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param encryptionSecret
     *    {@link String} used to decrypt the JWE token
     * @param signatureSecret
     *    {@link String} used to sign the nested JWS token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload of the nested JWS token inside {@code jweToken}
     *
     * @throws IllegalArgumentException if {@code jweToken}, {@code encryptionSecret} or {@code signatureSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code jweToken} is not a JWE or the nested JWS is not a signed token one or such
     *                               JWS was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jweToken} is valid but its nested JWS has expired
     * @throws TokenException if there was a problem getting claims of {@code jweToken}
     */
    public static Map<String, Object> getAllClaimsFromToken(final String jweToken,
                                                            final String encryptionSecret,
                                                            final String signatureSecret) {
        AssertUtil.hasText(jweToken, "jweToken cannot be null or empty");
        AssertUtil.hasText(encryptionSecret, "encryptionSecret cannot be null or empty");
        AssertUtil.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        String jwsToken = decryptJweToken(
                jweToken,
                encryptionSecret
        );
        return JwsUtil.getAllClaimsFromToken(
                jwsToken,
                signatureSecret
        );
    }


    /**
     *    Returns an {@link Right} with all the information included in the payload of the nested JWS token inside provided
     * {@code jwsToken}. {@link Left} with the {@link Exception} if there was an error trying to extract such payload.
     *
     * @apiNote
     *    The difference between this method and {@link JweUtil#getAllClaimsFromToken(String, String, String)}
     * is: this method does not throw any exception, if there is a problem extracting the payload the {@link Exception}
     * will be added in the {@link Left} element of returned {@link Either}.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param encryptionSecret
     *    {@link String} used to decrypt the JWE token
     * @param signatureSecret
     *    {@link String} used to sign the nested JWS token
     *
     * @return {@link Right} with {@link Map} of {@link String}-{@link Object} with contain of the payload of the nested
     *         JWS token inside {@code jweToken}.
     *         {@link Left} with the {@link Exception} is there was an error during the process.
     */
    public static Either<Exception, Map<String, Object>> getSafeAllClaimsFromToken(final String jweToken,
                                                                                   final String encryptionSecret,
                                                                                   final String signatureSecret) {
        try {
            return right(
                    getAllClaimsFromToken(
                            jweToken,
                            encryptionSecret,
                            signatureSecret
                    )
            );

        } catch (Exception e) {
            log.debug(
                    format("The was an error getting information included in JWE token: %s. %s",
                            jweToken,
                            getFormattedCurrentAndRootError(e)
                    ),
                    e
            );
            return left(e);
        }
    }


    /**
     *    Gets the information included in the nested JWS token of given JWE {@code jweToken} that match with provided
     * {@code keysToInclude}.
     *
     * @apiNote
     *    If {@code keysToInclude} is {@code null} or empty, then an empty {@link Map} is returned.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToInclude
     *    {@link Set} of {@link String} with the {@code key}s to extract from nested JWS token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload of the nested JWS token inside
     *         {@code jweToken} that matches with requested {@code keysToInclude}
     *
     * @throws IllegalArgumentException if {@code jweToken}, {@code signatureSecret} or {@code encryptionSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code jweToken} is not a JWE or the nested JWS is not a signed token one or such
     *                               JWS was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jweToken} is valid but its nested JWS has expired
     * @throws TokenException if there was a problem getting claims of {@code jweToken}
     */
    public Map<String, Object> getPayloadKeys(final String jweToken,
                                              final String encryptionSecret,
                                              final String signatureSecret,
                                              final Set<String> keysToInclude) {
        final Set<String> finalKeysToInclude = getOrElse(
                keysToInclude,
                new HashSet<>()
        );
        return getAllClaimsFromToken(
                jweToken,
                encryptionSecret,
                signatureSecret
        )
        .entrySet().stream()
        .filter(e ->
                finalKeysToInclude.contains(
                        e.getKey()
                )
        )
        .collect(
                toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )
        );
    }


    /**
     *    Gets the information included in the nested JWS token of given JWE {@code jweToken} except the given
     * {@code keysToExclude}.
     *
     * @apiNote
     *    If {@code keysToExclude} is {@code null} or empty, then a {@link Map} containing all data of nested JWS of the
     * given {@code jweToken} is returned.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToExclude
     *    {@link Set} of {@link String} with the {@code key}s to exclude from nested JWS token
     *
     * @return {@link Map} of {@link String}-{@link Object} with contain of the payload of the nested JWS token inside
     *         {@code jweToken} that does not match with {@code keysToExclude}
     *
     * @throws IllegalArgumentException if {@code jweToken}, {@code signatureSecret} or {@code encryptionSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code jweToken} is not a JWE or the nested JWS is not a signed token one or such
     *                               JWS was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jweToken} is valid but its nested JWS has expired
     * @throws TokenException if there was a problem getting claims of {@code jweToken}
     */
    public Map<String, Object> getPayloadExceptKeys(final String jweToken,
                                                    final String encryptionSecret,
                                                    final String signatureSecret,
                                                    final Set<String> keysToExclude) {
        final Set<String> finalKeysToExclude = getOrElse(
                keysToExclude,
                new HashSet<>()
        );
        return getAllClaimsFromToken(
                jweToken,
                encryptionSecret,
                signatureSecret
        )
        .entrySet().stream()
        .filter(e ->
                !finalKeysToExclude.contains(
                        e.getKey()
                )
        )
        .collect(
                toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                )
        );
    }


    /**
     * Returns if the given {@code token} is a JWE one.
     *
     * @param token
     *    {@link String} with the {@code token} to check
     *
     * @return {@code true} if the {@code token} is an JWE one, {@code false} otherwise
     */
    public boolean isJweToken(final String token) {
        try {
            AssertUtil.hasText(token, "token cannot be null or empty");
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
                            getFormattedCurrentAndRootError(e)
                    ),
                    e
            );
            return false;
        }
    }


    /**
     * Encrypts the given JWS token using provided {@link TokenEncryptionAlgorithm} and {@link TokenEncryptionMethod}.
     *
     * @param jwsToken
     *    {@link String} with the JWS token to encrypt
     * @param encryptionAlgorithm
     *    {@link TokenEncryptionAlgorithm} used to encrypt the JWS token
     * @param encryptionMethod
     *    {@link TokenEncryptionMethod} used to encrypt the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link String} with the JWE token
     *
     * @throws TokenInvalidException it the given {@code jwsToken} is not a JWS one
     * @throws TokenException it there was a problem encrypting {@code jwsToken}
     */
    private String encryptJwsToken(final String jwsToken,
                                   final TokenEncryptionAlgorithm encryptionAlgorithm,
                                   final TokenEncryptionMethod encryptionMethod,
                                   final String encryptionSecret) {
        try {
            if (!JwsUtil.isJwsToken(jwsToken)) {
                throw new TokenInvalidException(
                        format("The token: %s is not a JWS one",
                                jwsToken
                        )
                );
            }
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(
                            encryptionAlgorithm.getAlgorithm(),
                            encryptionMethod.getMethod()
                    )
                    .contentType(JWE_CONTENT_TYPE)   // Required to indicate nested JWT
                    .build(),
                    new Payload(jwsToken)
            );
            jweObject.encrypt(
                    getSuitableEncrypter(
                            encryptionAlgorithm,
                            encryptionSecret
                    )
            );
            return jweObject.serialize();

        } catch (Exception e) {
            throw handleMultipleExceptions(
                    e,
                    format("The was a problem trying to encrypt the JWS token: %s using the algorithm: %s and method: %s",
                            jwsToken,
                            ofNullable(encryptionAlgorithm).map(Enum::name).orElse("null"),
                            ofNullable(encryptionMethod).map(Enum::name).orElse("null")
                    )
            );
        }
    }


    /**
     * Decrypts the given JWE token using provided {@code encryptionSecret} and returning the nested JWS token.
     *
     * @param jweToken
     *    {@link String} with the JWE token to decrypt
     * @param encryptionSecret
     *    {@link String} used to decrypt the JWE token
     *
     * @return {@link String} with the JWS nested token
     *
     * @throws TokenInvalidException it the given {@code jwsToken} is not a JWS one
     * @throws TokenException it there was a problem decrypting {@code jweToken}
     */
    private String decryptJweToken(final String jweToken,
                                   final String encryptionSecret) {
        try {
            if (!isJweToken(jweToken)) {
                throw new TokenInvalidException(
                        format("The token: %s is not a JWE one",
                                jweToken
                        )
                );
            }
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(jweToken);
            encryptedJWT.decrypt(
                    getSuitableDecrypter(
                            encryptedJWT,
                            encryptionSecret
                    )
            );
            return encryptedJWT.getPayload()
                    .toSignedJWT()
                    .serialize();

        } catch (Exception e) {
            throw handleMultipleExceptions(
                    e,
                    format("The was a problem trying to decrypt the JWE token: %s",
                            jweToken
                    )
            );
        }
    }


    /**
     * Returns the suitable {@link JWEEncrypter} taking into account the provided {@link TokenEncryptionAlgorithm}.
     *
     * @param encryptionAlgorithm
     *    {@link TokenEncryptionAlgorithm} used to encrypt the JWE token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     *
     * @return {@link JWEEncrypter}
     *
     * @throws TokenException if it was not possible to find a suitable {@link JWEEncrypter} or there was an error
     *                        creating it
     */
    private static JWEEncrypter getSuitableEncrypter(final TokenEncryptionAlgorithm encryptionAlgorithm,
                                                     final String encryptionSecret) {
        try {
            return switch (encryptionAlgorithm) {
                case DIR ->
                        new DirectEncrypter(
                                encryptionSecret.getBytes()
                        );

                case RSA_OAEP_256, RSA_OAEP_384, RSA_OAEP_512 ->
                        new RSAEncrypter(
                                RSAKey.parseFromPEMEncodedObjects(
                                    encryptionSecret
                                )
                                .toRSAKey()
                        );

                case ECDH_1PU_A128KW, ECDH_1PU_A192KW, ECDH_1PU_A256KW -> {
                    ECKey key = ECKey.parseFromPEMEncodedObjects(
                                   encryptionSecret
                                )
                                .toECKey();

                    yield new ECDH1PUEncrypter(
                            key.toECPrivateKey(),
                            key.toECPublicKey()
                    );
                }

                case null, default ->
                        throw new TokenException(
                                format("It was not possible to find a suitable encrypter for the encryption algorithm: %s",
                                        ofNullable(encryptionAlgorithm).map(Enum::name).orElse("null")
                                )
                        );
            };

        } catch (Exception e) {
            throw handleMultipleExceptions(
                    e,
                    format("The was a problem trying to create the suitable encrypter for the encryption algorithm: %s",
                            ofNullable(encryptionAlgorithm).map(Enum::name).orElse("null")
                    )
            );
        }
    }


    /**
     *    Returns the suitable {@link JWEDecrypter} taking into account the {@link TokenEncryptionAlgorithm} used to
     * encrypt the given JWE token {@code jweObject}.
     *
     * @param encryptedJWT
     *    {@link EncryptedJWT} with JWE token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWE token
     *
     * @return {@link JWEDecrypter}
     *
     * @throws TokenException if it was not possible to find a suitable {@link JWEDecrypter} or there was an error
     *                        creating it
     */
    private static JWEDecrypter getSuitableDecrypter(final EncryptedJWT encryptedJWT,
                                                     final String encryptionSecret) {
        TokenEncryptionAlgorithm encryptionAlgorithm = TokenEncryptionAlgorithm.getByAlgorithm(
                encryptedJWT.getHeader().getAlgorithm()
        ).orElse(null);

        try {
            return switch (encryptionAlgorithm) {
                case DIR ->
                        new DirectDecrypter(
                                encryptionSecret.getBytes()
                        );

                case RSA_OAEP_256, RSA_OAEP_384, RSA_OAEP_512 ->
                        new RSADecrypter(
                                RSAKey.parseFromPEMEncodedObjects(
                                   encryptionSecret
                                )
                                .toRSAKey()
                        );

                case ECDH_1PU_A128KW, ECDH_1PU_A192KW, ECDH_1PU_A256KW -> {
                    ECKey key = ECKey.parseFromPEMEncodedObjects(
                                   encryptionSecret
                                )
                                .toECKey();

                    yield new ECDH1PUDecrypter(
                            key.toECPrivateKey(),
                            key.toECPublicKey()
                    );
                }

                case null, default ->
                        throw new TokenException(
                                format("It was not possible to find a suitable decrypter for the encryption algorithm: %s",
                                        ofNullable(encryptionAlgorithm).map(Enum::name).orElse("null")
                                )
                        );
            };

        } catch (Exception e) {
            throw handleMultipleExceptions(
                    e,
                    format("The was a problem trying to create the suitable decrypter for the encryption algorithm: %s",
                            ofNullable(encryptionAlgorithm).map(Enum::name).orElse("null")
                    )
            );
        }
    }


    /**
     * Method used to avoid nested {@link TokenException}s with very similar error messages.
     *
     * @param sourceException
     *    Original {@link Exception} to check
     * @param errorMessageIfNoTokenException
     *    {@link String} with the error message to include in the returned {@link Exception} if {@code sourceException}
     *    is not a {@link TokenException} one
     *
     * @return {@link TokenException}
     */
    private static TokenException handleMultipleExceptions(final Exception sourceException,
                                                           final String errorMessageIfNoTokenException) {
        if (sourceException instanceof TokenException) {
            return (TokenException) sourceException;
        }
        return new TokenException(
                format("%s. %s",
                        errorMessageIfNoTokenException,
                        getFormattedRootError(sourceException)
                ),
                sourceException
        );
    }

}
