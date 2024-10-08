package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.predicate.HeptaPredicate;
import com.spring6microservices.common.core.predicate.HexaPredicate;
import com.spring6microservices.common.core.predicate.NonaPredicate;
import com.spring6microservices.common.core.predicate.OctaPredicate;
import com.spring6microservices.common.core.predicate.PentaPredicate;
import com.spring6microservices.common.core.predicate.TetraPredicate;
import com.spring6microservices.common.core.predicate.TriPredicate;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.util.Arrays.asList;

@UtilityClass
public class PredicateUtil {

    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   Predicate<Integer> isGreaterThanTen = i -> 10 < i;
     *   Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
     *
     *   allOf().test(5);                                          // true
     *   allOf(isGreaterThanTen, isGreaterThanTwenty).test(30);    // true
     *   allOf(isGreaterThanTen, isGreaterThanTwenty).test(20);    // false
     * </pre>
     *
     * @param predicates
     *    {@link Predicate} to verify
     *
     * @return {@link Predicate} verifying all provided ones
     */
    @SafeVarargs
    public static <T> Predicate<T> allOf(final Predicate<? super T>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return alwaysTrue();
        }
        return t ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = null == currentPred || currentPred.test(t);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link Predicate} with {@code false} as result.
     *
     * @return {@link Predicate} always returning {@code false}
     */
    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }


    /**
     * Returns a {@link Predicate} with {@code true} as result.
     *
     * @return {@link Predicate} always returning {@code true}
     */
    public static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   Predicate<Integer> isGreaterThanTen = i -> 10 < i;
     *   Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
     *
     *   anyOf().test(5);                                          // false
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(11);    // true
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(21);    // true
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(1);     // false
     * </pre>
     *
     * @param predicates
     *    {@link Predicate} to verify
     *
     * @return {@link Predicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *         {@link Predicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T> Predicate<T> anyOf(final Predicate<? super T>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return alwaysFalse();
        }
        return t -> {
            for (Predicate<? super T> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
     *   BiPredicate<Integer, String> isGreaterThanTwentyAndStringLongerThan5 = (i, s) -> (20 < i) && (5 < s.length());
     *
     *   biAllOf().test(5, "");                                                                                              // true
     *   biAllOf(isIntegerGreaterThanTenAndStringLongerThan2, isGreaterThanTwentyAndStringLongerThan5).test(30, "abcdef");   // true
     *   biAllOf(isIntegerGreaterThanTenAndStringLongerThan2, isGreaterThanTwentyAndStringLongerThan5).test(20, "abc");      // false
     * </pre>
     *
     * @param predicates
     *    {@link BiPredicate} to verify
     *
     * @return {@link BiPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2> BiPredicate<T1, T2> biAllOf(final BiPredicate<? super T1, ? super T2>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return biAlwaysTrue();
        }
        return (t1, t2) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link BiPredicate} with {@code false} as result.
     *
     * @return {@link BiPredicate} always returning {@code false}
     */
    public static <T1, T2> BiPredicate<T1, T2> biAlwaysFalse() {
        return (t1, t2) -> false;
    }


    /**
     * Returns a {@link BiPredicate} with {@code true} as result.
     *
     * @return {@link BiPredicate} always returning {@code true}
     */
    public static <T1, T2> BiPredicate<T1, T2> biAlwaysTrue() {
        return (t1, t2) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
     *   BiPredicate<Integer, String> isLowerThanTwentyAndStringShorterThan5 = (i, s) -> (20 > i) && (5 > s.length());
     *
     *   biAnyOf().test(5, "");                                                                                            // false
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(11, "abc");     // true
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(8, "abc");      // true
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(5, "abcdef");   // false
     * </pre>
     *
     * @param predicates
     *    {@link BiPredicate} to verify
     *
     * @return {@link BiPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link BiPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2> BiPredicate<T1, T2> biAnyOf(final BiPredicate<? super T1, ? super T2>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return biAlwaysFalse();
        }
        return (t1, t2) -> {
            for (BiPredicate<? super T1, ? super T2> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link BiPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link BiPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2> BiPredicate<T1, T2> biIsNull() {
        return (t1, t2) ->
                null == t1 &&
                        null == t2;
    }


    /**
     * Returns a {@link BiPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link BiPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2> BiPredicate<T1, T2> biNonNull() {
        return (t1, t2) ->
                null != t1 &&
                        null != t2;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;
     *
     *   TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
     *      10 < t1 && 10 < t2 && 10 < t3;
     *
     *   triAllOf().test(5, 6, 7);                                 // true
     *   triAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19);   // true
     *   triAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33);    // false
     * </pre>
     *
     * @param predicates
     *    {@link TriPredicate} to verify
     *
     * @return {@link TriPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triAllOf(final TriPredicate<? super T1, ? super T2, ? super T3>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return triAlwaysTrue();
        }
        return (t1, t2, t3) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link TriPredicate} with {@code false} as result.
     *
     * @return {@link TriPredicate} always returning {@code false}
     */
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triAlwaysFalse() {
        return (t1, t2, t3) -> false;
    }


    /**
     * Returns a {@link TriPredicate} with {@code true} as result.
     *
     * @return {@link TriPredicate} always returning {@code true}
     */
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triAlwaysTrue() {
        return (t1, t2, t3) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;
     *
     *   TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
     *      10 < t1 && 10 < t2 && 10 < t3;
     *
     *   triAnyOf().test(5, 6, 7);                                 // false
     *   triAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19);    // true
     *   triAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19);   // true
     *   triAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33);    // false
     * </pre>
     *
     * @param predicates
     *    {@link TriPredicate} to verify
     *
     * @return {@link TriPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link TriPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triAnyOf(final TriPredicate<? super T1, ? super T2, ? super T3>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return triAlwaysFalse();
        }
        return (t1, t2, t3) -> {
            for (TriPredicate<? super T1, ? super T2, ? super T3> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link TriPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link TriPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triIsNull() {
        return (t1, t2, t3) ->
                null == t1 &&
                        null == t2 &&
                        null == t3;
    }


    /**
     * Returns a {@link TriPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link TriPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3> TriPredicate<T1, T2, T3> triNonNull() {
        return (t1, t2, t3) ->
                null != t1 &&
                        null != t2 &&
                        null != t3;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   TetraPredicate<Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2;
     *
     *   TetraPredicate<Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4;
     *
     *   tetraAllOf().test(5, 6, 7, 11);                                 // true
     *   tetraAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33);   // true
     *   tetraAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12);    // false
     * </pre>
     *
     * @param predicates
     *    {@link TetraPredicate} to verify
     *
     * @return {@link TetraPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraAllOf(final TetraPredicate<? super T1, ? super T2, ? super T3, ? super T4>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return tetraAlwaysTrue();
        }
        return (t1, t2, t3, t4) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link TetraPredicate} with {@code false} as result.
     *
     * @return {@link TetraPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraAlwaysFalse() {
        return (t1, t2, t3, t4) -> false;
    }


    /**
     * Returns a {@link TetraPredicate} with {@code true} as result.
     *
     * @return {@link TetraPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraAlwaysTrue() {
        return (t1, t2, t3, t4) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   TetraPredicate<Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2;
     *
     *   TetraPredicate<Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4;
     *
     *   tetraAnyOf().test(5, 6, 7, 11);                                 // false
     *   tetraAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7);     // true
     *   tetraAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16);   // true
     *   tetraAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12);    // false
     * </pre>
     *
     * @param predicates
     *    {@link TetraPredicate} to verify
     *
     * @return {@link TetraPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link TetraPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraAnyOf(final TetraPredicate<? super T1, ? super T2, ? super T3, ? super T4>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return tetraAlwaysFalse();
        }
        return (t1, t2, t3, t4) -> {
            for (TetraPredicate<? super T1, ? super T2, ? super T3, ? super T4> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link TetraPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link TetraPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraIsNull() {
        return (t1, t2, t3, t4) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4;
    }


    /**
     * Returns a {@link TetraPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link TetraPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> tetraNonNull() {
        return (t1, t2, t3, t4) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   PentaPredicate<Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2;
     *
     *   PentaPredicate<Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5;
     *
     *   pentaAllOf().test(5, 6, 7, 11, 12);                                 // true
     *   pentaAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33, 41);   // true
     *   pentaAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12, 22);    // false
     * </pre>
     *
     * @param predicates
     *    {@link PentaPredicate} to verify
     *
     * @return {@link PentaPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaAllOf(final PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return pentaAlwaysTrue();
        }
        return (t1, t2, t3, t4, t5) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4, t5);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link PentaPredicate} with {@code false} as result.
     *
     * @return {@link PentaPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaAlwaysFalse() {
        return (t1, t2, t3, t4, t5) -> false;
    }


    /**
     * Returns a {@link PentaPredicate} with {@code true} as result.
     *
     * @return {@link PentaPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaAlwaysTrue() {
        return (t1, t2, t3, t4, t5) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   PentaPredicate<Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2;
     *
     *   PentaPredicate<Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5;
     *
     *   pentaAnyOf().test(5, 6, 7, 11, 14);                                 // false
     *   pentaAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7, 3);      // true
     *   pentaAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16, 22);   // true
     *   pentaAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12, 8);     // false
     * </pre>
     *
     * @param predicates
     *    {@link PentaPredicate} to verify
     *
     * @return {@link PentaPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link PentaPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaAnyOf(PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return pentaAlwaysFalse();
        }
        return (t1, t2, t3, t4, t5) -> {
            for (PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4, t5)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link PentaPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link PentaPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaIsNull() {
        return (t1, t2, t3, t4, t5) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4 &&
                        null == t5;
    }


    /**
     * Returns a {@link PentaPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link PentaPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> pentaNonNull() {
        return (t1, t2, t3, t4, t5) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4 &&
                        null != t5;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2;
     *
     *   HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6;
     *
     *   hexaAllOf().test(5, 6, 7, 11, 12, 0);                                  // true
     *   hexaAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33, 41, 17);   // true
     *   hexaAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12, 22, 14);    // false
     * </pre>
     *
     * @param predicates
     *    {@link HexaPredicate} to verify
     *
     * @return {@link HexaPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaAllOf(final HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return hexaAlwaysTrue();
        }
        return (t1, t2, t3, t4, t5, t6) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4, t5, t6);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link HexaPredicate} with {@code false} as result.
     *
     * @return {@link HexaPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaAlwaysFalse() {
        return (t1, t2, t3, t4, t5, t6) -> false;
    }


    /**
     * Returns a {@link HexaPredicate} with {@code true} as result.
     *
     * @return {@link HexaPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaAlwaysTrue() {
        return (t1, t2, t3, t4, t5, t6) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2;
     *
     *   HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6;
     *
     *   hexaAnyOf().test(5, 6, 7, 11, 14, 4);                                  // false
     *   hexaAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7, 3, 9);       // true
     *   hexaAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16, 22, 18);   // true
     *   hexaAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12, 8, 5);      // false
     * </pre>
     *
     * @param predicates
     *    {@link HexaPredicate} to verify
     *
     * @return {@link HexaPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link HexaPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaAnyOf(final HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return hexaAlwaysFalse();
        }
        return (t1, t2, t3, t4, t5, t6) -> {
            for (HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4, t5, t6)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link HexaPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link HexaPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaIsNull() {
        return (t1, t2, t3, t4, t5, t6) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4 &&
                        null == t5 &&
                        null == t6;
    }


    /**
     * Returns a {@link HexaPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link HexaPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> hexaNonNull() {
        return (t1, t2, t3, t4, t5, t6) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4 &&
                        null != t5 &&
                        null != t6;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2;
     *
     *   HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7;
     *
     *   heptaAllOf().test(5, 6, 7, 11, 12, 0, 15);                                  // true
     *   heptaAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33, 41, 17, 33);   // true
     *   heptaAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12, 22, 14, 0);     // false
     * </pre>
     *
     * @param predicates
     *    {@link HeptaPredicate} to verify
     *
     * @return {@link HeptaPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaAllOf(final HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return heptaAlwaysTrue();
        }
        return (t1, t2, t3, t4, t5, t6, t7) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4, t5, t6, t7);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link HeptaPredicate} with {@code false} as result.
     *
     * @return {@link HeptaPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaAlwaysFalse() {
        return (t1, t2, t3, t4, t5, t6, t7) -> false;
    }


    /**
     * Returns a {@link HeptaPredicate} with {@code true} as result.
     *
     * @return {@link HeptaPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaAlwaysTrue() {
        return (t1, t2, t3, t4, t5, t6, t7) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2;
     *
     *   HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7;
     *
     *   heptaAnyOf().test(5, 6, 7, 11, 14, 4, 0);                                   // false
     *   heptaAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7, 3, 9, 11);       // true
     *   heptaAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16, 22, 18, 20);   // true
     *   heptaAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12, 8, 5, 0);       // false
     * </pre>
     *
     * @param predicates
     *    {@link HeptaPredicate} to verify
     *
     * @return {@link HeptaPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link HeptaPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaAnyOf(final HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return heptaAlwaysFalse();
        }
        return (t1, t2, t3, t4, t5, t6, t7) -> {
            for (HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4, t5, t6, t7)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link HeptaPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link HeptaPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaIsNull() {
        return (t1, t2, t3, t4, t5, t6, t7) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4 &&
                        null == t5 &&
                        null == t6 &&
                        null == t7;
    }


    /**
     * Returns a {@link HeptaPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link HeptaPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> heptaNonNull() {
        return (t1, t2, t3, t4, t5, t6, t7) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4 &&
                        null != t5 &&
                        null != t6 &&
                        null != t7;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   OctaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7, t8) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2 && 1 == t8 % 2;
     *
     *   OctaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7, t8) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7 && 10 < t8;
     *
     *   octaAllOf().test(5, 6, 7, 11, 12, 0, 15, 3);                                   // true
     *   octaAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33, 41, 17, 33, 77);   // true
     *   octaAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12, 22, 14, 0, 15);     // false
     * </pre>
     *
     * @param predicates
     *    {@link OctaPredicate} to verify
     *
     * @return {@link OctaPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaAllOf(final OctaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return octaAlwaysTrue();
        }
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4, t5, t6, t7, t8);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link OctaPredicate} with {@code false} as result.
     *
     * @return {@link OctaPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaAlwaysFalse() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> false;
    }


    /**
     * Returns a {@link OctaPredicate} with {@code true} as result.
     *
     * @return {@link OctaPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaAlwaysTrue() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   OctaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7, t8) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2 && 1 == t8 % 2;
     *
     *   OctaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7, t8) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7 && 10 < t8;
     *
     *   octaAnyOf().test(5, 6, 7, 11, 14, 4, 0, 3);                                    // false
     *   octaAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7, 3, 9, 11, 5);        // true
     *   octaAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16, 22, 18, 20, 14);   // true
     *   octaAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12, 8, 5, 0, 4);        // false
     * </pre>
     *
     * @param predicates
     *    {@link OctaPredicate} to verify
     *
     * @return {@link OctaPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link OctaPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaAnyOf(final OctaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return octaAlwaysFalse();
        }
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> {
            for (OctaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4, t5, t6, t7, t8)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link OctaPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link OctaPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaIsNull() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4 &&
                        null == t5 &&
                        null == t6 &&
                        null == t7 &&
                        null == t8;
    }


    /**
     * Returns a {@link OctaPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link OctaPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> OctaPredicate<T1, T2, T3, T4, T5, T6, T7, T8> octaNonNull() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4 &&
                        null != t5 &&
                        null != t6 &&
                        null != t7 &&
                        null != t8;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   NonaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2 && 1 == t8 % 2 && 1 == t9 % 2;
     *
     *   NonaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7 && 10 < t8 && 10 < t9;
     *
     *   nonaAllOf().test(5, 6, 7, 11, 12, 0, 15, 3, 16);                                   // true
     *   nonaAllOf(allAreOdd, allGreaterThan10).test(11, 15, 19, 33, 41, 17, 33, 77, 55);   // true
     *   nonaAllOf(allAreOdd, allGreaterThan10).test(9, 21, 33, 12, 22, 14, 0, 15, 7);      // false
     * </pre>
     *
     * @param predicates
     *    {@link NonaPredicate} to verify
     *
     * @return {@link NonaPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaAllOf(final NonaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return nonaAlwaysTrue();
        }
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = Objects.isNull(currentPred) || currentPred.test(t1, t2, t3, t4, t5, t6, t7, t8, t9);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Returns a {@link NonaPredicate} with {@code false} as result.
     *
     * @return {@link NonaPredicate} always returning {@code false}
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaAlwaysFalse() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> false;
    }


    /**
     * Returns a {@link NonaPredicate} with {@code true} as result.
     *
     * @return {@link NonaPredicate} always returning {@code true}
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaAlwaysTrue() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> true;
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   NonaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
     *      1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2 && 1 == t8 % 2 && 1 == t9 % 2;
     *
     *   NonaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
     *      10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7 && 10 < t8 && 10 < t9;
     *
     *   nonaAnyOf().test(5, 6, 7, 11, 14, 4, 0, 3, 18);                                    // false
     *   nonaAnyOf(allAreOdd, allGreaterThan10).test(1, 15, 19, 7, 3, 9, 11, 5, 33);        // true
     *   nonaAnyOf(allAreOdd, allGreaterThan10).test(12, 14, 19, 16, 22, 18, 20, 14, 42);   // true
     *   nonaAnyOf(allAreOdd, allGreaterThan10).test(9, 22, 33, 12, 8, 5, 0, 4, 25);        // false
     * </pre>
     *
     * @param predicates
     *    {@link NonaPredicate} to verify
     *
     * @return {@link NonaPredicate} returning {@code true} if at least one of provided {@code predicates} returns {@code true},
     *          {@link NonaPredicate} returning {@code false} otherwise
     */
    @SafeVarargs
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaAnyOf(final NonaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return nonaAlwaysFalse();
        }
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> {
            for (NonaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9> predicate: predicates) {
                if (Objects.nonNull(predicate) && predicate.test(t1, t2, t3, t4, t5, t6, t7, t8, t9)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link NonaPredicate} that verifies if provided parameters are {@code null}.
     *
     * @return {@link NonaPredicate} returning {@code true} if given parameters are {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaIsNull() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                null == t1 &&
                        null == t2 &&
                        null == t3 &&
                        null == t4 &&
                        null == t5 &&
                        null == t6 &&
                        null == t7 &&
                        null == t8 &&
                        null == t9;
    }


    /**
     * Returns a {@link NonaPredicate} that verifies if provided parameters are not {@code null}.
     *
     * @return {@link NonaPredicate} returning {@code true} if given parameters are not {@code null}, {@code false} otherwise
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> NonaPredicate<T1, T2, T3, T4, T5, T6, T7, T8, T9> nonaNonNull() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) ->
                null != t1 &&
                        null != t2 &&
                        null != t3 &&
                        null != t4 &&
                        null != t5 &&
                        null != t6 &&
                        null != t7 &&
                        null != t8 &&
                        null != t9;
    }


    /**
     * Used when we want to get the unique elements of a given {@link Collection} by a specific property of its objects
     *
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to distinct the elements
     *
     * @return unique object
     */
    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    /**
     *    Transforms the given {@link BiPredicate} {@code filterPredicate} into a {@link Predicate} using {@link Map#entry(Object, Object)}
     * as source. If {@code predicate} or {@link Map#entry(Object, Object)} to verify are {@code null} will return {@code true}.
     *
     * @param predicate
     *    {@link BiPredicate} to transform into a {@link Predicate}
     *
     * @return {@link Predicate} to verify {@link Map#entry(Object, Object)} instances
     */
    public static <K, V> Predicate<Map.Entry<K, V>> fromBiPredicateToMapEntryPredicate(final BiPredicate<? super K, ? super V> predicate) {
        if (Objects.isNull(predicate)) {
            return alwaysTrue();
        }
        return (entry) ->
                Objects.isNull(entry) ||
                        predicate.test(
                                entry.getKey(),
                                entry.getValue()
                        );
    }


    /**
     * Returns the given {@code predicate} if not {@code null}, {@link PredicateUtil#alwaysFalse()} otherwise.
     *
     * @param predicate
     *    {@link Predicate} to return if not {@code null}
     *
     * @return {@code predicate} if not {@code null},
     *         {@link PredicateUtil#alwaysFalse()} otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getOrAlwaysFalse(final Predicate<? super T> predicate) {
        return (Predicate<T>) getOrElse(
                predicate,
                alwaysFalse()
        );
    }


    /**
     * Returns the given {@code predicate} if not {@code null}, {@link PredicateUtil#biAlwaysFalse()}} otherwise.
     *
     * @param predicate
     *    {@link BiPredicate} to return if not {@code null}
     *
     * @return {@code predicate} if not {@code null},
     *         {@link PredicateUtil#biAlwaysFalse()} otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> BiPredicate<T1, T2> getOrAlwaysFalse(final BiPredicate<? super T1, ? super T2> predicate) {
        return (BiPredicate<T1, T2>) getOrElse(
                predicate,
                biAlwaysFalse()
        );
    }


    /**
     * Returns the given {@code predicate} if not {@code null}, {@link PredicateUtil#alwaysTrue()} otherwise.
     *
     * @param predicate
     *    {@link Predicate} to return if not {@code null}
     *
     * @return {@code predicate} if not {@code null},
     *         {@link PredicateUtil#alwaysTrue()} otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getOrAlwaysTrue(final Predicate<? super T> predicate) {
        return (Predicate<T>) getOrElse(
                predicate,
                alwaysTrue()
        );
    }


    /**
     * Returns the given {@code predicate} if not {@code null}, {@link PredicateUtil#biAlwaysTrue()}} otherwise.
     *
     * @param predicate
     *    {@link BiPredicate} to return if not {@code null}
     *
     * @return {@code predicate} if not {@code null},
     *         {@link PredicateUtil#biAlwaysTrue()} otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T1, T2> BiPredicate<T1, T2> getOrAlwaysTrue(final BiPredicate<? super T1, ? super T2> predicate) {
        return (BiPredicate<T1, T2>) getOrElse(
                predicate,
                biAlwaysTrue()
        );
    }


    /**
     * Returns a {@link Predicate} that verifies if provided parameter is {@code null}.
     *
     * @return {@link Predicate} returning {@code true} if given parameter is {@code null}, {@code false} otherwise
     */
    public static <T> Predicate<T> isNull() {
        return Objects::isNull;
    }


    /**
     * Returns a {@link Predicate} that verifies if provided parameter is not {@code null}.
     *
     * @return {@link Predicate} returning {@code true} if given parameter is not {@code null}, {@code false} otherwise
     */
    public static <T> Predicate<T> nonNull() {
        return Objects::nonNull;
    }

}
