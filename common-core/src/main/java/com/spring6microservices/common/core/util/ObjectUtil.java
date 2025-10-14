package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.spring6microservices.common.core.util.PredicateUtil.alwaysTrue;
import static java.util.Optional.ofNullable;

@UtilityClass
public class ObjectUtil {

    /**
     * Returns the first not {@code null} value of the provided ones.
     *
     * <pre>
     *    coalesce(                      Result:
     *       null,                        Optional(12)
     *       12,
     *       15
     *    )
     * </pre>
     *
     * @param valuesToVerify
     *    Values to check the first not {@code null} one
     *
     * @return {@link Optional} containing the first not {@code null} value included in {@code valuesToVerify},
     *         {@link Optional#empty()} otherwise.
     */
    @SafeVarargs
    public static <T> Optional<T> coalesce(T ...valuesToVerify) {
        return ofNullable(valuesToVerify)
                .flatMap(values ->
                        Arrays.stream(values)
                                .filter(Objects::nonNull)
                                .findFirst()
                );
    }


    /**
     * Determine whether the given {@code sourceArray} is empty: i.e. {@code null} or of zero length.
     *
     * @param sourceArray
     *    The array to check
     *
     * @return {@code true} if given {@code sourceArray} is {@code null} or has no elements,
     *         {@code false} otherwise.
     */
    public static boolean isEmpty(final Object[] sourceArray) {
        return null == sourceArray ||
                0 == sourceArray.length;
    }


    /**
     * Return the given {@code sourceInstance} if is not {@code null}. Otherwise, returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceInstance} if is not {@code null}, {@code defaultValue} otherwise
     */
    public static <T> T getOrElse(final T sourceInstance,
                                  final T defaultValue) {
        return ofNullable(sourceInstance)
                .orElse(defaultValue);
    }


    /**
     *    Return the given {@code sourceInstance} if is not {@code null} and verifies {@code filterPredicate}.
     * Otherwise, returns {@code defaultValue}.
     *
     * @apiNote
     *   If {@code filterPredicate} is {@code null} then no filter will be applied.
     *
     * <pre>
     *    getOrElse(                               Result:
     *       "   ",                                 "other"
     *       s -> s.trim().size() > 0,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param filterPredicate
     *    {@link Predicate} to apply if {@code sourceInstance} is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceInstance} if is not {@code null} and verifies {@code filterPredicate},
     *         {@code defaultValue} otherwise
     */
    public static <T> T getOrElse(final T sourceInstance,
                                  final Predicate<? super T> filterPredicate,
                                  final T defaultValue) {
        final Predicate<? super T> finalPredicateToMatch = getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return ofNullable(sourceInstance)
                .filter(finalPredicateToMatch)
                .orElse(defaultValue);
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getOrElse(                     Result:
     *       23,                          "23"
     *       Object::toString,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    Returned value if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> E getOrElse(final T sourceInstance,
                                     final Function<? super T, ? extends E> mapper,
                                     final E defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance)
                )
                .orElse(defaultValue);
    }


    /**
     *    Using the provided {@link Function}s {@code mapper1} and {@code mapper2}, transform/extract from the given
     * {@code sourceInstance} the related value. Otherwise, returns {@code defaultValue}.
     *
     * @apiNote
     *   If {@code mapper1} or {@code mapper2} are {@code null} then {@code defaultValue} is returned.
     *
     * <pre>
     *    getOrElse(                     Result:
     *       23,                          "23_v2"
     *       Object::toString,
     *       s -> s + "_v2"
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper1
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param mapper2
     *    A mapping {@link Function} to use required information of the result of {@code mapper1}
     * @param defaultValue
     *    Returned value if applying {@code mapper1} and/or {@code mapper2}, no value is obtained.
     *
     * @return {@code mapper1} and {@code mapper2} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T1, T2, R> R getOrElse(final T1 sourceInstance,
                                          final Function<? super T1, ? extends T2> mapper1,
                                          final Function<? super T2, ? extends R> mapper2,
                                          final R defaultValue) {
        if (null == sourceInstance || null == mapper1 || null == mapper2) {
            return defaultValue;
        }
        T2 mapper1Result = mapper1.apply(
                sourceInstance
        );
        if (null != mapper1Result) {
            R mapper2Result = mapper2.apply(
                    mapper1Result
            );
            if (null != mapper2Result) {
                return mapper2Result;
            }
        }
        return defaultValue;
    }


    /**
     *    Return the given {@code sourceInstance} if is not {@code null}. Otherwise, returns the result of the {@link Supplier}
     * {@code defaultValue}.
     *
     * <pre>
     *    getOrElseGet(                  Result:
     *       23,                          23
     *       () -> 25
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    {@link Supplier} with the alternative value to return
     *
     * @return {@code sourceInstance} if is not {@code null}, result of {@code defaultValue} otherwise
     *
     * @throws IllegalArgumentException if {@code defaultValue} is {@code null} and {@code sourceInstance} is {@code null}
     */
    public static <T> T getOrElseGet(final T sourceInstance,
                                     final Supplier<? extends T> defaultValue) {
        return ofNullable(sourceInstance)
                .orElseGet(() -> {
                    AssertUtil.notNull(defaultValue, "defaultValue must be not null");
                    return defaultValue.get();
                });
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise, returns the result of the {@link Supplier} {@code defaultValue}.
     *
     * <pre>
     *    getOrElseGet(                  Result:
     *       23,                          "23"
     *       Object::toString,
     *       () -> "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    {@link Supplier} with the alternative value to return if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         result of {@code defaultValue} otherwise.
     *
     * @throws IllegalArgumentException if {@code defaultValue} is {@code null} and {@code sourceInstance} is {@code null}
     */
    public static <T, E> E getOrElseGet(final T sourceInstance,
                                        final Function<? super T, ? extends E> mapper,
                                        final Supplier<? extends E> defaultValue) {
        if (null != sourceInstance && null != mapper) {
            final E mapperResult = mapper.apply(
                    sourceInstance
            );
            if (null != mapperResult) {
                return mapperResult;
            }
        }
        AssertUtil.notNull(defaultValue, "defaultValue must be not null");
        return defaultValue.get();
    }


    /**
     *    Using the provided {@link Function}s {@code mapper1} and {@code mapper2}, transform/extract from the given
     * {@code sourceInstance} the related value. Otherwise, returns the result of the {@link Supplier} {@code defaultValue}.
     *
     * @apiNote
     *   If {@code mapper1} or {@code mapper2} are {@code null} then {@code defaultValue} is returned.
     *
     * <pre>
     *    getOrElseGet(                  Result:
     *       23,                          "23_v2"
     *       Object::toString,
     *       s -> s + "_v2"
     *       () -> "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper1
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param mapper2
     *    A mapping {@link Function} to use required information of the result of {@code mapper1}
     * @param defaultValue
     *    {@link Supplier} with the alternative value to return if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper1} and {@code mapper2} {@code apply} method if not {@code null} is returned,
     *         result of {@code defaultValue} otherwise.
     *
     * @throws IllegalArgumentException if {@code defaultValue} is {@code null} and {@code sourceInstance} is {@code null}
     */
    public static <T1, T2, R> R getOrElseGet(final T1 sourceInstance,
                                             final Function<? super T1, ? extends T2> mapper1,
                                             final Function<? super T2, ? extends R> mapper2,
                                             final Supplier<? extends R> defaultValue) {



        if (null != sourceInstance && null != mapper1 && null != mapper2) {
            final T2 mapper1Result = mapper1.apply(
                    sourceInstance
            );
            if (null != mapper1Result) {
                R mapper2Result = mapper2.apply(
                        mapper1Result
                );
                if (null != mapper2Result) {
                    return mapper2Result;
                }
            }
        }
        AssertUtil.notNull(defaultValue, "defaultValue must be not null");
        return defaultValue.get();
    }

}
