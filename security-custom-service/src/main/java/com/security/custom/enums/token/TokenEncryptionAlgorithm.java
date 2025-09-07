package com.security.custom.enums.token;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.security.custom.exception.token.TokenException;
import com.security.custom.interfaces.ITokenEncryptionAlgorithm;
import com.spring6microservices.common.core.util.EnumUtil;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Allowed algorithms to encrypt a JWS token.
 * <p>
 * Regarding how to generate a valid encryption keys according to the selected algorithm:
 * <p>
 * <ul>
 *   <li>{@link TokenEncryptionAlgorithm#ECDH_1PU_A128KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A192KW}, {@link TokenEncryptionAlgorithm#ECDH_1PU_A256KW}:
 *        using <a href="https://www.openssl.org/">openssl</a>
 *        <pre>
 *          1.1 openssl ecparam -genkey -name prime256v1 -noout -out key.pem
 *          1.2 openssl ecparam -genkey -name secp384r1 -noout -out key.pem
 *          1.3 openssl ecparam -genkey -name secp521r1 -noout -out key.pem
 *
 *          2. openssl ec -in key.pem -pubout -out public.pem
 *
 *          3. cat public.pem key.pem > keypair.pem
 *        </pre>
 *   </li>
 *   <li>{@link TokenEncryptionAlgorithm#RSA_OAEP_256}, {@link TokenEncryptionAlgorithm#RSA_OAEP_384}, {@link TokenEncryptionAlgorithm#RSA_OAEP_512}:
 *        using <a href="https://www.openssl.org/">openssl</a>
 *        <pre>
 *          1. openssl genrsa -out key.pem 2048
 *          2. openssl rsa -in key.pem -pubout -out public.pem
 *          3. cat public.pem key.pem > keypair.pem
 *        </pre>
 *   </li>
 * </ul>
 */
@Getter
public enum TokenEncryptionAlgorithm implements ITokenEncryptionAlgorithm {

    DIR(JWEAlgorithm.DIR) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                return new DirectEncrypter(
                        encryptionSecret.getBytes()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                return new DirectDecrypter(
                        encryptionSecret.getBytes()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    ECDH_1PU_A128KW(JWEAlgorithm.ECDH_1PU_A128KW) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                    encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUEncrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUDecrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    ECDH_1PU_A192KW(JWEAlgorithm.ECDH_1PU_A192KW) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                    encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUEncrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUDecrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    ECDH_1PU_A256KW(JWEAlgorithm.ECDH_1PU_A256KW) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUEncrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                ECKey key = ECKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                            )
                            .toECKey();

                return new ECDH1PUDecrypter(
                        key.toECPrivateKey(),
                        key.toECPublicKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    RSA_OAEP_256(JWEAlgorithm.RSA_OAEP_256) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                return new RSAEncrypter(
                        RSAKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                return new RSADecrypter(
                        RSAKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    RSA_OAEP_384(JWEAlgorithm.RSA_OAEP_384) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                return new RSAEncrypter(
                        RSAKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                return new RSADecrypter(
                        RSAKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    },
    RSA_OAEP_512(JWEAlgorithm.RSA_OAEP_512) {

        @Override
        public JWEEncrypter getEncrypter(final String encryptionSecret) {
            try {
                return new RSAEncrypter(
                        RSAKey.parseFromPEMEncodedObjects(
                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEEncrypter of: " + this.name(),
                        t
                );
            }
        }


        @Override
        public JWEDecrypter getDecrypter(final String encryptionSecret) {
            try {
                return new RSADecrypter(
                        RSAKey.parseFromPEMEncodedObjects(

                                encryptionSecret
                        )
                        .toRSAKey()
                );
            } catch (Throwable t) {
                throw new TokenException(
                        "There was an error returning the JWEDecrypter of: " + this.name(),
                        t
                );
            }
        }
    };


    private final JWEAlgorithm algorithm;


    TokenEncryptionAlgorithm(JWEAlgorithm algorithm) {
        this.algorithm = algorithm;
    }


    /**
     * Gets the {@link TokenEncryptionAlgorithm} that matches with the given one.
     *
     * @param algorithm
     *    {@link JWEAlgorithm} to search
     *
     * @return {@link Optional} with {@link TokenEncryptionAlgorithm} if {@code algorithm} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<TokenEncryptionAlgorithm> getByAlgorithm(@Nullable final JWEAlgorithm algorithm) {
        return EnumUtil.getByInternalProperty(
                TokenEncryptionAlgorithm.class,
                algorithm,
                TokenEncryptionAlgorithm::getAlgorithm
        );
    }

}
