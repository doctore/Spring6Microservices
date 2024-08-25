package com.security.jwt.enums.token;

import com.nimbusds.jose.EncryptionMethod;
import lombok.Getter;

@Getter
public enum TokenEncryptionMethod {

    A128CBC_HS256(EncryptionMethod.A128CBC_HS256),
    A192CBC_HS384(EncryptionMethod.A192CBC_HS384),
    A256CBC_HS512(EncryptionMethod.A256CBC_HS512),
    A128GCM(EncryptionMethod.A128GCM),
    A192GCM(EncryptionMethod.A192GCM),
    A256GCM(EncryptionMethod.A256GCM),
    XC20P(EncryptionMethod.XC20P);

    private final EncryptionMethod method;


    TokenEncryptionMethod(EncryptionMethod method) {
        this.method = method;
    }

}
