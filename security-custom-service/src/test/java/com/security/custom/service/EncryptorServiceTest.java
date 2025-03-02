package com.security.custom.service;

import com.spring6microservices.common.core.util.StringUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EncryptorServiceTest {

    @Mock
    private TextEncryptor mockTextEncryptor;

    private EncryptorService service;


    @BeforeEach
    public void init() {
        service = new EncryptorService(
                mockTextEncryptor
        );
    }


    static Stream<Arguments> defaultDecryptTestCases() {
        String decryptedText = "ItIsNotImportant";
        return Stream.of(
                //@formatter:off
                //            encryptedText,          expectedResult
                Arguments.of( null,                   StringUtil.EMPTY_STRING ),
                Arguments.of( "ItDoesNotCare",        decryptedText ),
                Arguments.of( "{cipher}sourceText",   decryptedText )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("defaultDecryptTestCases")
    @DisplayName("defaultDecrypt: test cases")
    public void defaultDecrypt_testCases(String encryptedText,
                                         String expectedResult) {
        when(mockTextEncryptor.decrypt(anyString()))
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.defaultDecrypt(encryptedText)
        );
        VerificationMode timesInvoked = null == encryptedText
                ? times(0)
                : times(1);

        verify(mockTextEncryptor, timesInvoked)
                .decrypt(
                        anyString()
                );
    }


    static Stream<Arguments> defaultEncryptTestCases() {
        String encryptedText = "ItIsNotImportant";
        return Stream.of(
                //@formatter:off
                //            textToEncrypt,     expectedResult
                Arguments.of( null,              StringUtil.EMPTY_STRING ),
                Arguments.of( "ItDoesNotCare",   encryptedText )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("defaultEncryptTestCases")
    @DisplayName("defaultEncrypt: test cases")
    public void defaultEncrypt_testCases(String textToEncrypt,
                                         String expectedResult) {
        when(mockTextEncryptor.encrypt(eq(textToEncrypt)))
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.defaultEncrypt(textToEncrypt)
        );
        VerificationMode timesInvoked = null == textToEncrypt
                ? times(0)
                : times(1);

        verify(mockTextEncryptor, timesInvoked)
                .encrypt(
                        eq(textToEncrypt)
                );
    }


    static Stream<Arguments> encryptTestCases() {
        String toEncrypt = "Raw information to encrypt";
        String password = "23Rhf(@_2-Poas";
        return Stream.of(
                //@formatter:off
                //            toEncrypt,   password,   expectedException
                Arguments.of( null,        null,       IllegalArgumentException.class ),
                Arguments.of( toEncrypt,   null,       IllegalArgumentException.class ),
                Arguments.of( toEncrypt,   password,   null )
        ); //@formatter:on
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("encryptTestCases")
    @DisplayName("encrypt: test cases")
    public void encrypt_testCases(String toEncrypt,
                                  String password,
                                  Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.encrypt(toEncrypt, password)
            );
        } else {
            assertNotNull(service.encrypt(toEncrypt, password));
        }
    }


    static Stream<Arguments> decryptTestCases() {
        String toDecrypt = "SGB2pnNj1NUZ7IKV6RpEes/cv76rwV/0fNopGgnIuVeyuy1wdOomylv4i0geFJBHQe0B4VAyjcWGD6gpCEtszrGwcDiu1w==";
        String password = "23Rhf(@_2-Poas";
        String decrypted = "Raw information to encrypt";
        return Stream.of(
                //@formatter:off
                //            toDecrypt,   password,   expectedException,                expectedResult
                Arguments.of( null,        null,       IllegalArgumentException.class,   null ),
                Arguments.of( toDecrypt,   null,       IllegalArgumentException.class,   null ),
                Arguments.of( toDecrypt,   password,   null,                             decrypted )
        ); //@formatter:on
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("decryptTestCases")
    @DisplayName("decrypt: test cases")
    public void decrypt_testCases(String toDecrypt,
                                  String password, Class<? extends Exception> expectedException,
                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> service.decrypt(toDecrypt, password)
            );
        } else {
            assertEquals(
                    expectedResult,
                    service.decrypt(toDecrypt, password)
            );
        }
    }

}
