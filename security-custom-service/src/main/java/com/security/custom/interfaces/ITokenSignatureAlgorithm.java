package com.security.custom.interfaces;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.exception.token.TokenException;

/**
 * Required methods to implement by every algorithm included in {@link TokenSignatureAlgorithm}.
 */
public interface ITokenSignatureAlgorithm {

    /**
     * Returns the suitable {@link JWSSigner} for the current {@link TokenSignatureAlgorithm} used to sing a JWS token.
     *
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link JWSSigner}
     *
     * @throws TokenException if there is an error creating the {@link JWSSigner}
     */
    JWSSigner getSigner(final String signatureSecret);


    /**
     * Returns the suitable {@link JWSSigner} taking into account the current {@link TokenSignatureAlgorithm}.
     *
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return @return {@link JWSSigner}
     *
     * @throws TokenException if there is an error creating the {@link JWSVerifier}
     */
    JWSVerifier getVerifier(final String signatureSecret);

}
