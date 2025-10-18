package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.enums.PizzaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.EnumUtil.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnumUtilTest {

    static Stream<Arguments> getByInternalPropertyTestCases() {
        return Stream.of(
                //@formatter:off
                //            enumClass,         internalPropertyValueToSearch,            mapper,                          expectedException,                expectedResult
                Arguments.of( null,              null,                                     null,                            null,                             empty() ),
                Arguments.of( null,              "ItDoesNotCare",                          null,                            null,                             empty() ),
                Arguments.of( null,              "ItDoesNotCare",                          PIZZA_ENUM_GET_DATABASE_VALUE,   null,                             empty() ),
                Arguments.of( PizzaEnum.class,   null,                                     null,                            IllegalArgumentException.class,   null ),
                Arguments.of( PizzaEnum.class,   "ItDoesNotCare",                          null,                            IllegalArgumentException.class,   null ),
                Arguments.of( PizzaEnum.class,   "ItDoesNotCare",                          PIZZA_ENUM_GET_DATABASE_VALUE,   null,                             empty() ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.NULL.getDatabaseValue(),        PIZZA_ENUM_GET_DATABASE_VALUE,   null,                             of(PizzaEnum.NULL) ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.CARBONARA.getDatabaseValue(),   PIZZA_ENUM_GET_DATABASE_VALUE,   null,                             of(PizzaEnum.CARBONARA) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByInternalPropertyTestCases")
    @DisplayName("getByInternalProperty: test cases")
    public <T, E extends Enum<E>> void getByInternalProperty_testCases(Class<E> enumClass,
                                                                       T internalPropertyValueToSearch,
                                                                       Function<? super E, ? extends T> mapper,
                                                                       Class<? extends Exception> expectedException,
                                                                       Optional<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () ->
                            getByInternalProperty(
                                    enumClass, internalPropertyValueToSearch, mapper
                            )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    getByInternalProperty(
                            enumClass, internalPropertyValueToSearch, mapper
                    )
            );
        }
    }


    static Stream<Arguments> getByNameIgnoreCaseTestCases() {
        return Stream.of(
                //@formatter:off
                //            enumClass,         enumName,                                    expectedResult
                Arguments.of( null,              null,                                        empty() ),
                Arguments.of( null,              "ItDoesNotCare",                             empty() ),
                Arguments.of( PizzaEnum.class,   null,                                        empty() ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.CARBONARA.name().toLowerCase(),    of(PizzaEnum.CARBONARA) ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.MARGUERITA.name().toUpperCase(),   of(PizzaEnum.MARGUERITA) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByNameIgnoreCaseTestCases")
    @DisplayName("getByNameIgnoreCase: test cases")
    public <E extends Enum<E>> void getByNameIgnoreCase_testCases(Class<E> enumClass,
                                                                  String enumName,
                                                                  Optional<E> expectedResult) {
        assertEquals(
                expectedResult,
                getByNameIgnoreCase(enumClass, enumName)
        );
    }


    static Stream<Arguments> getByNameIgnoreCaseOrThrowTestCases() {
        return Stream.of(
                //@formatter:off
                //            enumClass,         enumName,                                    expectedException,                expectedResult
                Arguments.of( null,              null,                                        IllegalArgumentException.class,   null ),
                Arguments.of( null,              "ItDoesNotCare",                             IllegalArgumentException.class,   null ),
                Arguments.of( PizzaEnum.class,   null,                                        IllegalArgumentException.class,   null ),
                Arguments.of( PizzaEnum.class,   "NotFound",                                  IllegalArgumentException.class,   null ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.CARBONARA.name().toLowerCase(),    null,                             PizzaEnum.CARBONARA ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.MARGUERITA.name().toUpperCase(),   null,                             PizzaEnum.MARGUERITA )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByNameIgnoreCaseOrThrowTestCases")
    @DisplayName("getByNameIgnoreCaseOrThrow: test cases")
    public <E extends Enum<E>> void getByNameIgnoreCaseOrThrow_testCases(Class<E> enumClass,
                                                                         String enumName,
                                                                         Class<? extends Exception> expectedException,
                                                                         E expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () ->
                            getByNameIgnoreCaseOrThrow(
                                    enumClass, enumName
                            )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    getByNameIgnoreCaseOrThrow(
                            enumClass, enumName
                    )
            );
        }
    }


    static Stream<Arguments> getEnumListTestCases() {
        List<PizzaEnum> expectedEnumValues = Arrays.asList(
                PizzaEnum.values()
        );
        return Stream.of(
                //@formatter:off
                //            enumClass,         expectedResult
                Arguments.of( null,              List.of() ),
                Arguments.of( PizzaEnum.class,   expectedEnumValues )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getEnumListTestCases")
    @DisplayName("getEnumList: test cases")
    public <E extends Enum<E>> void getEnumList_testCases(Class<E> enumClass,
                                                          List<E> expectedResult) {
        assertEquals(
                expectedResult,
                getEnumList(enumClass)
        );
    }


    private static final Function<PizzaEnum, String> PIZZA_ENUM_GET_DATABASE_VALUE = PizzaEnum::getDatabaseValue;

}
