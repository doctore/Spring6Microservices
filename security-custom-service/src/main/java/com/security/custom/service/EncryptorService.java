package com.security.custom.service;

import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import static com.security.custom.configuration.security.EncryptionConfiguration.CIPHER_SECRET_PREFIX;
import static com.security.custom.configuration.Constants.UTF_8;
import static java.util.Optional.ofNullable;

@Service
public class EncryptorService {

    private final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES";
    private final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    private final int ITERATION_COUNT = 1024;
    private final int KEY_LENGTH = 256;

    private final int TAG_LENGTH_BIT = 128;
    private final int IV_LENGTH_BYTE = 12;
    private final int SALT_LENGTH_BYTE = 16;

    /**
     *    Default {@link TextEncryptor} based on the configured environment variable: {@code ENCRYPT_KEY}
     * (default symmetric encryption key).
     */
    private final TextEncryptor textEncryptor;


    @Autowired
    public EncryptorService(final TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }


    /**
     *    Decrypts the given {@code encryptedText} using default {@link TextEncryptor}, based on the configured
     * environment variable: {@code ENCRYPT_KEY} (default symmetric encryption key).
     *
     * @param encryptedText
     *    {@link String} to decrypt
     *
     * @return {@link String} with decrypted text
     */
    public String defaultDecrypt(final String encryptedText) {
        return ofNullable(encryptedText)
                .map(eText ->
                        textEncryptor.decrypt(
                                encryptedText.replace(
                                        CIPHER_SECRET_PREFIX,
                                        ""
                                )
                        )
                )
                .orElse(
                        StringUtil.EMPTY_STRING
                );
    }


    /**
     *    Encrypts the given {@code textToEncrypt} using default {@link TextEncryptor}, based on the configured
     * environment variable: {@code ENCRYPT_KEY} (default symmetric encryption key).
     *
     * @param textToEncrypt
     *    {@link String} to encrypt
     *
     * @return {@link String} with encrypted text
     */
    public String defaultEncrypt(final String textToEncrypt) {
        return ofNullable(textToEncrypt)
                .map(textEncryptor::encrypt)
                .orElse(
                        StringUtil.EMPTY_STRING
                );
    }


    /**
     *    Encrypt {@code toEncrypt} using the provided {@code password}. The output consist of iv, password's salt,
     * encrypted content and auth tag in the following format:
     *
     * <pre>
     *   output = byte[] {i i i s s s c c c c c c ...}
     *
     *    i = IV bytes
     *    s = Salt bytes
     *    c = content bytes (encrypted content)
     * </pre>
     *
     * @param toEncrypt
     *    {@link String} to encrypt.
     * @param password
     *    {@link String} password used to encrypt {@code toEncrypt}.
     *
     * @return encrypted {@code toEncrypt}
     *
     * @throws IllegalArgumentException if {@code toEncrypt} or {@code password} are {@code null}
     * @throws GeneralSecurityException if there was a problem encrypting the given {@code toEncrypt}
     */
    public String encrypt(final String toEncrypt,
                          final String password) throws Exception {
        AssertUtil.notNull(toEncrypt, "toEncrypt must be not null");
        AssertUtil.notNull(password, "password must be not null");

        // 16 bytes salt
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);

        // Recommended 12 bytes iv
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);

        // Secret key from password
        SecretKey aesKeyFromPassword = getSecretKey(
                SYMMETRIC_ENCRYPTION_ALGORITHM,
                SECRET_KEY_ALGORITHM,
                password.toCharArray(),
                salt,
                ITERATION_COUNT,
                KEY_LENGTH
        );
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

        // ASE-GCM needs GCMParameterSpec
        cipher.init(
                Cipher.ENCRYPT_MODE,
                aesKeyFromPassword,
                new GCMParameterSpec(TAG_LENGTH_BIT, iv)
        );
        byte[] cipherText = cipher.doFinal(
                toEncrypt.getBytes(UTF_8)
        );

        // Prefix IV and Salt to cipher text
        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();

        // String representation, base64, send this string to other for decryption
        return Base64.getEncoder()
                .encodeToString(cipherTextWithIvSalt);
    }


    /**
     *    Decrypt {@code toDecrypt} using the provided {@code password}. The given {@code toDecrypt} should have been
     * encrypted using the {@code encrypt} function, due to have to follow the output format.
     *
     * @param toDecrypt
     *    {@link String} to decrypt.
     * @param password
     *    {@link String} password used to decrypt {@code toDecrypt}.
     *
     * @return decrypted {@code toDecrypt}
     *
     * @throws IllegalArgumentException if {@code toDecrypt} or {@code password} are {@code null}
     * @throws GeneralSecurityException if there was a problem decrypting the given {@code toDecrypt}
     */
    public String decrypt(final String toDecrypt,
                          final String password) throws Exception {
        AssertUtil.notNull(toDecrypt, "toDecrypt must be not null");
        AssertUtil.notNull(password, "password must be not null");

        byte[] decode = Base64.getDecoder()
                .decode(
                        toDecrypt.getBytes(UTF_8)
                );

        // Get back the iv and salt from the cipher text
        ByteBuffer bb = ByteBuffer.wrap(decode);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        bb.get(salt);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        // Get back the aes key from the same password and salt
        SecretKey aesKeyFromPassword = getSecretKey(
                SYMMETRIC_ENCRYPTION_ALGORITHM,
                SECRET_KEY_ALGORITHM,
                password.toCharArray(),
                salt,
                ITERATION_COUNT,
                KEY_LENGTH
        );
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(
                Cipher.DECRYPT_MODE,
                aesKeyFromPassword,
                new GCMParameterSpec(
                        TAG_LENGTH_BIT,
                        iv
                )
        );
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(
                plainText,
                UTF_8
        );
    }


    /**
     * Generates a {@link SecretKey} instance.
     *
     * @param symmetricAlgorithm
     *    The name of the secret-key algorithm to be associated with the given key material. See the Java Security
     *    Standard Algorithm Names document for information about standard algorithm names
     * @param secretKeyAlgorithm
     *    The standard name of the requested secret-key algorithm. See the SecretKeyFactory section in the Java
     *    Security Standard Algorithm Names Specification for information about standard algorithm names
     * @param password
     *    The password
     * @param salt
     *    Salt used to increase the security.
     * @param iterationCount
     *    Number of iterations over the given {@code password} and {@code salt}.
     * @param keyLength
     *    The to-be-derived key length
     *
     * @return {@link SecretKey}
     *
     * @throws NoSuchAlgorithmException if {@code secretKeyAlgorithm} is not available
     * @throws InvalidKeySpecException if there was an error generating returned {@link SecretKey}
     */
    private SecretKey getSecretKey(final String symmetricAlgorithm,
                                   final String secretKeyAlgorithm,
                                   final char[] password,
                                   final byte[] salt,
                                   final int iterationCount,
                                   final int keyLength) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyAlgorithm);
        KeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterationCount,
                keyLength
        );
        return new SecretKeySpec(
                factory.generateSecret(spec).getEncoded(),
                symmetricAlgorithm
        );
    }


    private byte[] getRandomNonce(final int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

}
