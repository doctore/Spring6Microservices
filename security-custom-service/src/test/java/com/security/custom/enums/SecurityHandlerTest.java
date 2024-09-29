package com.security.custom.enums;

import com.security.custom.exception.ApplicationClientNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.security.custom.enums.SecurityHandler.getByApplicationClientId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SecurityHandlerTest {

    static Stream<Arguments> getByApplicationClientIdTestCases() {
        return Stream.of(
                //@formatter:off
                //            applicationClientId,                                              expectedException,                          expectedResult
                Arguments.of( null,                                                             ApplicationClientNotFoundException.class,   null ),
                Arguments.of( "NotFound",                                                       ApplicationClientNotFoundException.class,   null ),
                Arguments.of( SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId(),   null,                                       SecurityHandler.SPRING6_MICROSERVICES )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByApplicationClientIdTestCases")
    @DisplayName("getByApplicationClientId: test cases")
    public void getByApplicationClientId_testCases(String applicationClientId,
                                                   Class<? extends Exception> expectedException,
                                                   SecurityHandler expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> getByApplicationClientId(applicationClientId)
            );
        } else {
            assertEquals(
                    expectedResult,
                    getByApplicationClientId(applicationClientId)
            );
        }
    }

}
