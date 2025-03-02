package com.security.custom.enums.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTypeTest {

    @Test
    @DisplayName("getRequiredEncryptionAlgorithm: test cases")
    public void getRequiredEncryptionAlgorithm_testCases() {
        Set<TokenType> tokenTypes = TokenType.getRequiredEncryptionAlgorithm();

        assertNotNull(tokenTypes);
        assertEquals(
                2,
                tokenTypes.size()
        );
        assertTrue(
                tokenTypes.contains(
                        TokenType.JWE
                )
        );
        assertTrue(
                tokenTypes.contains(
                        TokenType.ENCRYPTED_JWE
                )
        );
    }


    @Test
    @DisplayName("getEncryptedTokenTypes: test cases")
    public void getEncryptedTokenTypes_testCases() {
        Set<TokenType> tokenTypes = TokenType.getEncryptedTokenTypes();

        assertNotNull(tokenTypes);
        assertEquals(
                2,
                tokenTypes.size()
        );
        assertTrue(
                tokenTypes.contains(
                        TokenType.ENCRYPTED_JWS
                )
        );
        assertTrue(
                tokenTypes.contains(
                        TokenType.ENCRYPTED_JWE
                )
        );
    }

}
