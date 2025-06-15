package com.security.custom.application.spring6microservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(
        SpringExtension.class
)
public class Spring6MicroserviceAuthorizationServiceTest {

    private Spring6MicroserviceAuthorizationService service;


    @BeforeEach
    public void init() {
        service = new Spring6MicroserviceAuthorizationService();
    }


    static Stream<Arguments> getAdditionalAuthorizationInformationTestCases() {
        Map<String, Object> rawAuthorizationInformationNoUsername = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
        }};
        Map<String, Object> rawAuthorizationInformationComplete = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        Map<String, Object> expectedResultNoUsername = new LinkedHashMap<>() {{
            put("authorities", Set.of("admin", "user"));
        }};
        Map<String, Object> expectedResultComplete = new LinkedHashMap<>() {{
            put("username", "username value");
            put("authorities", Set.of("admin", "user"));
        }};
        return Stream.of(
                //@formatter:off
                //            rawAuthorizationInformation,             expectedResult
                Arguments.of( null,                                    new HashMap<>() ),
                Arguments.of( new HashMap<>(),                         new HashMap<>() ),
                Arguments.of( rawAuthorizationInformationNoUsername,   expectedResultNoUsername ),
                Arguments.of( rawAuthorizationInformationComplete,     expectedResultComplete )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAdditionalAuthorizationInformationTestCases")
    @DisplayName("getAdditionalAuthorizationInformation: test cases")
    public void getAdditionalAuthorizationInformation_testCases(Map<String, Object> rawAuthorizationInformation,
                                                                Map<String, Object> expectedResult) {
        assertEquals(
                expectedResult,
                service.getAdditionalAuthorizationInformation(rawAuthorizationInformation)
        );
    }


    static Stream<Arguments> getAuthoritiesTestCases() {
        Map<String, Object> rawAuthorizationInformation = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            rawAuthorizationInformation,   expectedResult
                Arguments.of( null,                          new HashSet<>() ),
                Arguments.of( new HashMap<>(),               new HashSet<>() ),
                Arguments.of( rawAuthorizationInformation,   Set.of("admin", "user") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAuthoritiesTestCases")
    @DisplayName("getAuthorities: test cases")
    public void getAuthorities_testCases(Map<String, Object> rawAuthorizationInformation,
                                         Set<String> expectedResult) {
        assertEquals(
                expectedResult,
                service.getAuthorities(rawAuthorizationInformation)
        );
    }


    static Stream<Arguments> getUsernameTestCases() {
        Map<String, Object> rawAuthorizationInformation = new LinkedHashMap<>() {{
            put("name", "name value");
            put("exp", new Date(5000000000L * 1000));
            put("iat", new Date(1700000000L * 1000));
            put("age", 23L);
            put("authorities", List.of("admin", "user"));
            put("username", "username value");
        }};
        return Stream.of(
                //@formatter:off
                //            rawAuthorizationInformation,   expectedResult
                Arguments.of( null,                          empty() ),
                Arguments.of( new HashMap<>(),               empty() ),
                Arguments.of( rawAuthorizationInformation,   of("username value") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getUsernameTestCases")
    @DisplayName("getUsername: test cases")
    public void getUsername_testCases(Map<String, Object> rawAuthorizationInformation,
                                      Optional<String> expectedResult) {
        assertEquals(
                expectedResult,
                service.getUsername(rawAuthorizationInformation)
        );
    }

}
