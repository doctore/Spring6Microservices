package com.spring6microservices.common.spring.validator.enums;

import com.spring6microservices.common.spring.enums.PizzaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IEnumInternalPropertyValueTest {

    static Stream<Arguments> getByInternalPropertyValueTestCases() {
        return Stream.of(
                //@formatter:off
                //            enumClass,         internalPropertyValue,                              expectedResult
                Arguments.of( null,              null,                                               empty() ),
                Arguments.of( PizzaEnum.class,   null,                                               empty() ),
                Arguments.of( PizzaEnum.class,   "Not found",                                        empty() ),
                Arguments.of( PizzaEnum.class,   PizzaEnum.FOUR_CHEESE.getInternalPropertyValue(),   of(PizzaEnum.FOUR_CHEESE) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByInternalPropertyValueTestCases")
    @DisplayName("getByInternalPropertyValue: test cases")
    public <E extends Enum<? extends IEnumInternalPropertyValue<T>>, T> void getByInternalPropertyValue_testCases(Class<E> enumClass,
                                                                                                                  T internalPropertyValue,
                                                                                                                  Optional<E> expectedResult) {
        Optional<E> result = IEnumInternalPropertyValue.getByInternalPropertyValue(
                enumClass,
                internalPropertyValue
        );
        if (expectedResult.isEmpty()) {
            assertTrue(result.isEmpty());
        }
        else {
            assertTrue(result.isPresent());
            assertEquals(
                    expectedResult.get(),
                    result.get()
            );
        }
    }


    static Stream<Arguments> getInternalPropertyValuesTestCases() {
        List<String> expectedValues = Arrays.stream(PizzaEnum.values())
                .map(PizzaEnum::getInternalPropertyValue)
                .toList();
        return Stream.of(
                //@formatter:off
                //            enumClass,         expectedResult
                Arguments.of( null,              new ArrayList<>() ),
                Arguments.of( PizzaEnum.class,   expectedValues )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getInternalPropertyValuesTestCases")
    @DisplayName("getInternalPropertyValues: test cases")
    public <E extends Enum<? extends IEnumInternalPropertyValue<T>>, T> void getInternalPropertyValues_testCases(Class<E> enumClass,
                                                                                                                 List<T> expectedResult) {
        assertEquals(
                expectedResult,
                IEnumInternalPropertyValue.getInternalPropertyValues(
                        enumClass
                )
        );
    }

}
