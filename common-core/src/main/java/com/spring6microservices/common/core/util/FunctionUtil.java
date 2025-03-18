package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class FunctionUtil {

    /**
     *    Transforms the given {@link BiFunction} {@code keyValueMapper} into another one used to create new objects from
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyValueMapper
     *    {@link BiFunction} to transform key/value of returned {@link Function} {@link Map#entry(Object, Object)} source
     *
     * @return {@link Function} to transform {@link Map#entry(Object, Object)} instances into another object
     *
     * @throws IllegalArgumentException if {@code keyValueMapper} is {@code null}
     * @throws NullPointerException if provided {@link Map#entry(Object, Object)} is {@code null}
     */
    public static <T, K, V> Function<Map.Entry<K, V>, T> fromBiFunctionToMapEntryFunction(final BiFunction<? super K, ? super V, ? extends T> keyValueMapper) {
        AssertUtil.notNull(keyValueMapper, "keyValueMapper must be not null");
        return (entry) ->
                keyValueMapper.apply(
                        entry.getKey(),
                        entry.getValue()
                );
    }


    /**
     *    Transforms the given {@link BiFunction} {@code keyMapper} and {@code valueMapper} into another one used to create
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyMapper
     *    {@link BiFunction} to transform given {@link Map.Entry} to a key of returned {@link Map#entry(Object, Object)}
     * @param valueMapper
     *    {@link BiFunction} to transform given {@link Map.Entry} to a value of returned {@link Map#entry(Object, Object)}
     *
     * @return {@link Function} to create {@link Map#entry(Object, Object)} instances from other ones
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <K1, K2, V1, V2> Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> fromBiFunctionsToMapEntriesFunction(final BiFunction<? super K1, ? super V1, ? extends K2> keyMapper,
                                                                                                                      final BiFunction<? super K1, ? super V1, ? extends V2> valueMapper) {
        AssertUtil.notNull(keyMapper, "keyMapper must be not null");
        AssertUtil.notNull(valueMapper, "valueMapper must be not null");
        return (entryMap) ->
                new AbstractMap.SimpleEntry<>(
                        keyMapper.apply(
                                entryMap.getKey(),
                                entryMap.getValue()
                        ),
                        valueMapper.apply(
                                entryMap.getKey(),
                                entryMap.getValue()
                        )
                );
    }


    /**
     *    Transforms the given {@link Function} {@code keyMapper} and {@code valueMapper} into another one used to create
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyMapper
     *    {@link Function} to transform given element to a key of returned {@link Map#entry(Object, Object)}
     * @param valueMapper
     *    {@link Function} to transform given element to a value of returned {@link Map#entry(Object, Object)}
     *
     * @return {@link Function} to create {@link Map#entry(Object, Object)} instances
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, V> Function<T, Map.Entry<K, V>> fromFunctionsToMapEntryFunction(final Function<? super T, ? extends K> keyMapper,
                                                                                         final Function<? super T, ? extends V> valueMapper) {
        AssertUtil.notNull(keyMapper, "keyMapper must be not null");
        AssertUtil.notNull(valueMapper, "valueMapper must be not null");
        return (t) ->
                new AbstractMap.SimpleEntry<>(
                        keyMapper.apply(t),
                        valueMapper.apply(t)
                );
    }


    /**
     *    {@link BinaryOperator} used in methods like {@link Collectors#toMap(Function, Function, BinaryOperator)} to resolve
     * collisions between values associated with the same key. In this case, when old and new instance already exist, returns
     * the new one.
     *
     * <pre>
     *   toMap(
     *      Map.Entry::getKey,
     *      Map.Entry::getValue,
     *      resolveWithNew()
     *   )
     * </pre>
     */
    public static <T> BinaryOperator<T> resolveWithNew() {
        return (oldInstance, newInstance) -> newInstance;
    }


    /**
     *    {@link BinaryOperator} used in methods like {@link Collectors#toMap(Function, Function, BinaryOperator)} to resolve
     * collisions between values associated with the same key. In this case, when old and new instance already exist, returns
     * the old one.
     *
     * <pre>
     *   toMap(
     *      Map.Entry::getKey,
     *      Map.Entry::getValue,
     *      resolveWithOld()
     *   )
     * </pre>
     */
    public static <T> BinaryOperator<T> resolveWithOld() {
        return (oldInstance, newInstance) -> oldInstance;
    }

}