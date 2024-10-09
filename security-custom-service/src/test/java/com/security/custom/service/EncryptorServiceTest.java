package com.security.custom.service;

import com.spring6microservices.common.core.util.StringUtil;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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


    static Stream<Arguments> decryptTestCases() {
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
    @MethodSource("decryptTestCases")
    @DisplayName("decrypt: test cases")
    public void decrypt_testCases(String encryptedText,
                                  String expectedResult) {
        when(mockTextEncryptor.decrypt(anyString()))
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.decrypt(encryptedText)
        );
        VerificationMode timesInvoked = null == encryptedText
                ? times(0)
                : times(1);

        verify(mockTextEncryptor, timesInvoked)
                .decrypt(
                        anyString()
                );
    }

}
