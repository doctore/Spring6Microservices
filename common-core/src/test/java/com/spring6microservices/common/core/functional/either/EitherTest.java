package com.spring6microservices.common.core.functional.either;

import com.spring6microservices.common.core.functional.Try.Try;
import com.spring6microservices.common.core.functional.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EitherTest {

    static Stream<Arguments> rightTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Either.right(null) ),
                Arguments.of( 1,       Either.right(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("rightTestCases")
    @DisplayName("right: test cases")
    public <L, R> void right_testCases(R value,
                                       Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                Either.right(value)
        );
    }


    static Stream<Arguments> leftTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,       expectedResult
                Arguments.of( null,        Either.left(null) ),
                Arguments.of( "problem",   Either.left("problem") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("leftTestCases")
    @DisplayName("left: test cases")
    public <L, R> void left_testCases(L value,
                                      Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                Either.left(value)
        );
    }


    static Stream<Arguments> combineTestCases() {
        Either<String, Integer> right1 = Either.right(11);
        Either<String, Integer> right2 = Either.right(20);
        Either<String, Integer> left1 = Either.left("error");
        Either<String, Integer> left2 = Either.left("warning");
        Either<String, Integer>[] allEithersArray = new Either[] { right1, left1, right2, left2 };

        Either<String, Integer> rightResult = Either.right(31);
        Either<String, Integer> leftResult = Either.left("errorwarning");
        return Stream.of(
                //@formatter:off
                //            mapperLeft,       mapperRight,    eithers,                                  expectedException,                expectedResult
                Arguments.of( null,             null,           null,                                     null,                             Right.empty() ),
                Arguments.of( CONCAT_STRINGS,   null,           null,                                     null,                             Right.empty() ),
                Arguments.of( null,             ADD_INTEGERS,   null,                                     null,                             Right.empty() ),
                Arguments.of( null,             null,           new Either[] { right1 },                  IllegalArgumentException.class,   null ),
                Arguments.of( CONCAT_STRINGS,   null,           new Either[] { right1 },                  IllegalArgumentException.class,   null ),
                Arguments.of( null,             ADD_INTEGERS,   new Either[] { right1 },                  IllegalArgumentException.class,   null ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   null,                                     null,                             Right.empty() ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] {},                          null,                             Right.empty() ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] { right1 },                  null,                             right1 ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] { right1, right2 },          null,                             rightResult ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] { left1, right1, right2 },   null,                             left1 ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] { right1, right2, left1 },   null,                             left1 ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   new Either[] { left1, left2 },            null,                             leftResult ),
                Arguments.of( CONCAT_STRINGS,   ADD_INTEGERS,   allEithersArray,                          null,                             leftResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("combineTestCases")
    @DisplayName("combine: test cases")
    public <L, R> void combine_testCases(BiFunction<? super L, ? super L, ? extends L> mapperLeft,
                                         BiFunction<? super R, ? super R, ? extends R> mapperRight,
                                         Either<L, R>[] eithers,
                                         Class<? extends Exception> expectedException,
                                         Either<L, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> Either.combine(mapperLeft, mapperRight, eithers)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    Either.combine(mapperLeft, mapperRight, eithers)
            );
        }
    }


    static Stream<Arguments> combineGetFirstLeftTestCases() {
        Either<String, Integer> right1 = Either.right(11);
        Either<String, Integer> right2 = Either.right(20);
        Either<String, Integer> left1 = Either.left("error");
        Either<String, Integer> left2 = Either.left("warning");

        Supplier<Either<String, Integer>> supRight1 = () -> right1;
        Supplier<Either<String, Integer>> supRight2 = () -> right2;
        Supplier<Either<String, Integer>> supLeft1 = () -> left1;
        Supplier<Either<String, Integer>> supLeft2 = () -> left2;

        Either<String, Integer> rightResult = Either.right(31);
        return Stream.of(
                //@formatter:off
                //            mapperRight,    supplier1,   supplier2,   supplier3,   expectedException,                expectedResult
                Arguments.of( null,           null,        null,        null,        null,                             Right.empty() ),
                Arguments.of( null,           supRight1,   supRight2,   supLeft1,    IllegalArgumentException.class,   null ),
                Arguments.of( ADD_INTEGERS,   null,        null,        null,        null,                             Right.empty() ),
                Arguments.of( ADD_INTEGERS,   supRight1,   null,        null,        null,                             right1 ),
                Arguments.of( ADD_INTEGERS,   supRight1,   supRight2,   null,        null,                             rightResult ),
                Arguments.of( ADD_INTEGERS,   supLeft1,    supLeft2,    null,        null,                             left1 ),
                Arguments.of( ADD_INTEGERS,   supLeft1,    supRight1,   supRight2,   null,                             left1 ),
                Arguments.of( ADD_INTEGERS,   supRight1,   supRight2,   supLeft1,    null,                             left1 )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("combineGetFirstLeftTestCases")
    @DisplayName("combineGetFirstLeft: test cases")
    public <L, R> void combineGetFirstLeft_testCases(BiFunction<? super R, ? super R, ? extends R> mapperRight,
                                                     Supplier<Either<L, R>> supplier1,
                                                     Supplier<Either<L, R>> supplier2,
                                                     Supplier<Either<L, R>> supplier3,
                                                     Class<? extends Exception> expectedException,
                                                     Either<L, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> Either.combineGetFirstLeft(mapperRight, supplier1)
            );
        }
        else {
            Either<L, R> result;
            if (Objects.isNull(supplier1) && Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
                result = Either.combineGetFirstLeft(mapperRight);
            }
            else if (Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
                result = Either.combineGetFirstLeft(mapperRight, supplier1);
            }
            else if (Objects.isNull(supplier3)) {
                result = Either.combineGetFirstLeft(mapperRight, supplier1, supplier2);
            }
            else {
                result = Either.combineGetFirstLeft(mapperRight, supplier1, supplier2, supplier3);
            }
            assertEquals(
                    expectedResult,
                    result
            );
        }
    }


    static Stream<Arguments> containTestCases() {
        Either<String, Long> rightEmpty = Either.right(null);
        Either<String, Long> rightNotEmpty = Either.right(65L);
        Either<String, Long> left = Either.left("problem");
        return Stream.of(
                //@formatter:off
                //            either,          value,   expectedResult
                Arguments.of( rightEmpty,      null,    true ),
                Arguments.of( rightEmpty,      65L,     false ),
                Arguments.of( rightNotEmpty,   null,    false ),
                Arguments.of( rightNotEmpty,   23L,     false ),
                Arguments.of( rightNotEmpty,   65L,     true ),
                Arguments.of( left,            null,    false ),
                Arguments.of( left,            65L,     false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containTestCases")
    @DisplayName("contain: test cases")
    public <L, R> void contain_testCases(Either<L, R> either,
                                          R value,
                                          boolean expectedResult) {
        assertEquals(
                expectedResult,
                either.contain(value)
        );
    }


    static Stream<Arguments> filterTestCases() {
        Either<String, Integer> rightVerifyFilter = Either.right(11);
        Either<String, Integer> rightDoesNotVerifyFilter = Either.right(2);
        Either<String, Integer> left = Either.left("problem");
        return Stream.of(
                //@formatter:off
                //            either,                     predicate,        expectedResult
                Arguments.of( rightVerifyFilter,          null,             of(rightVerifyFilter) ),
                Arguments.of( rightVerifyFilter,          IS_INTEGER_ODD,   of(rightVerifyFilter) ),
                Arguments.of( rightDoesNotVerifyFilter,   IS_INTEGER_ODD,   empty() ),
                Arguments.of( left,                       null,             of(left) ),
                Arguments.of( left,                       IS_INTEGER_ODD,   of(left) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <L, R> void filter_testCases(Either<L, R> either,
                                        Predicate<? super R> predicate,
                                        Optional<Either<L, R>> expectedResult) {
        assertEquals(
                expectedResult,
                either.filter(predicate)
        );
    }


    static Stream<Arguments> filterOrElseTestCases() {
        Either<String, Integer> rightVerifyFilter = Either.right(11);
        Either<String, Integer> rightDoesNotVerifyFilter = Either.right(2);
        Either<String, Integer> left = Either.left("warning");
        Either<String, Integer> leftResult = Either.left("error");

        Supplier<String> errorString = () -> "error";
        return Stream.of(
                //@formatter:off
                //            either,                     predicate,        zero,          expectedException,                expectedResult
                Arguments.of( rightVerifyFilter,          null,             null,          null,                             rightVerifyFilter ),
                Arguments.of( rightDoesNotVerifyFilter,   null,             null,          null,                             rightDoesNotVerifyFilter ),
                Arguments.of( left,                       null,             null,          null,                             left ),
                Arguments.of( rightVerifyFilter,          IS_INTEGER_ODD,   null,          null,                             rightVerifyFilter ),
                Arguments.of( rightVerifyFilter,          IS_INTEGER_ODD,   errorString,   null,                             rightVerifyFilter ),
                Arguments.of( rightDoesNotVerifyFilter,   IS_INTEGER_ODD,   null,          IllegalArgumentException.class,   null ),
                Arguments.of( rightDoesNotVerifyFilter,   IS_INTEGER_ODD,   errorString,   null,                             leftResult ),
                Arguments.of( left,                       IS_INTEGER_ODD,   errorString,   null,                             left )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterOrElseTestCases")
    @DisplayName("filterOrElse: test cases")
    public <L, R> void filterOrElse_testCases(Either<L, R> either,
                                              Predicate<? super R> predicate,
                                              Supplier<? extends L> zero,
                                              Class<? extends Exception> expectedException,
                                              Either<L, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.filterOrElse(predicate, zero)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.filterOrElse(predicate, zero)
            );
        }
    }


    static Stream<Arguments> mapWithRightMapperTestCases() {
        Either<String, Integer> right = Either.right(12);
        Either<String, Integer> left = Either.left("There was a problem");
        return Stream.of(
                //@formatter:off
                //            either,   mapper,                   expectedException,                expectedResult
                Arguments.of( right,    null,                     IllegalArgumentException.class,   null ),
                Arguments.of( right,    FROM_INTEGER_TO_STRING,   null,                             Either.right("12") ),
                Arguments.of( left,     null,                     null,                             left ),
                Arguments.of( left,     FROM_INTEGER_TO_STRING,   null,                             left )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithRightMapperTestCases")
    @DisplayName("map: with right mapper test cases")
    public <L, R, U> void mapWithRightMapper_testCases(Either<L, R> either,
                                                       Function<? super R, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Either<L, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.map(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.map(mapper)
            );
        }
    }


    static Stream<Arguments> mapWithBothMappersTestCases() {
        Either<String, Integer> right = Either.right(33);
        Either<String, Integer> left = Either.left("There was a problem");
        Function<String, String> addTwoLetters = s -> s + "2Z";
        return Stream.of(
                //@formatter:off
                //            either,   mapperLeft,      mapperRight,              expectedException,                expectedResult
                Arguments.of( right,    addTwoLetters,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( right,    null,            FROM_INTEGER_TO_STRING,   null,                             Either.right("33") ),
                Arguments.of( right,    addTwoLetters,   FROM_INTEGER_TO_STRING,   null,                             Either.right("33") ),
                Arguments.of( left,     null,            FROM_INTEGER_TO_STRING,   IllegalArgumentException.class,   null ),
                Arguments.of( left,     addTwoLetters,   null,                     null,                             Either.left("There was a problem2Z") ),
                Arguments.of( left,     addTwoLetters,   FROM_INTEGER_TO_STRING,   null,                             Either.left("There was a problem2Z"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithBothMappersTestCases")
    @DisplayName("map: with both mappers test cases")
    public <L, R, L2, R2> void mapWithBothMappers_testCases(Either<L, R> either,
                                                            Function<? super L, ? extends L2> mapperLeft,
                                                            Function<? super R, ? extends R2> mapperRight,
                                                            Class<? extends Exception> expectedException,
                                                            Either<L2, R2>  expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.map(mapperLeft, mapperRight)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.map(mapperLeft, mapperRight)
            );
        }
    }


    static Stream<Arguments> mapLeftTestCases() {
        Either<String, Integer> right = Either.right(12);
        Either<String, Integer> left = Either.left("There was a problem");
        Function<String, String> addALetter = s -> s + "2";
        return Stream.of(
                //@formatter:off
                //            either,   mapper,       expectedException,                expectedResult
                Arguments.of( left,     null,         IllegalArgumentException.class,   null ),
                Arguments.of( left,     addALetter,   null,                             Either.left("There was a problem2") ),
                Arguments.of( right,    null,         null,                             right ),
                Arguments.of( right,    addALetter,   null,                             right )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapLeftTestCases")
    @DisplayName("mapLeft: test cases")
    public <L, R, U> void mapLeft_testCases(Either<L, R> either,
                                            Function<? super L, ? extends U> mapper,
                                            Class<? extends Exception> expectedException,
                                            Either<U, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.mapLeft(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.mapLeft(mapper)
            );
        }
    }


    static Stream<Arguments> flatmapTestCases() {
        Either<String, Integer> right = Either.right(44);
        Either<String, Integer> left = Either.left("There was a problem");
        Function<Integer, Either<String, String>> fromIntegerToRightWithString = i -> Either.right(i.toString());
        return Stream.of(
                //@formatter:off
                //            either,   mapper,                         expectedException,                expectedResult
                Arguments.of( right,    null,                           IllegalArgumentException.class,   null ),
                Arguments.of( right,    fromIntegerToRightWithString,   null,                             Either.right("44") ),
                Arguments.of( left,     null,                           null,                             left ),
                Arguments.of( left,     fromIntegerToRightWithString,   null,                             left )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flatmapTestCases")
    @DisplayName("flatmap: test cases")
    public <L, R, U> void flatmap_testCases(Either<L, R> either,
                                            Function<? super R, ? extends Either<L, ? extends U>> mapper,
                                            Class<? extends Exception> expectedException,
                                            Either<L, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.flatMap(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.flatMap(mapper)
            );
        }
    }


    static Stream<Arguments> apTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty1 = Either.right(1);
        Either<String, Integer> rightNotEmpty2 = Either.right(3);
        Either<String, Integer> leftEmpty = Either.left(null);
        Either<String, Integer> leftNotEmpty1 = Either.left("There was");
        Either<String, Integer> leftNotEmpty2 = Either.left("a problem");

        Either<String, Integer> sumAllRight = Either.right(4);
        Either<String, Integer> concatAllLeft = Either.left("There wasa problem");
        return Stream.of(
                //@formatter:off
                //            either,           eitherParam,      mapperLeft,       mapperRight,    expectedException,                expectedResult
                Arguments.of( rightEmpty,       null,             null,             null,           null,                             rightEmpty ),
                Arguments.of( rightNotEmpty1,   null,             null,             null,           null,                             rightNotEmpty1 ),
                Arguments.of( leftEmpty,        null,             null,             null,           null,                             leftEmpty ),
                Arguments.of( leftNotEmpty1,    null,             null,             null,           null,                             leftNotEmpty1 ),
                Arguments.of( rightEmpty,       rightNotEmpty1,   CONCAT_STRINGS,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( leftEmpty,        leftNotEmpty1,    null,             ADD_INTEGERS,   IllegalArgumentException.class,   null ),
                Arguments.of( rightNotEmpty1,   leftNotEmpty1,    null,             null,           null,                             leftNotEmpty1 ),
                Arguments.of( leftNotEmpty2,    rightNotEmpty2,   null,             null,           null,                             leftNotEmpty2 ),
                Arguments.of( rightNotEmpty1,   rightNotEmpty2,   null,             ADD_INTEGERS,   null,                             sumAllRight ),
                Arguments.of( leftNotEmpty1,    leftNotEmpty2,    CONCAT_STRINGS,   null,           null,                             concatAllLeft )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("apTestCases")
    @DisplayName("ap: test cases")
    public <L, R> void ap_testCases(Either<L, R> either,
                                    Either<? extends L, ? extends R> eitherParam,
                                    BiFunction<? super L, ? super L, ? extends L> mapperLeft,
                                    BiFunction<? super R, ? super R, ? extends R> mapperRight,
                                    Class<? extends Exception> expectedException,
                                    Either<L, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.ap(eitherParam, mapperLeft, mapperRight)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.ap(eitherParam, mapperLeft, mapperRight)
            );
        }
    }


    static Stream<Arguments> foldOneMapperTestCases() {
        Integer defaultValue = 11;
        Either<String, Integer> rightWithNoValue = Either.right(null);
        Either<String, Integer> right = Either.right(99);
        Either<String, Integer> left = Either.left("There was a problem");
        Function<Either<String, Integer>, Integer> mapper = e ->
                e.isRight()
                        ? ofNullable(e.get()).orElse(defaultValue)
                        : e.getLeft().length();
        return Stream.of(
                //@formatter:off
                //            either,             mapper,   expectedException,                expectedResult
                Arguments.of( right,              null,     IllegalArgumentException.class,   null ),
                Arguments.of( left,               null,     IllegalArgumentException.class,   null ),
                Arguments.of( rightWithNoValue,   mapper,   null,                             defaultValue ),
                Arguments.of( right,              mapper,   null,                             right.get() ),
                Arguments.of( left,               mapper,   null,                             left.getLeft().length() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldOneMapperTestCases")
    @DisplayName("fold: with one mapper test cases")
    public <L, R, U> void foldOneMapper_testCases(Either<L, R> either,
                                                  Function<? super Either<L, R>, ? extends U> mapper,
                                                  Class<? extends Exception> expectedException,
                                                  U expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.fold(mapper)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.fold(mapper)
            );
        }
    }


    static Stream<Arguments> foldTwoMappersTestCases() {
        Either<String, Integer> right = Either.right(99);
        Either<String, Integer> left = Either.left("There was a problem");
        Function<String, String> stringToString = Function.identity();
        return Stream.of(
                //@formatter:off
                //            either,   mapperLeft,       mapperRight,              expectedException,                expectedResult
                Arguments.of( right,    null,             null,                     IllegalArgumentException.class,   null ),
                Arguments.of( right,    stringToString,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( left,     null,             null,                     IllegalArgumentException.class,   null ),
                Arguments.of( left,     null,             FROM_INTEGER_TO_STRING,   IllegalArgumentException.class,   null ),
                Arguments.of( right,    null,             FROM_INTEGER_TO_STRING,   null,                             "99" ),
                Arguments.of( right,    stringToString,   FROM_INTEGER_TO_STRING,   null,                             "99" ),
                Arguments.of( left,     stringToString,   null,                     null,                             left.getLeft() ),
                Arguments.of( left,     stringToString,   FROM_INTEGER_TO_STRING,   null,                             left.getLeft() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldTwoMappersTestCases")
    @DisplayName("fold: with two mappers test cases")
    public <L, R, U> void foldTwoMappers_testCases(Either<L, R> either,
                                                   Function<? super L, ? extends U> mapperLeft,
                                                   Function<? super R, ? extends U> mapperRight,
                                                   Class<? extends Exception> expectedException,
                                                   U expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.fold(mapperLeft, mapperRight)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.fold(mapperLeft, mapperRight)
            );
        }
    }


    static Stream<Arguments> peekWithValidActionTestCases() {
        Either<String, Integer> right = Either.right(99);
        Either<String, Integer> left = Either.left("There was a problem");
        Consumer<Integer> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            either,   action,   expectedResult
                Arguments.of( right,    null,     right ),
                Arguments.of( right,    action,   right ),
                Arguments.of( left,     null,     left ),
                Arguments.of( left,     action,   left )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekWithValidActionTestCases")
    @DisplayName("peek: with valid action test cases")
    public <L, R> void peekWithValidAction_testCases(Either<L, R> either,
                                                     Consumer<? super R> action,
                                                     Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                either.peek(action)
        );
    }


    static Stream<Arguments> peekWithBothConsumersTestCases() {
        Either<String, Integer> right = Either.right(99);
        Either<String, Integer> left = Either.left("An error happened");
        Consumer<Integer> actionRight = System.out::println;
        Consumer<String> actionLeft = System.out::println;
        return Stream.of(
                //@formatter:off
                //            either,   actionLeft,   actionRight,    expectedResult
                Arguments.of( right,    actionLeft,   null,          right ),
                Arguments.of( right,    null,         actionRight,   right ),
                Arguments.of( right,    actionLeft,   actionRight,   right ),
                Arguments.of( left,     null,         actionRight,   left ),
                Arguments.of( left,     actionLeft,   null,          left ),
                Arguments.of( left,     actionLeft,   actionRight,   left )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekWithBothConsumersTestCases")
    @DisplayName("peek: with both consumers test cases")
    public <L, R> void peekWithBothConsumers_testCases(Either<L, R> either,
                                                       Consumer<? super L> actionLeft,
                                                       Consumer<? super R> actionRight,
                                                       Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                either.peek(actionLeft, actionRight)
        );
    }


    static Stream<Arguments> peekLeftTestCases() {
        Either<String, Integer> right = Either.right(33);
        Either<String, Integer> left = Either.left("Problem");
        Consumer<String> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            either,   action,   expectedResult
                Arguments.of( left,     null,     left ),
                Arguments.of( left,     action,   left ),
                Arguments.of( right,    null,     right ),
                Arguments.of( right,    action,   right )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekLeftTestCases")
    @DisplayName("peekLeft: test cases")
    public <L, R> void peekLeft_testCases(Either<L, R> either,
                                          Consumer<? super L> action,
                                          Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                either.peekLeft(action)
        );
    }


    static Stream<Arguments> getOrElseWithValueTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(11);
        Either<String, Integer> leftNotEmpty = Either.left("problem");
        Integer other = 33;
        return Stream.of(
                //@formatter:off
                //            either,          other,   expectedResult
                Arguments.of( rightEmpty,      null,    rightEmpty.get() ),
                Arguments.of( rightEmpty,      other,   rightEmpty.get() ),
                Arguments.of( rightNotEmpty,   null,    rightNotEmpty.get() ),
                Arguments.of( rightNotEmpty,   other,   rightNotEmpty.get() ),
                Arguments.of( leftNotEmpty,    null,    null ),
                Arguments.of( leftNotEmpty,    other,   other )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithValueTestCases")
    @DisplayName("getOrElse: with value as parameter test cases")
    public <L, R> void getOrElseWithValue_testCases(Either<L, R> either,
                                                    R other,
                                                    R expectedResult) {
        assertEquals(
                expectedResult,
                either.getOrElse(other)
        );
    }


    static Stream<Arguments> getOrElseWithSupplierTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(44);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");
        Supplier<Integer> supplier = () -> 33;
        return Stream.of(
                //@formatter:off
                //            either,          supplier,   expectedException,                expectedResult
                Arguments.of( rightEmpty,      null,       null,                             rightEmpty.get() ),
                Arguments.of( rightEmpty,      supplier,   null,                             rightEmpty.get() ),
                Arguments.of( rightNotEmpty,   null,       null,                             rightNotEmpty.get() ),
                Arguments.of( rightNotEmpty,   supplier,   null,                             rightNotEmpty.get() ),
                Arguments.of( leftNotEmpty,    null,       IllegalArgumentException.class,   null ),
                Arguments.of( leftNotEmpty,    supplier,   null,                             supplier.get() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseWithSupplierTestCases")
    @DisplayName("getOrElse: with Supplier as parameter test cases")
    public <L, R> void getOrElseWithSupplier_testCases(Either<L, R> either,
                                                       Supplier<? extends R> supplier,
                                                       Class<? extends Exception> expectedException,
                                                       R expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.getOrElse(supplier)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.getOrElse(supplier)
            );
        }
    }


    static Stream<Arguments> getOrElseThrowWithSupplierTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(44);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");
        Supplier<Exception> exceptionSupplier = () -> new IllegalArgumentException("Something was wrong");
        return Stream.of(
                //@formatter:off
                //            either,          exceptionSupplier,   expectedException,                expectedResult
                Arguments.of( rightEmpty,      null,                null,                             null ),
                Arguments.of( rightEmpty,      exceptionSupplier,   null,                             null ),
                Arguments.of( rightNotEmpty,   null,                null,                             rightNotEmpty.get() ),
                Arguments.of( rightNotEmpty,   exceptionSupplier,   null,                             rightNotEmpty.get() ),
                Arguments.of( leftNotEmpty,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( leftNotEmpty,    exceptionSupplier,   IllegalArgumentException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseThrowWithSupplierTestCases")
    @DisplayName("getOrElseThrow: with Supplier as parameter test cases")
    public <L, R, X extends Throwable> void getOrElseThrowWithSupplier_testCases(Either<L, R> either,
                                                                                 Supplier<X> exceptionSupplier,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 R expectedResult) throws Throwable {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.getOrElseThrow(exceptionSupplier)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.getOrElseThrow(exceptionSupplier)
            );
        }
    }


    static Stream<Arguments> getOrElseThrowWithFunctionTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(44);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");
        Function<String, Exception> exceptionFunction =
                (s) ->
                        new IllegalArgumentException(
                                format("Something was wrong. Reason: %s",
                                        s
                                )
                        );
        return Stream.of(
                //@formatter:off
                //            either,          exceptionFunction,   expectedException,                expectedResult
                Arguments.of( rightEmpty,      null,                null,                             null ),
                Arguments.of( rightEmpty,      exceptionFunction,   null,                             null ),
                Arguments.of( rightNotEmpty,   null,                null,                             rightNotEmpty.get() ),
                Arguments.of( rightNotEmpty,   exceptionFunction,   null,                             rightNotEmpty.get() ),
                Arguments.of( leftNotEmpty,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( leftNotEmpty,    exceptionFunction,   IllegalArgumentException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseThrowWithFunctionTestCases")
    @DisplayName("getOrElseThrow: with Function as parameter test cases")
    public <L, R, X extends Throwable> void getOrElseThrowWithFunction_testCases(Either<L, R> either,
                                                                                 Function<? super L, X> exceptionFunction,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 R expectedResult) throws Throwable {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.getOrElseThrow(exceptionFunction)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.getOrElseThrow(exceptionFunction)
            );
        }
    }


    static Stream<Arguments> orElseWithEitherTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty1 = Either.right(44);
        Either<String, Integer> rightNotEmpty2 = Either.right(55);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");
        return Stream.of(
                //@formatter:off
                //            either,           other,            expectedResult
                Arguments.of( rightEmpty,       null,             rightEmpty ),
                Arguments.of( rightEmpty,       rightNotEmpty1,   rightEmpty ),
                Arguments.of( rightNotEmpty1,   null,             rightNotEmpty1 ),
                Arguments.of( rightNotEmpty1,   rightNotEmpty2,   rightNotEmpty1 ),
                Arguments.of( leftNotEmpty,     null,             null ),
                Arguments.of( leftNotEmpty,     rightNotEmpty1,   rightNotEmpty1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithEitherTestCases")
    @DisplayName("orElse: with Either as parameter test cases")
    public <L, R> void orElseWithEither_testCases(Either<L, R> either,
                                                  Either<? extends L, ? extends R> other,
                                                  Either<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                either.orElse(other)
        );
    }


    static Stream<Arguments> orElseWithSupplierTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(44);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");
        Supplier<Either<String, Integer>> supplierRight = () -> Either.right(33);
        return Stream.of(
                //@formatter:off
                //            either,          supplier,        expectedException,                expectedResult
                Arguments.of( rightEmpty,      null,            null,                             rightEmpty ),
                Arguments.of( rightEmpty,      supplierRight,   null,                             rightEmpty ),
                Arguments.of( rightNotEmpty,   null,            null,                             rightNotEmpty ),
                Arguments.of( rightNotEmpty,   supplierRight,   null,                             rightNotEmpty ),
                Arguments.of( leftNotEmpty,    null,            IllegalArgumentException.class,   null ),
                Arguments.of( leftNotEmpty,    supplierRight,   null,                             Either.right(33) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithSupplierTestCases")
    @DisplayName("orElse: with Supplier as parameter test cases")
    public <L, R> void orElseWithSupplier_testCases(Either<L, R> either,
                                                    Supplier<Either<? extends L, ? extends R>> supplier,
                                                    Class<? extends Exception> expectedException,
                                                    Either<L, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.orElse(supplier)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.orElse(supplier)
            );
        }
    }


    static Stream<Arguments> swapTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(1);
        Either<String, Integer> leftEmpty = Either.left(null);
        Either<String, Integer> leftNotEmpty = Either.left("problem");
        return Stream.of(
                //@formatter:off
                //            either,          expectedResult
                Arguments.of( rightEmpty,      Either.left(null) ),
                Arguments.of( rightNotEmpty,   Either.left(1) ),
                Arguments.of( leftEmpty,       Either.right(null) ),
                Arguments.of( leftNotEmpty,    Either.right("problem") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("swapTestCases")
    @DisplayName("swap: test cases")
    public <L, R> void swap_testCases(Either<L, R> either,
                                      Either<R, L> expectedResult) {
        assertEquals(
                expectedResult,
                either.swap()
        );
    }


    static Stream<Arguments> isEmptyTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(1);
        Either<String, Integer> left = Either.left("problem");
        return Stream.of(
                //@formatter:off
                //            either,          expectedResult
                Arguments.of( rightEmpty,      true ),
                Arguments.of( rightNotEmpty,   false ),
                Arguments.of( left,            true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public <L, R> void isEmpty_testCases(Either<L, R> either,
                                         boolean expectedResult) {
        assertEquals(
                expectedResult,
                either.isEmpty()
        );
    }


    static Stream<Arguments> toOptionalTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(1);
        Either<String, Integer> left = Either.left("problem");
        return Stream.of(
                //@formatter:off
                //            either,          expectedResult
                Arguments.of( rightEmpty,      empty() ),
                Arguments.of( rightNotEmpty,   of(rightNotEmpty.get()) ),
                Arguments.of( left,            empty() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toOptionalTestCases")
    @DisplayName("toOptional: test cases")
    public <L, R> void toOptional_testCases(Either<L, R> either,
                                            Optional<R> expectedResult) {
        assertEquals(
                expectedResult,
                either.toOptional()
        );
    }


    static Stream<Arguments> toTryTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(11);
        Either<String, Integer> leftEmpty = Either.left(null);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");

        NullPointerException nullPointerException = new NullPointerException("Something is missing");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Something is not valid");

        Try<Integer> tryFromEmptyRight = Try.success(null);
        Try<Integer> tryFromRight = Try.success(11);
        Try<Integer> tryFromEmptyLeft = Try.failure(nullPointerException);
        Try<Integer> tryFromLeft = Try.failure(illegalArgumentException);

        Function<String, ? extends Throwable> mapperLeft =
                (s) ->
                        null == s
                                ? nullPointerException
                                : illegalArgumentException;
        return Stream.of(
                //@formatter:off
                //            either,          mapperLeft,   expectedException,                expectedResult
                Arguments.of( rightEmpty,      null,         null,                             tryFromEmptyRight ),
                Arguments.of( rightEmpty,      mapperLeft,   null,                             tryFromEmptyRight ),
                Arguments.of( rightNotEmpty,   null,         null,                             tryFromRight ),
                Arguments.of( rightNotEmpty,   mapperLeft,   null,                             tryFromRight ),
                Arguments.of( leftEmpty,       null,         IllegalArgumentException.class,   null ),
                Arguments.of( leftEmpty,       mapperLeft,   null,                             tryFromEmptyLeft ),
                Arguments.of( leftNotEmpty,    null,         IllegalArgumentException.class,   null ),
                Arguments.of( leftNotEmpty,    mapperLeft,   null,                             tryFromLeft )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toTryTestCases")
    @DisplayName("toTry: test cases")
    public <L, R> void toTry_testCases(Either<L, R> either,
                                       Function<? super L, ? extends Throwable> mapperLeft,
                                       Class<? extends Exception> expectedException,
                                       Try<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> either.toTry(mapperLeft)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    either.toTry(mapperLeft)
            );
        }
    }


    static Stream<Arguments> toValidationTestCases() {
        Either<String, Integer> rightEmpty = Either.right(null);
        Either<String, Integer> rightNotEmpty = Either.right(11);
        Either<String, Integer> leftEmpty = Either.left(null);
        Either<String, Integer> leftNotEmpty = Either.left("There was a problem");

        Validation<String, Integer> validFromEmptyEither = Validation.valid(null);
        Validation<String, Integer> validFromEither = Validation.valid(11);
        Validation<String, Integer> invalidFromEmptyEither = Validation.invalid(List.of());
        Validation<String, Integer> invalidFromEither = Validation.invalid(List.of("There was a problem"));
        return Stream.of(
                //@formatter:off
                //            either,          expectedResult
                Arguments.of( rightEmpty,      validFromEmptyEither ),
                Arguments.of( rightNotEmpty,   validFromEither ),
                Arguments.of( leftEmpty,       invalidFromEmptyEither ),
                Arguments.of( leftNotEmpty,    invalidFromEither )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toValidationTestCases")
    @DisplayName("toValidation: test cases")
    public <L, R> void toValidation_testCases(Either<L, R> either,
                                              Validation<L, R> expectedResult) {
        assertEquals(
                expectedResult,
                either.toValidation()
        );
    }


    private static final BiFunction<Integer, Integer, Integer> ADD_INTEGERS = Integer::sum;

    private static final BiFunction<String, String, String> CONCAT_STRINGS = String::concat;

    private static final Function<Integer, String> FROM_INTEGER_TO_STRING = Object::toString;

    private static final Predicate<Integer> IS_INTEGER_ODD = i -> i % 2 == 1;

}
