package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.collection.tuple.Tuple;
import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.functional.PartialFunction;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.spring6microservices.common.core.util.CollectorsUtil.getOrDefaultListSupplier;
import static com.spring6microservices.common.core.util.CollectorsUtil.toMapNullableValues;
import static com.spring6microservices.common.core.util.ComparatorUtil.safeNaturalOrderNullFirst;
import static com.spring6microservices.common.core.util.ComparatorUtil.safeNaturalOrderNullLast;
import static com.spring6microservices.common.core.util.FunctionUtil.fromFunctionsToMapEntryFunction;
import static com.spring6microservices.common.core.util.FunctionUtil.resolveWithNew;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static com.spring6microservices.common.core.util.PredicateUtil.alwaysTrue;
import static com.spring6microservices.common.core.util.PredicateUtil.getOrAlwaysTrue;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@UtilityClass
public class CollectionUtil {

    /**
     *    Adds the provided {@code element} in the given {@code sourceCollection}, returning {@code true} if both parameters
     * are different of {@code null}, {@code false} otherwise.
     *
     * @param sourceCollection
     *    Source {@link Collection} to include {@code element}
     * @param element
     *    Item to add in {@code sourceCollection}
     *
     * @return {@code true} if {@code sourceCollection} and {@code element} are not {@code null},
     *         {@code false} otherwise
     */
    public static <T> boolean addIfNotNull(final Collection<T> sourceCollection,
                                           final T element) {
        return ofNullable(sourceCollection)
                .map(sc ->
                        ofNullable(element)
                                .map(elto -> {
                                    sc.add(elto);
                                    return true;
                                })
                                .orElse(false)
                )
                .orElse(false);
    }


    /**
     *    Returns a new {@link List} using the given {@code sourceCollection}, applying to its elements the composed
     * {@link Function} {@code secondMapper}({@code firstMapper}(x))
     *
     * <pre>
     *    andThen(                       Result:
     *       [1, 2, 3, 6],                ["2", "3", "4", "7"]
     *       i -> i + 1,
     *       Object::toString
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform
     * @param firstMapper
     *    {@link Function} with the first modification to apply
     * @param secondMapper
     *    {@link Function} with the second modification to apply
     *
     * @return {@link List} applying {@code firstMapper} and {@code secondMapper} to the provided {@code sourceCollection}
     *
     * @throws IllegalArgumentException if {@code firstMapper} or {@code secondMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, E, R> List<R> andThen(final Collection<? extends T> sourceCollection,
                                            final Function<? super T, ? extends E> firstMapper,
                                            final Function<? super E, ? extends R> secondMapper) {
        return (List<R>) andThen(
                sourceCollection,
                firstMapper,
                secondMapper,
                ArrayList::new
        );
    }


    /**
     *    Returns a new {@link Collection} using the given {@code sourceCollection}, applying to its elements the
     * composed {@link Function} {@code secondMapper}({@code firstMapper}(x))
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    andThen(                       Result:
     *       [1, 2, 3, 6],                ["2", "3", "4", "7"]
     *       i -> i + 1,
     *       Object::toString,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform
     * @param firstMapper
     *    {@link Function} with the first modification to apply
     * @param secondMapper
     *    {@link Function} with the second modification to apply
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link List} applying {@code firstMapper} and {@code secondMapper} to the provided {@code sourceCollection}
     *
     * @throws IllegalArgumentException if {@code firstMapper} or {@code secondMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E, R> Collection<R> andThen(final Collection<? extends T> sourceCollection,
                                                  final Function<? super T, ? extends E> firstMapper,
                                                  final Function<? super E, ? extends R> secondMapper,
                                                  final Supplier<Collection<R>> collectionFactory) {
        final Supplier<Collection<R>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(firstMapper, "firstMapper must be not null");
        AssertUtil.notNull(secondMapper, "secondMapper must be not null");
        final Function<? super T, ? extends R> finalMapper = firstMapper.andThen(secondMapper);

        return sourceCollection.stream()
                .map(finalMapper)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Returns a new {@link List} using the given {@code sourceCollection}, applying {@code defaultMapper} if the
     * current element verifies {@code filterPredicate}, {@code orElseMapper} otherwise.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be updated using
     * {@code defaultMapper}.
     *
     * <pre>
     *    applyOrElse(                   Result:
     *       [1, 2, 3, 6],                [2, 4, 4, 12]
     *       i -> i % 2 == 1,
     *       i -> i + 1,
     *       i -> i * 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements of {@code sourceCollection}
     * @param defaultMapper
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}
     * @param orElseMapper
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}
     *
     * @return new {@link List} from applying the given {@code defaultMapper} to each element of {@code sourceCollection}
     *         that verifies {@code filterPredicate} and collecting the results or {@code orElseMapper} otherwise
     *
     * @throws IllegalArgumentException if {@code defaultMapper} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> List<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                             final Predicate<? super T> filterPredicate,
                                             final Function<? super T, ? extends E> defaultMapper,
                                             final Function<? super T, ? extends E> orElseMapper) {
        return (List<E>) applyOrElse(
                sourceCollection,
                filterPredicate,
                defaultMapper,
                orElseMapper,
                ArrayList::new
        );
    }


    /**
     *    Returns a new {@link Collection} using the given {@code sourceCollection}, applying {@code defaultMapper} if
     * the current element verifies {@code filterPredicate}, {@code orElseMapper} otherwise.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be updated using
     * {@code defaultMapper}. If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    applyOrElse(                   Result:
     *       [1, 2, 3, 6],                [2, 4, 4, 12]
     *       i -> i % 2 == 1,
     *       i -> i + 1,
     *       i -> i * 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements of {@code sourceCollection}
     * @param defaultMapper
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}
     * @param orElseMapper
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Collection} from applying the given {@code defaultMapper} to each element of {@code sourceCollection}
     *         that verifies {@code filterPredicate} and collecting the results or {@code orElseMapper} otherwise
     *
     * @throws IllegalArgumentException if {@code defaultMapper} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                                   final Predicate<? super T> filterPredicate,
                                                   final Function<? super T, ? extends E> defaultMapper,
                                                   final Function<? super T, ? extends E> orElseMapper,
                                                   final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(defaultMapper, "defaultMapper must be not null");
        AssertUtil.notNull(orElseMapper, "orElseMapper must be not null");
        return applyOrElse(
                sourceCollection,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
                        defaultMapper
                ),
                orElseMapper,
                finalCollectionFactory
        );
    }


    /**
     *    Returns a new {@link List} using the given {@code sourceCollection}, applying {@link PartialFunction#apply(Object)}
     * if the current element verifies {@link PartialFunction#isDefinedAt(Object)}, {@code orElseMapper} otherwise.
     *
     * <pre>
     *    applyOrElse(                                Result:
     *       [1, 2, 3, 6],                             [2, 4, 4, 12]
     *       PartialFunction.of(
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : i + 1
     *       ),
     *       i -> i * 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param orElseMapper
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@link PartialFunction#isDefinedAt(Object)}
     *
     * @return new {@link List} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results, {@code orElseMapper} otherwise
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> List<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                             final PartialFunction<? super T, ? extends E> partialFunction,
                                             final Function<? super T, ? extends E> orElseMapper) {
        return (List<E>) applyOrElse(
                sourceCollection,
                partialFunction,
                orElseMapper,
                ArrayList::new
        );
    }


    /**
     *    Returns a new {@link Collection} using the given {@code sourceCollection}, applying {@link PartialFunction#apply(Object)}
     * if the current element verifies {@link PartialFunction#isDefinedAt(Object)}, {@code orElseMapper} otherwise.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    applyOrElse(                                Result:
     *       [1, 2, 3, 6],                             [2, 4, 4, 12]
     *       PartialFunction.of(
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : i + 1
     *       ),
     *       i -> i * 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param orElseMapper
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Collection} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results, {@code orElseMapper} otherwise
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                                   final PartialFunction<? super T, ? extends E> partialFunction,
                                                   final Function<? super T, ? extends E> orElseMapper,
                                                   final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");
        AssertUtil.notNull(orElseMapper, "orElseMapper must be not null");
        return sourceCollection.stream()
                .map(elto ->
                        partialFunction.isDefinedAt(elto)
                                ? partialFunction.apply(elto)
                                : orElseMapper.apply(elto)
                )
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     * Returns a new {@link Collection} after applying to {@code sourceCollection}:
     * <p>
     * <ul>
     *     <li>Filter its elements using {@code filterPredicate}.</li>
     *     <li>Transform its filtered elements using {@code mapFunction}.</li>
     * </ul>
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be transformed.
     *
     * <pre>
     *    collect(                       Result:
     *       [1, 2, 3, 6],                ["1", "3"]
     *       i -> 1 == i % 2,
     *       Object::toString
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements of {@code sourceCollection}
     * @param mapFunction
     *    {@link Function} to transform filtered elements of {@code sourceCollection}
     *
     * @return new {@link List} from applying the given {@link Function} to each element of {@code sourceCollection}
     *         on which {@link Predicate} returns {@code true} and collecting the results
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, E> List<E> collect(final Collection<? extends T> sourceCollection,
                                         final Predicate<? super T> filterPredicate,
                                         final Function<? super T, ? extends E> mapFunction) {
        return (List<E>) collect(
                sourceCollection,
                filterPredicate,
                mapFunction,
                ArrayList::new
        );
    }


    /**
     * Returns a new {@link Collection} after applying to {@code sourceCollection}:
     * <p>
     * <ul>
     *     <li>Filter its elements using {@code filterPredicate}.</li>
     *     <li>Transform its filtered elements using {@code mapFunction}.</li>
     * </ul>
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be transformed. If {@code collectionFactory}
     * is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    collect(                       Result:
     *       [1, 2, 3, 6],                ["1", "3"]
     *       i -> 1 == i % 2,
     *       Object::toString,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param mapFunction
     *    {@link Function} to transform filtered elements of the source {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Collection} from applying the given {@link Function} to each element of {@code sourceCollection}
     *         on which {@link Predicate} returns {@code true} and collecting the results
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> collect(final Collection<? extends T> sourceCollection,
                                               final Predicate<? super T> filterPredicate,
                                               final Function<? super T, ? extends E> mapFunction,
                                               final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(mapFunction, "mapFunction must be not null");
        return collect(
                sourceCollection,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
                        mapFunction
                ),
                finalCollectionFactory
        );
    }


    /**
     * Returns a new {@link Collection} after applying to {@code sourceCollection}:
     * <p>
     * <ul>
     *     <li>Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}.</li>
     *     <li>Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}.</li>
     * </ul>
     *
     * <pre>
     *    collect(                                    Result:
     *       [1, 2, 3, 6],                             ["1", "3"]
     *       PartialFunction.of(
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : i.toString()
     *       )
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     *
     * @return new {@link List} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, E> List<E> collect(final Collection<? extends T> sourceCollection,
                                         final PartialFunction<? super T, ? extends E> partialFunction) {
        return (List<E>) collect(
                sourceCollection,
                partialFunction,
                ArrayList::new
        );
    }


    /**
     * Returns a new {@link Collection} after applying to {@code sourceCollection}:
     * <p>
     * <ul>
     *     <li>Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}.</li>
     *     <li>Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}.</li>
     * </ul>
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    collect(                                    Result:
     *       [1, 2, 3, 6],                             ["1", "3"]
     *       PartialFunction.of(
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : i.toString()
     *       ),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Collection} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> collect(final Collection<? extends T> sourceCollection,
                                               final PartialFunction<? super T, ? extends E> partialFunction,
                                               final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");
        return sourceCollection
                .stream()
                .filter(partialFunction::isDefinedAt)
                .map(partialFunction)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Finds the first element of the {@code sourceCollection} for which the given {@link PartialFunction} is defined,
     * and applies the {@link PartialFunction} to it.
     *
     * <pre>
     *    collectFirst(                               Result:
     *       [1, 2, 3, 6],                             Optional("2")
     *       PartialFunction.of(
     *          i -> null != i && 0 == i % 2,
     *          i -> null == i
     *             ? null
     *             : i.toString()
     *       )
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter elements of {@code sourceCollection} and transform the first one defined at function's domain
     *
     * @return {@link Optional} value containing {@code partialFunction} applied to the first value for which it is defined,
     *         {@link Optional#empty()} if none exists.
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, E> Optional<E> collectFirst(final Collection<? extends T> sourceCollection,
                                                  final PartialFunction<? super T, ? extends E> partialFunction) {
        if (isEmpty(sourceCollection)) {
            return empty();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");
        return find(
                sourceCollection,
                partialFunction::isDefinedAt
        )
        .map(partialFunction);
    }

    /**
     * Returns a new {@link List} containing the elements of provided {@link Collection}s {@code collections}.
     *
     * <pre>
     *    concat(                        Result:
     *       [1, 2],                      [1, 2, 1, 4]
     *       [1, 4]
     *    )
     * </pre>
     *
     * @param collections
     *    {@link Collection}s to concat
     *
     * @return {@link List} with the elements of {@code collections}
     */
    @SafeVarargs
    public static <T> List<T> concat(final Collection<? extends T> ...collections) {
        return (List<T>) concat(
                ArrayList::new,
                collections
        );
    }


    /**
     * Returns a new {@link Collection} containing the elements of provided {@link Collection}s {@code collections}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    concat(                        Result:
     *       ArrayList::new,              [1, 2, 1, 4]
     *       [1, 2],
     *       [1, 4]
     *    )
     * </pre>
     *
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     * @param collections
     *    {@link Collection}s to concat
     *
     * @return {@link Collection} with the elements of {@code collections}
     */
    @SafeVarargs
    public static <T> Collection<T> concat(final Supplier<Collection<T>> collectionFactory,
                                           final Collection<? extends T> ...collections) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        return ofNullable(collections)
                .map(c ->
                        Stream.of(c)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .collect(
                                        Collectors.toCollection(finalCollectionFactory)
                                )
                )
                .orElseGet(finalCollectionFactory);
    }


    /**
     * Returns a new {@link List} containing the elements of provided {@code sourceCollection}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to copy
     *
     * @return {@link List} containing all elements included in {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> copy(final Collection<? extends T> sourceCollection) {
        return (List<T>) copy(
                sourceCollection,
                ArrayList::new
        );
    }


    /**
     * Returns a new {@link Collection} containing the elements of provided {@code sourceCollection}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    copy(                          Result:
     *       [1, 2, 3, 2],                [1, 2, 3]
     *       HashSet::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to copy
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection} containing all elements included in {@code sourceCollection}
     */
    public static <T> Collection<T> copy(final Collection<? extends T> sourceCollection,
                                         final Supplier<Collection<T>> collectionFactory) {
        final Collection<T> result = getOrDefaultListSupplier(collectionFactory).get();
        if (!isEmpty(sourceCollection)) {
            result.addAll(sourceCollection);
        }
        return result;
    }


    /**
     * Counts the number of elements in the {@code sourceCollection} which satisfy the {@code filterPredicate}.
     *
     * <pre>
     *    count(                         Result:
     *       [1, 2, 3, 6],                2
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *   {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return the number of elements satisfying the {@link Predicate} {@code filterPredicate}
     */
    public static <T> int count(final Collection<? extends T> sourceCollection,
                                final Predicate<? super T> filterPredicate) {
        if (isEmpty(sourceCollection)) {
            return 0;
        }
        if (null == filterPredicate) {
            return sourceCollection.size();
        }
        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .mapToInt(elto -> 1)
                .sum();
    }


    /**
     *    Returns a {@link List} removing the longest prefix of elements included in {@code sourceCollection} that satisfy
     * the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    dropWhile(                     Result:
     *       [1, 3, 4, 5, 6],             [4, 5, 6]
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return the longest suffix of provided {@code sourceCollection} whose first element does not satisfy {@code filterPredicate}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> dropWhile(final Collection<? extends T> sourceCollection,
                                        final Predicate<? super T> filterPredicate) {
        return (List<T>) dropWhile(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link List} removing the longest prefix of elements included in {@code sourceCollection} that satisfy
     * the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned. If
     * {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    dropWhile(                     Result:
     *       [1, 3, 4, 5, 6],             [4, 5, 6]
     *       i -> 1 == i % 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return the longest suffix of provided {@code sourceCollection} whose first element does not satisfy {@code filterPredicate}
     */
    public static <T> Collection<T> dropWhile(final Collection<? extends T> sourceCollection,
                                              final Predicate<? super T> filterPredicate,
                                              final Supplier<Collection<T>> collectionFactory) {
        if (isEmpty(sourceCollection) || null == filterPredicate) {
            return copy(
                    sourceCollection,
                    collectionFactory
            );
        }
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        return sourceCollection
                .stream()
                .dropWhile(filterPredicate)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Returns a {@link List} with the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    filter(                        Result:
     *       [1, 2, 3, 6],                [1, 3]
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return empty {@link List} if {@code sourceCollection} has no elements or no one verifies provided {@code filterPredicate},
     *         otherwise a new {@link List} with the elements of {@code sourceCollection} which verify {@code filterPredicate}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> filter(final Collection<? extends T> sourceCollection,
                                     final Predicate<? super T> filterPredicate) {
        return (List<T>) filter(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} with the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned. If
     * {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    filter(                        Result:
     *       [1, 2, 3, 6],                [1, 3]
     *       i -> 1 == i % 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return empty {@link Collection} if {@code sourceCollection} has no elements or no one verifies provided {@code filterPredicate},
     *         otherwise a new {@link Collection} with the elements of {@code sourceCollection} which verify {@code filterPredicate}
     */
    public static <T> Collection<T> filter(final Collection<? extends T> sourceCollection,
                                           final Predicate<? super T> filterPredicate,
                                           final Supplier<Collection<T>> collectionFactory) {
        if (isEmpty(sourceCollection) || null == filterPredicate) {
            return copy(
                    sourceCollection,
                    collectionFactory
            );
        }
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Returns a {@link List} removing the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    filterNot(                     Result:
     *       [1, 2, 3, 6],                [2, 6]
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return empty {@link List} if {@code sourceCollection} has no elements,
     *         otherwise a new {@link List} with the elements of {@code sourceCollection} which do not verify {@code filterPredicate}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> filterNot(final Collection<? extends T> sourceCollection,
                                        final Predicate<? super T> filterPredicate) {
        return (List<T>) filterNot(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} removing the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned. If
     * {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    filterNot(                     Result:
     *       [1, 2, 3, 6],                [2, 6]
     *       i -> 1 == i % 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return empty {@link Collection} if {@code sourceCollection} has no elements,
     *         otherwise a new {@link Collection} with the elements of {@code sourceCollection} which do not verify {@code filterPredicate}
     */
    public static <T> Collection<T> filterNot(final Collection<? extends T> sourceCollection,
                                              final Predicate<? super T> filterPredicate,
                                              final Supplier<Collection<T>> collectionFactory) {
        final Predicate<? super T> finalFilterPredicate =
                null == filterPredicate
                        ? null
                        : filterPredicate.negate();

        return filter(
                sourceCollection,
                finalFilterPredicate,
                collectionFactory
        );
    }


    /**
     * Finds the first element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * <pre>
     *    find(                          Result:
     *       [1, 2, 3, 6],                Optional(2)
     *       i -> 0 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> find(final Collection<? extends T> sourceCollection,
                                                 final Predicate<? super T> filterPredicate) {
        if (isEmpty(sourceCollection) || null == filterPredicate) {
            return empty();
        }
        return getCollectionKeepingInternalOrdination(sourceCollection)
                .stream()
                .filter(filterPredicate)
                .findFirst();
    }


    /**
     * Finds the last element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * <pre>
     *    findLast(                      Result:
     *       [1, 2, 3, 6],                Optional(6)
     *       i -> 0 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the last element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> findLast(final Collection<? extends T> sourceCollection,
                                                     final Predicate<? super T> filterPredicate) {
        if (isEmpty(sourceCollection) || null == filterPredicate) {
            return empty();
        }
        return reverseList(sourceCollection)
                .stream()
                .filter(filterPredicate)
                .findFirst();
    }


    /**
     * Converts given {@code sourceCollection} into a {@link List} formed by the elements of these iterable collections.
     *
     * <pre>
     *    flatten(                       Result:
     *       [5, [3, 2], 9]               [5, 3, 2, 9]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Object} to concat
     *
     * @return {@link List} resulting from concatenating all element of {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> flatten(final Collection<Object> sourceCollection) {
        return (List<T>) flatten(
                sourceCollection,
                ArrayList::new
        );
    }


    /**
     * Converts given {@code sourceCollection} into a {@link Collection} formed by the elements of these iterable collections.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    flatten(                       Result:
     *       [5, [3, 2], 5],              [5, 3, 2]
     *       HashSet::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Object} to concat
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection} resulting from concatenating all element of {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> flatten(final Collection<Object> sourceCollection,
                                            final Supplier<Collection<T>> collectionFactory) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        Collection<T> result = finalCollectionFactory.get();
        for (Object elto: sourceCollection) {
            if (elto instanceof Collection) {
                result.addAll(
                        flatten(
                                (Collection<Object>) elto,
                                collectionFactory
                        )
                );
            }
            else {
                result.add((T) elto);
            }
        }
        return result;
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to all
     * elements of {@code sourceCollection}, going left to right.
     *
     * @apiNote
     *    If {@code sourceCollection} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     *
     * <pre>
     *    foldLeft(                      Result:
     *       [5, 7, 9],                   315
     *       1,
     *       (a, b) -> a * b
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link BiFunction} which combines elements
     *
     * @return result of inserting {@code accumulator} between consecutive elements of {@code sourceCollection}, going
     *         left to right with the start value {@code initialValue} on the left.
     */
    public static <T, E> E foldLeft(final Collection<? extends T> sourceCollection,
                                    final E initialValue,
                                    final BiFunction<E, ? super T, E> accumulator) {
        return foldLeft(
                sourceCollection,
                alwaysTrue(),
                initialValue,
                accumulator
        );
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to elements
     * of {@code sourceCollection} that verify {@code filterPredicate}, going left to right.
     *
     * @apiNote
     *    If {@code sourceCollection} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     * If {@code filterPredicate} is {@code null} then all elements will be used to calculate the final value.
     *
     * <pre>
     *    foldLeft(                      Result:
     *       [5, 7, 8, 9],                315
     *       1,
     *       (a, b) -> a * b,
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link BiFunction} which combines elements
     *
     * @return result of inserting {@code accumulator} between consecutive elements of {@code sourceCollection}, going
     *         left to right with the start value {@code initialValue} on the left.
     */
    public static <T, E> E foldLeft(final Collection<? extends T> sourceCollection,
                                    final Predicate<? super T> filterPredicate,
                                    final E initialValue,
                                    final BiFunction<E, ? super T, E> accumulator) {
        return ofNullable(sourceCollection)
                .map(CollectionUtil::getCollectionKeepingInternalOrdination)
                .map(sc -> {
                    E result = initialValue;
                    if (null != accumulator) {
                        final Predicate<? super T> finalFilterPredicate = getOrAlwaysTrue(filterPredicate);
                        for (T element: sc) {
                            if (finalFilterPredicate.test(element)) {
                                result = accumulator.apply(result, element);
                            }
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to all
     * elements of {@code sourceCollection}, going right to left.
     *
     * @apiNote
     *    If {@code sourceCollection} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     *
     * <pre>
     *    foldRight(                     Result:
     *       [5, 7, 9],                   315
     *       1,
     *       (a, b) -> a * b
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link BiFunction} which combines elements
     *
     * @return result of inserting {@code accumulator} between consecutive elements {@code sourceCollection}, going
     *         right to left with the start value {@code initialValue} on the right.
     */
    public static <T, E> E foldRight(final Collection<? extends T> sourceCollection,
                                     final E initialValue,
                                     final BiFunction<E, ? super T, E> accumulator) {
        return foldRight(
                sourceCollection,
                alwaysTrue(),
                initialValue,
                accumulator
        );
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to elements
     * of {@code sourceCollection} that verify {@code filterPredicate}, going right to left.
     *
     * @apiNote
     *    If {@code sourceCollection} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     * If {@code filterPredicate} is {@code null} then all elements will be used to calculate the final value.
     *
     * <pre>
     *    foldRight(                     Result:
     *       [5, 7, 8, 9],                315
     *       1,
     *       (a, b) -> a * b,
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link BiFunction} which combines elements
     *
     * @return result of inserting {@code accumulator} between consecutive elements {@code sourceCollection}, going
     *         right to left with the start value {@code initialValue} on the right.
     */
    public static <T, E> E foldRight(final Collection<? extends T> sourceCollection,
                                     final Predicate<? super T> filterPredicate,
                                     final E initialValue,
                                     final BiFunction<E, ? super T, E> accumulator) {
        return foldLeft(
                reverseList(sourceCollection),
                filterPredicate,
                initialValue,
                accumulator
        );
    }


    /**
     * Returns the number of occurrences of each element contained in {@code sourceCollection}.
     *
     * <pre>
     *    frequency(                     Result:
     *       ["a", "b", "c", "c"]         [("a", 1), ("b", 1), ("c", 2)]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to search
     *
     * @return {@link Map} with number of occurrences of each element in {@code sourceCollection}
     */
    public static <T> Map<T, Integer> frequency(final Collection<? extends T> sourceCollection) {
        return groupMapReduce(
                sourceCollection,
                Function.identity(),
                t -> 1,
                Integer::sum
        );
    }


    /**
     * Returns the number of occurrences of {@code objectToSearch} in {@code sourceCollection}.
     *
     * <pre>
     *    frequency(                     Result:
     *       ["a", "b", "c", "c"],        2
     *       "c"
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param objectToSearch
     *    The object to find the cardinality of
     *
     * @return number of occurrences of {@code objectToSearch} in {@code sourceCollection}
     */
    public static <T> int frequency(final Collection<? extends T> sourceCollection,
                                    final T objectToSearch) {
        if (isEmpty(sourceCollection)) {
            return 0;
        }
        if (sourceCollection instanceof Set && null != objectToSearch) {
            return sourceCollection.contains(objectToSearch)
                    ? 1
                    : 0;
        }
        return count(
                sourceCollection,
                t ->
                        null == objectToSearch
                                ? null == t
                                : null != t && t.equals(objectToSearch)
        );
    }


    /**
     * Returns a {@link List} with the elements included in the given {@link Iterator}.
     *
     * @param sourceIterator
     *    {@link Iterator} with the elements to add in the returned {@link List}
     *
     * @return {@link List} containing the elements accessible using {@code sourceIterator}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromIterator(final Iterator<? extends T> sourceIterator) {
        return (List<T>) fromIterator(
                sourceIterator,
                ArrayList::new
        );
    }


    /**
     * Returns a {@link Collection} with the elements included in the given {@link Iterator}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * @param sourceIterator
     *    {@link Iterator} with the elements to add in the returned {@link List}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return {@link Collection} containing the elements accessible using {@code sourceIterator}
     */
    public static <T> Collection<T> fromIterator(final Iterator<? extends T> sourceIterator,
                                                 final Supplier<Collection<T>> collectionFactory) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (null == sourceIterator || !sourceIterator.hasNext()) {
            return finalCollectionFactory.get();
        }
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                sourceIterator,
                                Spliterator.ORDERED
                        ),
                        false
                )
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Elements added as values of returned {@link Map} will be the same as original {@code sourceCollection}.
     *
     * <pre>
     *    groupBy(                       Result:
     *       [1, 3, 5, 6],                [(0,  [3, 6])
     *       i -> i % 3                    (1,  [1])
     *    )                                (2,  [5])]
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCollection}
     *         to generate the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K> Map<K, List<T>> groupBy(final Collection<? extends T> sourceCollection,
                                                 final Function<? super T, ? extends K> discriminatorKey) {
        return (Map) groupBy(
                sourceCollection,
                discriminatorKey,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Elements added as values of returned {@link Map} will be the same as original {@code sourceCollection}.
     *
     * <pre>
     *    groupBy(                       Result:
     *       [1, 3, 5, 6],                [(0,  [3, 6])
     *       i -> i % 3,                   (1,  [1])
     *       ArrayList::new                (2,  [5])]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCollection}
     *         to generate the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, K> Map<K, Collection<T>> groupBy(final Collection<? extends T> sourceCollection,
                                                       final Function<? super T, ? extends K> discriminatorKey,
                                                       final Supplier<Collection<T>> collectionFactory) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(discriminatorKey, "discriminatorKey must be not null");
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);

        Map<K, Collection<T>> result = new HashMap<>();
        sourceCollection
                .forEach(
                        e -> {
                            K discriminatorResult = discriminatorKey.apply(e);
                            result.putIfAbsent(
                                    discriminatorResult,
                                    finalCollectionFactory.get()
                            );
                            result.get(discriminatorResult)
                                    .add(e);
                        }
                );
        return result;
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Elements added as values of returned {@link Map} will be the same as original {@code sourceCollection}.
     *
     * @apiNote
     *    This method is similar to {@link CollectionUtil#groupBy(Collection, Function)} but {@code discriminatorKey}
     * returns a {@link Collection} of related key values.
     *
     * <pre>
     *    groupByMultiKey(                                  Result:
     *       [1, 2, 3, 6, 11, 12],                           [("even",  [2, 6, 12])
     *       i -> {                                           ("odd",   [1, 3, 11])
     *          List<String> keys = new ArrayList<>();        ("smaller10",  [1, 2, 3, 6])
     *          if (0 == i % 2) {                             ("greaterEqual10",  [11, 12])]
     *             keys.add("even");
     *          }
     *          else {
     *             keys.add("odd");
     *          }
     *          if (10 > i) {
     *             keys.add("smaller10");
     *          }
     *          else {
     *             keys.add("greaterEqual10");
     *          }
     *          return keys;
     *       }
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCollection}
     *         to generate the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K> Map<K, List<T>> groupByMultiKey(final Collection<? extends T> sourceCollection,
                                                         final Function<? super T, Collection<? extends K>> discriminatorKey) {
        return (Map) groupByMultiKey(
                sourceCollection,
                discriminatorKey,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Elements added as values of returned {@link Map} will be the same as original {@code sourceCollection}.
     *
     * @apiNote
     *    This method is similar to {@link CollectionUtil#groupBy(Collection, Function, Supplier)} but {@code discriminatorKey}
     * returns a {@link Collection} of related key values.
     *
     * <pre>
     *    groupByMultiKey(                                  Result:
     *       [1, 2, 3, 6, 11, 12],                           [("even",  [2, 6, 12])
     *       i -> {                                           ("odd",   [1, 3, 11])
     *          List<String> keys = new ArrayList<>();        ("smaller10",  [1, 2, 3, 6])
     *          if (0 == i % 2) {                             ("greaterEqual10",  [11, 12])]
     *             keys.add("even");
     *          }
     *          else {
     *             keys.add("odd");
     *          }
     *          if (10 > i) {
     *             keys.add("smaller10");
     *          }
     *          else {
     *             keys.add("greaterEqual10");
     *          }
     *          return keys;
     *       },
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCollection}
     *         to generate the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, K> Map<K, Collection<T>> groupByMultiKey(final Collection<? extends T> sourceCollection,
                                                               final Function<? super T, Collection<? extends K>> discriminatorKey,
                                                               final Supplier<Collection<T>> collectionFactory) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(discriminatorKey, "discriminatorKey must be not null");
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);

        Map<K, Collection<T>> result = new HashMap<>();
        sourceCollection
                .forEach(
                        e -> {
                            Collection<? extends K> discriminatorKeyResult = getOrElse(
                                    discriminatorKey.apply(e),
                                    new ArrayList<>()
                            );
                            discriminatorKeyResult
                                    .forEach(
                                            k -> {
                                                result.putIfAbsent(
                                                        k,
                                                        finalCollectionFactory.get()
                                                );
                                                result.get(k)
                                                        .add(e);
                                            }
                                    );
                        }
                );
        return result;
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     *
     * <pre>
     *    groupMap(                      Result:
     *       [1, 2, 3, 6],                [(0,  [4, 7])
     *       i -> i % 3,                   (1,  [2])
     *       i -> i + 1                    (2,  [3])]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMap(
                sourceCollection,
                alwaysTrue(),
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value of
     * type V using {@code valueMapper} {@link Function}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be used.
     *
     * <pre>
     *    groupMap(                      Result:
     *       [1, 2, 3, 6, 11],            [(0, [4, 7])
     *       i -> 10 > i,                  (1, [2])
     *       i -> i % 3,                   (2, [3])]
     *       i -> i + 1
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                     final Predicate<? super T> filterPredicate,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMap(
                sourceCollection,
                filterPredicate,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value of
     * type V using {@code valueMapper} {@link Function}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be used. If {@code collectionFactory} is
     * {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    groupMap(                      Result:
     *       [1, 2, 3, 6, 11],            [(0, [4, 7])
     *       i -> 10 > i,                  (1, [2])
     *       i -> i % 3,                   (2, [3])]
     *       i -> i + 1,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, K, V> Map<K, Collection<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                           final Predicate<? super T> filterPredicate,
                                                           final Function<? super T, ? extends K> discriminatorKey,
                                                           final Function<? super T, ? extends V> valueMapper,
                                                           final Supplier<Collection<V>> collectionFactory) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(discriminatorKey, "discriminatorKey must be not null");
        AssertUtil.notNull(valueMapper, "valueMapper must be not null");
        return groupMap(
                sourceCollection,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
                        discriminatorKey,
                        valueMapper
                ),
                getOrDefaultListSupplier(collectionFactory)
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code partialFunction}.
     * Each element in the {@link Collection} is transformed into a {@link Map.Entry} using {@code partialFunction}.
     *
     * <pre>
     *    groupMap(                                         Result:
     *       [1, 2, 3, 6, 9],                                [(0, [4, 10])
     *       PartialFunction.of(                              (1, [2])]
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 i % 3,
     *                 i + 1
     *               )
     *       )
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                     final PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction) {
        return (Map) groupMap(
                sourceCollection,
                partialFunction,
                (Supplier<Collection<V>>) ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code partialFunction}.
     * Each element in the {@link Collection} is transformed into a {@link Map.Entry} using {@code partialFunction}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    groupMap(                                         Result:
     *       [1, 2, 3, 6, 9],                                [(0, [4, 10])
     *       PartialFunction.of(                              (1, [2])]
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 i % 3,
     *                 i + 1
     *               )
     *       ),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, K, V> Map<K, Collection<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                           final PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                           final Supplier<Collection<V>> collectionFactory) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");
        final Supplier<Collection<V>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);

        Map<K, Collection<V>> result = new HashMap<>();
        sourceCollection.stream()
                .filter(partialFunction::isDefinedAt)
                .forEach(
                        e -> {
                            Map.Entry<K, V> keyValue = partialFunction.apply(e);
                            result.putIfAbsent(
                                    keyValue.getKey(),
                                    finalCollectionFactory.get()
                            );
                            result.get(keyValue.getKey())
                                    .add(keyValue.getValue());
                        }
                );
        return result;
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     *
     * @apiNote
     *    This method is similar to {@link CollectionUtil#groupMap(Collection, Function, Function)} but {@code discriminatorKey}
     * returns a {@link Collection} of related key values.
     *
     * <pre>
     *    groupMapMultiKey(                                 Result:
     *       [1, 2, 3, 6, 11, 12],                           [("even",  [2, 6, 12])
     *       i -> {                                           ("odd",   [1, 3, 11])
     *          List<String> keys = new ArrayList<>();        ("smaller10",  [1, 2, 3, 6])
     *          if (0 == i % 2) {                             ("greaterEqual10",  [11, 12])]
     *             keys.add("even");
     *          }
     *          else {
     *             keys.add("odd");
     *          }
     *          if (10 > i) {
     *             keys.add("smaller10");
     *          }
     *          else {
     *             keys.add("greaterEqual10");
     *          }
     *          return keys;
     *       },
     *       Function.identity()
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} are {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMapMultiKey(final Collection<? extends T> sourceCollection,
                                                             final Function<? super T, Collection<? extends K>> discriminatorKey,
                                                             final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMapMultiKey(
                sourceCollection,
                null,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value of
     * type V using {@code valueMapper} {@link Function}.
     *
     * @apiNote
     *    This method is similar to {@link CollectionUtil#groupMap(Collection, Predicate, Function, Function)} but
     * {@code discriminatorKey} returns a {@link Collection} of related key values.
     *
     * <pre>
     *    groupMapMultiKey(                                 Result:
     *       [1, 2, 3, 6, 11, 12],                           [("even",  [2, 6])
     *       i -> 10 > i,                                     ("odd",   [1, 3])
     *       i -> {                                           ("smaller5",  [1, 2, 3])
     *          List<String> keys = new ArrayList<>();        ("greaterEqual5",  [6])]
     *          if (0 == i % 2) {
     *             keys.add("even");
     *          }
     *          else {
     *             keys.add("odd");
     *          }
     *          if (10 > i) {
     *             keys.add("smaller10");
     *          }
     *          else {
     *             keys.add("greaterEqual10");
     *          }
     *          return keys;
     *       },
     *       Function.identity()
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} are {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMapMultiKey(final Collection<? extends T> sourceCollection,
                                                             final Predicate<? super T> filterPredicate,
                                                             final Function<? super T, Collection<? extends K>> discriminatorKey,
                                                             final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMapMultiKey(
                sourceCollection,
                filterPredicate,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value of
     * type V using {@code valueMapper} {@link Function}.
     *
     * @apiNote
     *    This method is similar to {@link CollectionUtil#groupMap(Collection, Predicate, Function, Function, Supplier)} but
     * {@code discriminatorKey} returns a {@link Collection} of related key values.
     * <p>
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    groupMapMultiKey(                                 Result:
     *       [1, 2, 3, 6, 11, 12],                           [("even",  [2, 6])
     *       i -> 10 > i,                                     ("odd",   [1, 3])
     *       i -> {                                           ("smaller5",  [1, 2, 3])
     *          List<String> keys = new ArrayList<>();        ("greaterEqual5",  [6])]
     *          if (0 == i % 2) {
     *             keys.add("even");
     *          }
     *          else {
     *             keys.add("odd");
     *          }
     *          if (10 > i) {
     *             keys.add("smaller10");
     *          }
     *          else {
     *             keys.add("greaterEqual10");
     *          }
     *          return keys;
     *       },
     *       Function.identity(),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and group
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} are {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, K, V> Map<K, Collection<V>> groupMapMultiKey(final Collection<? extends T> sourceCollection,
                                                                   final Predicate<? super T> filterPredicate,
                                                                   final Function<? super T, Collection<? extends K>> discriminatorKey,
                                                                   final Function<? super T, ? extends V> valueMapper,
                                                                   final Supplier<Collection<V>> collectionFactory) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(discriminatorKey, "discriminatorKey must be not null");
        AssertUtil.notNull(valueMapper, "valueMapper must be not null");

        final Supplier<Collection<V>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        final Stream<? extends T> sourceCollectionStream = Objects.isNull(filterPredicate)
                ? sourceCollection.stream()
                : sourceCollection.stream().filter(filterPredicate);

        Map<K, Collection<V>> result = new HashMap<>();
        sourceCollectionStream
                .forEach(
                        e -> {
                            V valueMapperResult = valueMapper.apply(e);
                            Collection<? extends K> discriminatorKeyResult = getOrElse(
                                    discriminatorKey.apply(e),
                                    new ArrayList<>()
                            );
                            discriminatorKeyResult
                                    .forEach(
                                            k -> {
                                                result.putIfAbsent(
                                                        k,
                                                        finalCollectionFactory.get()
                                                );
                                                result.get(k)
                                                        .add(valueMapperResult);
                                            }
                                    );
                        }
                );
        return result;
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} as values, according to {@code discriminatorKey}.
     * All the values that have the same discriminator are then transformed by the {@code valueMapper} {@link Function} and
     * then reduced into a single value with {@code reduceValues}.
     *
     * <pre>
     *    groupMapReduce(                Intermediate Map:          Result:
     *       [1, 2, 3, 6],                [(0,  [4, 7])              [(0, 11),
     *       i -> i % 3,                   (1,  [2])                  (1, 2),
     *       i -> i + 1,                   (2,  [3])]                 (2, 3)]
     *       Integer::sum
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform, group and reduce
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduce the values related with same key
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceCollection}, collecting the results and reduce them using provided {@code reduceValues}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey}, {@code valueMapper} or {@code reduceValues}
     *                                  are {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, K, V> Map<K, V> groupMapReduce(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, V> valueMapper,
                                                     final BinaryOperator<V> reduceValues) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(discriminatorKey, "discriminatorKey must be not null");
        AssertUtil.notNull(valueMapper, "valueMapper must be not null");
        AssertUtil.notNull(reduceValues, "reduceValues must be not null");
        return groupMapReduce(
                sourceCollection,
                PartialFunction.of(
                        alwaysTrue(),
                        discriminatorKey,
                        valueMapper
                ),
                reduceValues
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} as values, according to {@code partialFunction}.
     * If the current element verifies {@link PartialFunction#isDefinedAt(Object)}, all the values that have the same key
     * after applying {@link PartialFunction#apply(Object)} are then reduced into a single value with {@code reduceValues}.
     *
     * <pre>
     *    groupMapReduce(                                  Intermediate Map:           Result:
     *       [1, 2, 3, 6, 7, 11, 12],                       [(0,  [4, 7])               [(0, 11),
     *       PartialFunction.of(                             (1,  [2, 8])                (1, 10),
     *          i -> null != i && 10 > i,                    (2,  [3])]                  (2, 3)]
     *          i -> null == i
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 i % 3,
     *                 i + 1
     *               )
     *       ),
     *       Integer::sum
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter, transform, group and reduce
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduce the values related with same key
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceCollection}
     *         on which it is defined, collecting the results and reduce them using provided {@code reduceValues}
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code reduceValues} are {@code null} with a not
     *                                  empty {@code sourceCollection}
     */
    public static <T, K, V> Map<K, V> groupMapReduce(final Collection<? extends T> sourceCollection,
                                                     final PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                     final BinaryOperator<V> reduceValues) {
        if (isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");
        AssertUtil.notNull(reduceValues, "reduceValues must be not null");
        Map<K, V> result = new HashMap<>();
        groupMap(
                sourceCollection,
                partialFunction
        )
        .forEach(
                (k, v) ->
                        result.put(
                                k,
                                v.stream().reduce(reduceValues).orElse(null)
                        )
        );
        return result;
    }


    /**
     *    Returns {@code true} if the supplied {@link Collection} is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param sourceCollection
     *    The {@link Collection} to check
     *
     * @return whether the given {@link Collection} is empty
     */
    public static boolean isEmpty(final Collection<?> sourceCollection) {
        return null == sourceCollection || sourceCollection.isEmpty();
    }


    /**
     *    Using {@code initialValue} as first element, apply {@code applyFunction} up to {@code untilPredicate} function
     * is {@code true}. The accumulated results are returned in a {@link List}.
     *
     * <pre>
     *    iterate(                       Result:
     *       42,                          []
     *       a -> (int) a / 10,
     *       a -> 50 >= a
     *    )
     *    iterate(                       Result:
     *       42,                          [42, 4]
     *       a -> (int) a / 10,
     *       a -> 0 >= a
     *    )
     * </pre>
     *
     * @param initialValue
     *    The initial value to start with
     * @param applyFunction
     *    {@link UnaryOperator} to apply initially to {@code initialValue} and then next results
     * @param untilPredicate
     *    {@link Predicate} to know when to stop apply {@code applyFunction}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code initialValue} or {@code untilPredicate} are {@code null}
     */
    public static <T> List<T> iterate(final T initialValue,
                                      final UnaryOperator<T> applyFunction,
                                      final Predicate<? super T> untilPredicate) {
        AssertUtil.notNull(initialValue, "initialValue must be not null");
        AssertUtil.notNull(untilPredicate, "untilPredicate must be not null");
        return ofNullable(applyFunction)
                .map(af -> {
                    List<T> result = new ArrayList<>();
                    T currentValue = initialValue;
                    while (!untilPredicate.test(currentValue)) {
                        result.add(currentValue);
                        currentValue = applyFunction.apply(currentValue);
                    }
                    return result;
                })
                .orElseGet(() ->
                        CollectionUtil.toList(
                                initialValue
                        )
                );
    }


    /**
     * Returns a {@link Collection} applying provided {@code mapFunction} to every element of the given {@code sourceCollection}.
     *
     * <pre>
     *    map(                                                                      Result:
     *       [new PizzaDto("Carbonara", 5D), new PizzaDto("Margherita", 10D)]        ["Carbonara", "Margherita"]
     *       PizzaDto::getName,
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} to used as source of the new one
     * @param mapFunction
     *    {@link Function} to apply to {@code sourceCollection}'s elements
     *
     * @return {@link List} after applying {@code mapFunction} to {@code sourceCollection}'s elements
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} and {@code sourceCollection} is not empty.
     */
    @SuppressWarnings("unchecked")
    public static <T, E> List<E> map(final Collection<? extends T> sourceCollection,
                                     final Function<? super T, ? extends E> mapFunction) {
        return (List<E>) map(
                sourceCollection,
                mapFunction,
                ArrayList::new
        );
    }


    /**
     * Returns a {@link Collection} applying provided {@code mapFunction} to every element of the given {@code sourceCollection}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    map(                                                                      Result:
     *       [new PizzaDto("Carbonara", 5D), new PizzaDto("Margherita", 10D)]        ["Carbonara", "Margherita"]
     *       PizzaDto::getName,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} to used as source of the new one
     * @param mapFunction
     *    {@link Function} to apply to {@code sourceCollection}'s elements
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection} after applying {@code mapFunction} to {@code sourceCollection}'s elements
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} and {@code sourceCollection} is not empty.
     */
    public static <T, E> Collection<E> map(final Collection<? extends T> sourceCollection,
                                           final Function<? super T, ? extends E> mapFunction,
                                           final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(mapFunction, "mapFunction must be not null");
        return sourceCollection.stream()
                .map(mapFunction)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Returns a {@link List} of {@link Tuple} applying provided {@code mapFunction}s to every element of the given
     * {@code sourceCollection}.
     *
     * <pre>
     *    mapMulti(                                                                              Result:
     *       [new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05")],         [Tuple2.of("user1 name", 11)]
     *       [UserDto::getName, UserDto::getAge]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} to used as source of the new one
     * @param mapFunctions
     *    Array of {@link Function} to apply to {@code sourceCollection}'s elements
     *
     * @return {@link List} of {@link Tuple} after applying {@code mapFunction}s to {@code sourceCollection}'s elements
     *
     * @throws IllegalArgumentException if {@code mapFunctions} is {@code null} and {@code sourceCollection} is not empty
     *                                  or {@code mapFunctions}'s length > {@link Tuple#MAX_ALLOWED_TUPLE_ARITY}
     */
    @SafeVarargs
    public static <T> List<Tuple> mapMulti(final Collection<? extends T> sourceCollection,
                                           final Function<? super T, ?> ...mapFunctions) {
        return (List<Tuple>) mapMulti(
                sourceCollection,
                ArrayList::new,
                mapFunctions
        );
    }


    /**
     *    Returns a {@link Collection} of {@link Tuple} applying provided {@code mapFunction}s to every element of the given
     * {@code sourceCollection}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    mapMulti(                                                                              Result:
     *       [new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05")],         [Tuple2.of("user1 name", 11)]
     *       ArrayList::new,
     *       [UserDto::getName, UserDto::getAge]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} to used as source of the new one
     * @param mapFunctions
     *    Array of {@link Function} to apply to {@code sourceCollection}'s elements
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection} of {@link Tuple} after applying {@code mapFunction}s to {@code sourceCollection}'s elements
     *
     * @throws IllegalArgumentException if {@code mapFunctions} is {@code null} and {@code sourceCollection} is not empty
     *                                  or {@code mapFunctions}'s length > {@link Tuple#MAX_ALLOWED_TUPLE_ARITY}
     */
    @SafeVarargs
    public static <T> Collection<Tuple> mapMulti(final Collection<? extends T> sourceCollection,
                                                 final Supplier<Collection<Tuple>> collectionFactory,
                                                 final Function<? super T, ?> ...mapFunctions) {
        final Supplier<Collection<Tuple>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(mapFunctions, "mapFunction must be not null");
        AssertUtil.isTrue(
                Tuple.MAX_ALLOWED_TUPLE_ARITY >= mapFunctions.length,
                format("The length of mapFunctions should be <= %d",
                        Tuple.MAX_ALLOWED_TUPLE_ARITY
                )
        );
        return sourceCollection.stream()
                .map(elto -> {
                    Tuple result = Tuple.empty();
                    for (Function<? super T, ?> mapper: mapFunctions) {
                        result = result.globalAppend(
                                mapper.apply(elto)
                        );
                    }
                    return result;
                })
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Finds the largest element of the given {@code sourceCollection}. To avoid {@link NullPointerException},
     * {@link Comparable} implementation required in the type T, will be overwritten by:
     *
     *       <pre>
     *          Comparator.nullsFirst(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the smallest ones in the returned {@link Optional}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          max(
     *             sourceCollection,
     *             comparator          // Comparator.naturalOrder() uses Comparable definition provided by T class
     *          )
     *       </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to get its largest element.
     *
     * @return {@link Optional} with largest value using developed {@link Comparable},
     *         {@link Optional#empty()} if {@code sourceCollection} has no elements or its largest value is {@code null}.
     */
    public static <T extends Comparable<? super T>> Optional<T> max(final Collection<? extends T> sourceCollection) {
        return max(
                sourceCollection,
                safeNaturalOrderNullFirst()
        );
    }


    /**
     * Finds the first element of the given {@code sourceCollection} which yields the largest value measured by {@code comparator}.
     *
     * <pre>
     *    max(                                        Result:
     *       [1, 2, 1, 3],                             Optional(1)
     *       Comparator.reverseOrder()
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to get its largest element
     * @param comparator
     *    {@link Comparator} to be used for comparing values
     *
     * @return {@link Optional} with largest value using given {@link Comparator},
     *         {@link Optional#empty()} if {@code sourceCollection} has no elements or its largest value is {@code null}.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code sourceCollection} has elements
     */
    public static <T> Optional<T> max(final Collection<? extends T> sourceCollection,
                                      final Comparator<? super T> comparator) {
        if (isEmpty(sourceCollection)) {
            return empty();
        }
        AssertUtil.notNull(comparator, "comparator must be not null");
        return ofNullable(
                Collections.max(
                        sourceCollection,
                        comparator
                )
        );
    }


    /**
     *    Finds the smallest element of the given {@code sourceCollection}. To avoid {@link NullPointerException},
     * {@link Comparable} implementation required in the type T, will be overwritten by:
     *
     *       <pre>
     *          Comparator.nullsLast(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the smallest ones in the returned {@link Optional}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          min(
     *             sourceCollection,
     *             comparator          // Comparator.naturalOrder() uses Comparable definition provided by T class
     *          )
     *       </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to get its smallest element.
     *
     * @return {@link Optional} with smallest value using developed {@link Comparable},
     *         {@link Optional#empty()} if {@code sourceCollection} has no elements or its smallest value is {@code null}.
     */
    public static <T extends Comparable<? super T>> Optional<T> min(final Collection<? extends T> sourceCollection) {
        return min(
                sourceCollection,
                safeNaturalOrderNullLast()
        );
    }


    /**
     * Finds the first element of the given {@code sourceCollection} which yields the smallest value measured by {@code comparator}.
     *
     * <pre>
     *    min(                                        Result:
     *       [1, 2, 1, 3],                             Optional(3)
     *       Comparator.reverseOrder()
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to get its smallest element
     * @param comparator
     *    {@link Comparator} to be used for comparing values
     *
     * @return {@link Optional} with smallest value using provided {@link Comparator},
     *         {@link Optional#empty()} if {@code sourceCollection} has no elements or its smallest value is {@code null}.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code sourceCollection} has elements
     */
    public static <T> Optional<T> min(final Collection<? extends T> sourceCollection,
                                      final Comparator<? super T> comparator) {
        if (isEmpty(sourceCollection)) {
            return empty();
        }
        AssertUtil.notNull(comparator, "comparator must be not null");
        return ofNullable(
                Collections.min(
                        sourceCollection,
                        comparator
                )
        );
    }


    /**
     *    Returns an {@link List} used to loop through given {@code sourceCollection} in reverse order. When provided
     * {@link Collection} does not provide any kind of internal ordination, the returned {@link List} could return
     * same values with but in different positions every time.
     *
     * @param sourceCollection
     *    {@link Collection} to get elements in reverse order.
     *
     * @return new {@link List} with the elements included in {@code sourceCollection} in reverse order
     */
    public static <T> List<T> reverseList(final Collection<? extends T> sourceCollection) {
        return ofNullable(sourceCollection)
                .map(CollectionUtil::getCollectionKeepingInternalOrdination)
                .map(sc -> new ArrayList<T>(sc))
                .map(sl -> {
                    Collections.reverse(sl);
                    return sl;
                })
                .orElseGet(ArrayList::new);
    }


    /**
     * Gets the size of {@code sourceCollection} in a safe way, that is, managing when it is {@code null}.
     *
     * @param sourceCollection
     *    {@link Collection} to get the size. It can be {@code null}
     *
     * @return {@link Collection#size()} when {@code sourceCollection} is not {@code null},
     *         0 otherwise
     */
    public static <T> int size(final Collection<? extends T> sourceCollection) {
        return ofNullable(sourceCollection)
                .map(Collection::size)
                .orElse(0);
    }


    /**
     *    Using the provided {@code sourceCollection}, return all elements beginning at index {@code from} and afterward,
     * up to index {@code until} (excluding this one).
     *
     * <pre>
     *    slice(                         Result:
     *       [5, 7, 9, 6],                [7, 9]
     *       1,
     *       3
     *    )
     *    slice(                         Result:
     *       [a, b, c, d],                [d]
     *       3,
     *       7
     *    )
     *    slice(                         Result:
     *       [a, b, c, d],                [a, b]
     *       -1,
     *       2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to slice
     * @param from
     *    Lower limit of the chunk to extract from provided {@link Collection} (starting from {@code 0})
     * @param until
     *    Upper limit of the chunk to extract from provided {@link Collection} (up to {@link Collection#size()})
     *
     * @return {@link List} containing the elements greater than or equal to index {@code from} extending up to (but not including)
     *         index {@code until} of this {@code sourceCollection}.
     *
     * @throws IllegalArgumentException if {@code from} is greater than {@code until} or {@code zero}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> slice(final Collection<? extends T> sourceCollection,
                                    final int from,
                                    final int until) {
        AssertUtil.isTrue(0 <= from, "from cannot be a negative value");
        AssertUtil.isTrue(
                from < until,
                format("from: %d must be lower than to: %d",
                        from,
                        until
                )
        );
        if (isEmpty(sourceCollection) || from > sourceCollection.size() - 1) {
            return new ArrayList<>();
        }
        final int finalUntil = Math.min(sourceCollection.size(), until);
        if (sourceCollection instanceof List) {
            return ((List<T>) sourceCollection).subList(
                    from,
                    finalUntil
            );
        }

        int i = 0;
        List<T> result = new ArrayList<>(
                Math.max(
                        finalUntil - from,
                        finalUntil - from - 1
                )
        );
        for (T element: getCollectionKeepingInternalOrdination(sourceCollection)) {
            if (i >= finalUntil) {
                break;
            }
            if (i >= from) {
                result.add(element);
            }
            i++;
        }
        return result;
    }


    /**
     * Loops through the provided {@link Collection} one position every time, returning sublists with {@code size}
     *
     * <pre>
     *    sliding(                       Result:
     *       [1, 2],                      [[1, 2]]
     *       5
     *    )
     *    sliding(                       Result:
     *       [7, 8, 9],                   [[7, 8], [8, 9]]
     *       2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T> List<List<T>> sliding(final Collection<? extends T> sourceCollection,
                                            final int size) {
        AssertUtil.isTrue(0 <= size, "size must be a positive value");
        if (isEmpty(sourceCollection) || 0 == size) {
            return new ArrayList<>();
        }
        final List<T> listToSlide = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        if (size >= listToSlide.size()) {
            return CollectionUtil.toList(
                    listToSlide
            );
        }
        return IntStream.range(0, listToSlide.size() - size + 1)
                .mapToObj(start ->
                        listToSlide.subList(
                                start,
                                start + size
                        )
                )
                .collect(
                        Collectors.toList()
                );
    }


    /**
     *    Merges provided {@link Collection}s {@code collections} into a single sorted {@link List} such that the natural
     * ordering ({@code null} values first) of the elements is retained.
     * <p>
     *    To avoid {@link NullPointerException}, {@link Comparable} implementation required in the type T, will be overwritten
     * by:
     *
     *       <pre>
     *          Comparator.nullsFirst(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the smallest ones in the returned sorted {@link List}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          sort(
     *             Comparator.naturalOrder(),    // Uses Comparable definition provided by T class
     *             collections
     *          )
     *       </pre>
     *
     * <pre>
     *    sort(                          Result:
     *       [1, 2],                      [1, 1, 2, 4]
     *       [1, 4]
     *    )
     *    sort(                          Result:
     *       [1, 2, null],                [null, null, 1, 1, 2]
     *       [1, null]
     *    )
     * </pre>
     *
     * @param collections
     *    {@link Collection} to merge
     *
     * @return new sorted {@link List}, containing the elements of {@code collections}
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> List<T> sort(final Collection<? extends T> ...collections) {
        return (List<T>) sort(
                safeNaturalOrderNullFirst(),
                ArrayList::new,
                collections
        );
    }


    /**
     *    Merges provided {@link Collection}s {@code collections} into a single sorted {@link Collection} such that the
     * natural ordering ({@code null} values first) of the elements is retained.
     * <p>
     *    To avoid {@link NullPointerException}, {@link Comparable} implementation required in the type T, will be overwritten
     * by:
     *
     *       <pre>
     *          Comparator.nullsFirst(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the smallest ones in the returned sorted {@link List}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          sort(
     *             Comparator.naturalOrder(),    // Uses Comparable definition provided by T class
     *             collectionFactory,
     *             collections
     *          )
     *       </pre>
     *
     * <pre>
     *    sort(                          Result:
     *       HashSet::new,                [1, 2, 4]
     *       [1, 2],
     *       [1, 4]
     *    )
     *    sort(                          Result:
     *       HashSet::new,                [null, 1, 2]
     *       [1, 2, null],
     *       [1, null]
     *    )
     * </pre>
     *
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     * @param collections
     *    {@link Collection} to merge
     *
     * @return new sorted {@link Collection}, containing the elements of {@code collections}
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> Collection<T> sort(final Supplier<Collection<T>> collectionFactory,
                                                                       final Collection<? extends T> ...collections) {
        return sort(
                safeNaturalOrderNullFirst(),
                collectionFactory,
                collections
        );
    }


    /**
     *    Merges provided {@link Collection}s {@code collections} into a single sorted {@link List} such that the ordering
     * of the elements according to {@link Comparator} {@code comparator} is retained.
     *
     * <pre>
     *    sort(                                       Result:
     *       Comparator.reverseOrder(),                [4, 2, 1, 1]
     *       [1, 2],
     *       [1, 4]
     *    )
     * </pre>
     *
     * @param comparator
     *    {@link Comparator} to use for the merge
     * @param collections
     *    {@link Collection} to merge
     *
     * @return new sorted {@link List}, containing the elements of {@code collections}
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code collections} has elements
     */
    @SafeVarargs
    public static <T> List<T> sort(final Comparator<? super T> comparator,
                                   final Collection<? extends T> ...collections) {
        return (List<T>) sort(
                comparator,
                ArrayList::new,
                collections
        );
    }


    /**
     *    Merges provided {@link Collection}s {@code collections} into a single sorted {@link Collection} such that the
     * ordering of the elements according to {@link Comparator} {@code comparator} is retained.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    sort(                                       Result:
     *       Comparator.reverseOrder(),                [4, 2, 1]
     *       HashSet::new,
     *       [1, 2],
     *       [1, 4]
     *    )
     * </pre>
     *
     * @param comparator
     *    {@link Comparator} to use for the merge
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     * @param collections
     *    {@link Collection} to merge
     *
     * @return new sorted {@link Collection}, containing the elements of {@code collections}
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code collections} has elements
     */
    @SafeVarargs
    public static <T> Collection<T> sort(final Comparator<? super T> comparator,
                                         final Supplier<Collection<T>> collectionFactory,
                                         final Collection<? extends T> ...collections) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        if (ObjectUtil.isEmpty(collections)) {
            return finalCollectionFactory.get();
        }
        AssertUtil.notNull(comparator, "comparator must be not null");
        return concat(collections)
                .stream()
                .sorted(comparator)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     * Splits the given {@link Collection} in sublists with a size equal to the given {@code size}
     *
     * <pre>
     *    split(                         Result:
     *       [1, 2, 3, 4],                [[1, 2], [3, 4]]
     *       2
     *    )
     *    split(                         Result:
     *       [1, 2, 3, 4],                [[1, 2, 3], [4]]
     *       3
     *    )
     *    split(                         Result:
     *       [1, 2, 3, 4],                [[1, 2, 3, 4]]
     *       5
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to split
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T> List<List<T>> split(final Collection<? extends T> sourceCollection,
                                          final int size) {
        AssertUtil.isTrue(0 <= size, "size must be a positive value");
        if (isEmpty(sourceCollection) || 0 == size) {
            return new ArrayList<>();
        }
        final List<T> listToSplit = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        final int expectedSize = 0 == listToSplit.size() % size
                ? listToSplit.size() / size
                : (listToSplit.size() / size) + 1;

        List<List<T>> splits = new ArrayList<>(expectedSize);
        for (int i = 0; i < listToSplit.size(); i += size) {
            splits.add(
                    new ArrayList<>(
                            listToSplit.subList(
                                    i,
                                    Math.min(
                                            listToSplit.size(),
                                            i + size
                                    )
                            )
                    )
            );
        }
        return splits;
    }


    /**
     *    Returns a {@link List} with the longest prefix of elements included in {@code sourceCollection} that satisfy
     * the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    takeWhile(                     Result:
     *       [1, 3, 4, 5, 6],             [1, 3]
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return the longest prefix of provided {@code sourceCollection} whose elements all satisfy {@code filterPredicate}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> takeWhile(final Collection<? extends T> sourceCollection,
                                        final Predicate<? super T> filterPredicate) {
        return (List<T>) takeWhile(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link List} with the longest prefix of elements included in {@code sourceCollection} that satisfy
     * the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned. If
     * {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    takeWhile(                     Result:
     *       [1, 3, 4, 5, 6],             [1, 3]
     *       i -> 1 == i % 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return the longest prefix of provided {@code sourceCollection} whose elements all satisfy {@code filterPredicate}
     */
    public static <T> Collection<T> takeWhile(final Collection<? extends T> sourceCollection,
                                              final Predicate<? super T> filterPredicate,
                                              final Supplier<Collection<T>> collectionFactory) {
        if (isEmpty(sourceCollection) || null == filterPredicate) {
            return copy(
                    sourceCollection,
                    collectionFactory
            );
        }
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        return sourceCollection
                .stream()
                .takeWhile(filterPredicate)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
                );
    }


    /**
     * Returns a {@link Collection} containing all the given {@code elements}.
     *
     * @apiNote
     *   If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    toCollection(                  Result:
     *       LinkedHashSet::new,          [2, 1]
     *       2,
     *       1,
     *       2
     *    )
     * </pre>
     *
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     * @param elements
     *    Items to include in the returned {@link Collection}
     *
     * @return {@link Collection} which contains all the input {@code elements} in encounter order
     */
    @SafeVarargs
    public static <T> Collection<T> toCollection(final Supplier<Collection<T>> collectionFactory,
                                                 final T... elements) {
        return toCollection(
                collectionFactory,
                null,
                elements
        );
    }


    /**
     *    Returns a {@link Collection} containing the given {@code elements} if the current item verifies
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    toCollection(                  Result:
     *       HashSet::new,                [1]
     *       i -> 1 == i % 2,
     *       2,
     *       1,
     *       1
     *    )
     * </pre>
     *
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     * @param filterPredicate
     *    {@link Predicate} to filter {@code elements}
     * @param elements
     *    Items to include in the returned {@link Collection}
     *
     * @return {@link Collection} which contains all the input {@code elements} that verify {@code filterPredicate}
     */
    @SafeVarargs
    public static <T> Collection<T> toCollection(final Supplier<Collection<T>> collectionFactory,
                                                 final Predicate<? super T> filterPredicate,
                                                 final T... elements) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrDefaultListSupplier(collectionFactory);
        return ofNullable(elements)
                .map(e -> {
                    final Stream<T> elementsStream = Objects.isNull(filterPredicate)
                            ? Arrays.stream(e)
                            : Arrays.stream(e).filter(filterPredicate);

                    return elementsStream
                            .collect(
                                    Collectors.toCollection(finalCollectionFactory)
                            );
                })
                .orElseGet(finalCollectionFactory);
    }
    

    /**
     * Returns a mutable and non-fixed size {@link List} containing all the given {@code elements}.
     *
     * @param elements
     *    Items to include in the returned {@link List}
     *
     * @return {@link List} which contains all the input {@code elements} in encounter order
     */
    @SafeVarargs
    public static <T> List<T> toList(final T... elements) {
        return toList(
                ArrayList::new,
                null,
                elements
        );
    }


    /**
     *    Returns a mutable and non-fixed size {@link List} containing the given {@code elements} if the current item
     * verifies {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then no one element will be filtered to insert in the returned {@link List}.
     *
     * <pre>
     *    toList(                        Result:
     *       i -> 1 == i % 2,             [1, 1]
     *       2,
     *       1,
     *       1
     *    )
     * </pre>
     *
     * @param filterPredicate
     *    {@link Predicate} to filter {@code elements}
     * @param elements
     *    Items to include in the returned {@link List}
     *
     * @return {@link List} which contains all the input {@code elements} that verify {@code filterPredicate}
     */
    @SafeVarargs
    public static <T> List<T> toList(final Predicate<? super T> filterPredicate,
                                     final T... elements) {
        return toList(
                ArrayList::new,
                filterPredicate,
                elements
        );
    }


    /**
     *    Returns a {@link List} containing the given {@code elements} if the current item verifies
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link ArrayList} will be used.
     *
     * <pre>
     *    toList(                        Result:
     *       ArrayList::new,              [1, 1]
     *       i -> 1 == i % 2,
     *       2,
     *       1,
     *       1
     *    )
     * </pre>
     *
     * @param listFactory
     *   {@link Supplier} of the {@link List} used to store the returned elements.
     * @param filterPredicate
     *    {@link Predicate} to filter {@code elements}
     * @param elements
     *    Items to include in the returned {@link List}
     *
     * @return {@link List} which contains all the input {@code elements} that verify {@code filterPredicate}
     */
    @SafeVarargs
    public static <T> List<T> toList(final Supplier<List<T>> listFactory,
                                     final Predicate<? super T> filterPredicate,
                                     final T... elements) {
        final Supplier<List<T>> finalListFactory = getOrElse(
                listFactory,
                ArrayList::new
        );
        return ofNullable(elements)
                .map(e -> {
                    final Stream<T> elementsStream = null == filterPredicate
                            ? Arrays.stream(e)
                            : Arrays.stream(e).filter(filterPredicate);

                    return elementsStream
                            .collect(
                                    Collectors.toCollection(finalListFactory)
                            );
                })
                .orElseGet(finalListFactory);
    }


    /**
     *    Converts the given {@link Collection} to a {@link Map} using provided {@code keyMapper} and {@link Function#identity()}
     * as values of returned {@link Map}.
     *
     * <pre>
     *    toMap(                         Result:
     *       [1, 2, 3, 6],                [("1", 1), ("2", 2), ("3", 3), ("6", 6)]
     *       Object::toString
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with the elements to transform and include in the returned {@link Map}
     * @param keyMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into keys of the returned {@link Map}
     *
     * @return new {@link Map} using provided {@code keyMapper} to create included keys
     *
     * @throws IllegalArgumentException if {@code sourceCollection} is not empty and {@code keyMapper} is {@code null}
     */
    public static <T, K> Map<K, T> toMap(final Collection<? extends T> sourceCollection,
                                         final Function<? super T, ? extends K> keyMapper) {
        return toMap(
                sourceCollection,
                keyMapper,
                Function.identity(),
                alwaysTrue(),
                resolveWithNew(),
                HashMap::new
        );
    }


    /**
     * Converts the given {@link Collection} to a {@link Map} using provided {@code keyMapper} and {@code valueMapper}.
     *
     * <pre>
     *    toMap(                         Result:
     *       [1, 2, 3, 6],                [("1", 2), ("2", 3), ("3", 4), ("6", 7)]
     *       Object::toString,
     *       i -> i + 1
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with the elements to transform and include in the returned {@link Map}
     * @param keyMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into keys of the returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into values of the returned {@link Map}
     *
     * @return new {@link Map} using provided {@code keyMapper} and {@code valueMapper} to create included keys and values
     *
     * @throws IllegalArgumentException if {@code sourceCollection} is not empty and {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<? extends T> sourceCollection,
                                            final Function<? super T, ? extends K> keyMapper,
                                            final Function<? super T, ? extends V> valueMapper) {
        return toMap(
                sourceCollection,
                keyMapper,
                valueMapper,
                alwaysTrue(),
                resolveWithNew(),
                HashMap::new
        );
    }


    /**
     *    Converts the given {@link Collection} to a {@link Map} using provided {@code keyMapper} and {@code valueMapper},
     * only with the elements that satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * <pre>
     *    toMap(                         Result:
     *       [1, 2, 3, 6],                [("1", 2), ("3", 4)]
     *       Object::toString,
     *       i -> i + 1,
     *       i -> 1 == i % 2
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with the elements to transform and include in the returned {@link Map}
     * @param keyMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into keys of the returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into values of the returned {@link Map}
     * @param filterPredicate
     *    {@link Predicate} used to filter values from {@code sourceCollection} that will be added in the returned {@link Map}
     *
     * @return new {@link Map} using provided {@code keyMapper} and {@code valueMapper} to create included keys and values
     *
     * @throws IllegalArgumentException if {@code sourceCollection} is not empty and {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<? extends T> sourceCollection,
                                            final Function<? super T, ? extends K> keyMapper,
                                            final Function<? super T, ? extends V> valueMapper,
                                            final Predicate<? super T> filterPredicate) {
        return toMap(
                sourceCollection,
                keyMapper,
                valueMapper,
                filterPredicate,
                resolveWithNew(),
                HashMap::new
        );
    }


    /**
     *    Converts the given {@link Collection} to a {@link Map} using provided {@code keyMapper} and {@code valueMapper},
     * only with the elements that satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * <pre>
     *    toMap(                         Result:
     *       [1, 2, 3, 3],                [("1", 2), ("3", 4)]
     *       Object::toString,
     *       i -> i + 1,
     *       i -> 1 == i % 2,
     *       (old, new) -> new,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with the elements to transform and include in the returned {@link Map}
     * @param keyMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into keys of the returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection} into values of the returned {@link Map}
     * @param filterPredicate
     *    {@link Predicate} used to filter values from {@code sourceCollection} that will be added in the returned {@link Map}
     * @param mergeValueFunction
     *    {@link BinaryOperator} used to resolve collisions between values associated with the same key. If no one is
     *    provided, by default last value will be used
     * @param mapFactory
     *      {@link Supplier} of the {@link Map} used to store the returned elements.
     *      If {@code null} then {@link HashMap}
     *
     * @return new {@link Map} using provided {@code keyMapper} and {@code valueMapper} to create included keys and values
     *
     * @throws IllegalArgumentException if {@code sourceCollection} is not empty and {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<? extends T> sourceCollection,
                                            final Function<? super T, ? extends K> keyMapper,
                                            final Function<? super T, ? extends V> valueMapper,
                                            final Predicate<? super T> filterPredicate,
                                            final BinaryOperator<V> mergeValueFunction,
                                            final Supplier<Map<K, V>> mapFactory) {
        final Supplier<Map<K, V>> finalMapFactory = getOrElse(
                mapFactory,
                HashMap::new
        );
        if (isEmpty(sourceCollection)) {
            return finalMapFactory.get();
        }
        return toMap(
                sourceCollection,
                PartialFunction.of(
                        getOrElse(
                                filterPredicate,
                                alwaysTrue()
                        ),
                        fromFunctionsToMapEntryFunction(
                                keyMapper,
                                valueMapper
                        )
                ),
                mergeValueFunction,
                mapFactory
        );
    }


    /**
     * Converts the given {@link Collection} to a {@link Map} using provided {@code partialFunction}.
     *
     * <pre>
     *    toMap(                                            Result:
     *       [1, 2, 3, 3],                                   [("1", 2), ("3", 4)]
     *       PartialFunction.of(
     *          i -> null != i && 1 == i % 2,
     *          i -> null == i
     *             ? new AbstractMap.SimpleEntry<>(
     *                 "",
     *                 0
     *               )
     *             : new AbstractMap.SimpleEntry<>(
     *                 i.toString(),
     *                 i + 1
     *               )
     *       ),
     *       (old, new) -> new,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} with the elements to transform and include in the returned {@link Map}
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCollection}
     * @param mergeValueFunction
     *    {@link BinaryOperator} used to resolve collisions between values associated with the same key. If no one is
     *    provided, by default last value will be used
     * @param mapFactory
     *      {@link Supplier} of the {@link Map} used to store the returned elements.
     *      If {@code null} then {@link HashMap}
     *
     * @return new {@link Map} using provided {@code partialFunction} to create included keys and values
     *
     * @throws IllegalArgumentException if {@code sourceCollection} is not empty and {@code partialFunction} is {@code null}
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<? extends T> sourceCollection,
                                            final PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                            final BinaryOperator<V> mergeValueFunction,
                                            final Supplier<Map<K, V>> mapFactory) {
        final Supplier<Map<K, V>> finalMapFactory = getOrElse(
                mapFactory,
                HashMap::new
        );
        if (isEmpty(sourceCollection)) {
            return finalMapFactory.get();
        }
        AssertUtil.notNull(partialFunction, "partialFunction must be not null");

        final BinaryOperator<V> finalMergeValueFunction = getOrElse(
                mergeValueFunction,
                resolveWithNew()
        );
        return sourceCollection
                .stream()
                .filter(partialFunction::isDefinedAt)
                .map(partialFunction)
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                finalMergeValueFunction
                        )
                );
    }


    /**
     * Returns a mutable and non-fixed size {@link Set} containing not duplicated given {@code elements}.
     *
     * @param elements
     *    Items to include in the returned {@link Set}
     *
     * @return {@link Set} which contains the unique input {@code elements}
     */
    @SafeVarargs
    public static <T> Set<T> toSet(final T... elements) {
        return toSet(
                LinkedHashSet::new,
                null,
                elements
        );
    }


    /**
     *    Returns a mutable and non-fixed size {@link Set} containing not duplicated given {@code elements} if the current
     * item verifies {@code filterPredicate}.
     *
     * @apiNote
     *   If {@code filterPredicate} is {@code null} then no one element will be filtered to insert in the returned {@link Set}.
     *
     * <pre>
     *    toSet(                         Result:
     *       i -> 1 == i % 2,             [1]
     *       2,
     *       1,
     *       1
     *    )
     * </pre>
     *
     * @param filterPredicate
     *    {@link Predicate} to filter {@code elements}
     * @param elements
     *    Items to include in the returned {@link Set}
     *
     * @return {@link Set} which contains the unique input {@code elements} that verify {@code filterPredicate}
     */
    @SafeVarargs
    public static <T> Set<T> toSet(final Predicate<? super T> filterPredicate,
                                   final T... elements) {
        return toSet(
                LinkedHashSet::new,
                filterPredicate,
                elements
        );
    }


    /**
     *    Returns a {@link Set} containing not duplicated given {@code elements} if the current item verifies
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then an {@link LinkedHashSet} will be used.
     *
     * <pre>
     *    toSet(                         Result:
     *       HashSet::new,                [1]
     *       i -> 1 == i % 2,
     *       2,
     *       1,
     *       1
     *    )
     * </pre>
     *
     * @param setFactory
     *   {@link Supplier} of the {@link Set} used to store the returned elements.
     * @param filterPredicate
     *    {@link Predicate} to filter {@code elements}
     * @param elements
     *    Items to include in the returned {@link Set}
     *
     * @return {@link Set} which contains the unique input {@code elements} that verify {@code filterPredicate}
     */
    @SafeVarargs
    public static <T> Set<T> toSet(final Supplier<Set<T>> setFactory,
                                   final Predicate<? super T> filterPredicate,
                                   final T... elements) {
        final Supplier<Set<T>> finalSetFactory = getOrElse(
                setFactory,
                LinkedHashSet::new
        );
        return ofNullable(elements)
                .map(e -> {
                    final Stream<T> elementsStream = null == filterPredicate
                            ? Arrays.stream(e)
                            : Arrays.stream(e).filter(filterPredicate);

                    return elementsStream
                            .collect(
                                    Collectors.toCollection(finalSetFactory)
                            );
                })
                .orElseGet(finalSetFactory);
    }


    /**
     * Transposes the rows and columns of the given {@code sourceCollection}.
     *
     * <pre>
     *    transpose(                                            Result:
     *       [[1, 2, 3], [4, 5, 6]]                              [[1, 4], [2, 5], [3, 6]]
     *    )
     *    transpose(                                            Result:
     *       [["a1", "a2"], ["b1", "b2"], ["c1", "c2"]]          [["a1", "b1", "c1"], ["a2", "b2", "c2"]]
     *    )
     *    transpose(                                            Result:
     *       [[1, 2], [0], [7, 8, 9]]                            [[1, 0, 7], [2, 8], [9]]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Collection}s to transpose
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> transpose(final Collection<? extends Collection<T>> sourceCollection) {
        if (isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        final int[] sizeOfLongestSubCollection = { -1 };
        final List<Iterator<T>> iteratorList = sourceCollection.stream()
                .filter(Objects::nonNull)
                .map(c -> {
                    if (sizeOfLongestSubCollection[0] < c.size()) {
                        sizeOfLongestSubCollection[0] = c.size();
                    }
                    return c.iterator();
                })
                .toList();

        List<List<T>> result = new ArrayList<>(sizeOfLongestSubCollection[0]);
        for (int i = 0; i < sizeOfLongestSubCollection[0]; i++) {
            List<T> newRow = new ArrayList<>(sourceCollection.size());
            for (Iterator<T> iterator: iteratorList) {
                if (iterator.hasNext()) {
                    newRow.add(iterator.next());
                }
            }
            result.add(newRow);
        }
        return result;
    }


    /**
     *    Converts given {@code sourceCollection} of {@link Tuple2} into two {@link List} of the first and
     * second half of each pair.
     *
     * <pre>
     *    unzip(                                      Result:
     *       [("d", 6), ("h", 7), ("y", 11)]           [("d", "h", "y"), (6, 7, 11)]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Tuple2} to split its elements
     *
     * @return {@link Tuple2} of two {@link List}
     */
    public static <T, E> Tuple2<List<T>, List<E>> unzip(final Collection<Tuple2<T, E>> sourceCollection) {
        return foldLeft(
                sourceCollection,
                Tuple.of(
                        new ArrayList<>(),
                        new ArrayList<>()
                ),
                (tupleOfLists, currentElto) -> {
                    tupleOfLists._1.add(currentElto._1);
                    tupleOfLists._2.add(currentElto._2);
                    return tupleOfLists;
                }
        );
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link Tuple2}. If one of the two collections is longer than
     * the other, its remaining elements are ignored.
     *
     * <pre>
     *    zip(                           Result:
     *       ["d", "h", "y"],             [("d", 6), ("h", 7), ("y", 11)]
     *       [6, 7, 11]
     *    )
     *    zip(                           Result:
     *       [4, 9, 14],                  [(4, 23), (9, 8)]
     *       [23, 8]
     *    )
     * </pre>
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link Tuple2}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link Tuple2}
     *
     * @return {@link List} of {@link Tuple2}
     */
    public static <T, E> List<Tuple2<T, E>> zip(final Collection<? extends T> sourceLeftCollection,
                                                final Collection<? extends E> sourceRightCollection) {
        if (isEmpty(sourceLeftCollection) ||
                isEmpty(sourceRightCollection)) {
            return new ArrayList<>();
        }
        final int minCollectionsSize = Math.min(
                sourceLeftCollection.size(),
                sourceRightCollection.size()
        );

        final Iterator<? extends T> leftIterator = sourceLeftCollection.iterator();
        final Iterator<? extends E> rightIterator = sourceRightCollection.iterator();
        List<Tuple2<T, E>> result = new ArrayList<>();
        for (int i = 0; i < minCollectionsSize; i++) {
            result.add(
                    Tuple.of(
                            leftIterator.next(),
                            rightIterator.next()
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link Tuple2}. If one of the two collections is shorter than
     * the other, placeholder elements are used to extend the shorter collection to the length of the longer.
     *
     * <pre>
     *    zipAll(                        Result:
     *       ["d", "h", "y"],             [("d", 6), ("h", 7), ("y", 11)]
     *       [6, 7, 11],
     *       "z",
     *       55
     *    )
     *    zipAll(                        Result:
     *       [4, 9, 14],                  [(4, 23), (9, 8), (14, 10)]
     *       [23, 8],
     *       17,
     *       10
     *    )
     *    zipAll(                        Result:
     *       [4, 9],                      [(4, "f"), (9, "g"), (11, "m")]
     *       ["f", "g", "m"],
     *       11,
     *       "u"
     *    )
     * </pre>
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link Tuple2}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link Tuple2}
     * @param defaultLeftElement
     *    Element to be used to fill up the result if {@code sourceLeftCollection} is shorter than {@code sourceRightCollection}
     * @param defaultRightElement
     *    Element to be used to fill up the result if {@code sourceRightCollection} is shorter than {@code sourceLeftCollection}
     *
     * @return {@link List} of {@link Tuple2}
     */
    public static <T, E> List<Tuple2<T, E>> zipAll(final Collection<T> sourceLeftCollection,
                                                   final Collection<E> sourceRightCollection,
                                                   final T defaultLeftElement,
                                                   final E defaultRightElement) {
        final int maxCollectionSize = Math.max(
                isEmpty(sourceLeftCollection)
                        ? 0
                        : sourceLeftCollection.size(),
                isEmpty(sourceRightCollection)
                        ? 0
                        : sourceRightCollection.size()
        );
        final Iterator<T> leftIterator = ofNullable(sourceLeftCollection)
                .map(Collection::iterator)
                .orElse(null);
        final Iterator<E> rightIterator = ofNullable(sourceRightCollection)
                .map(Collection::iterator)
                .orElse(null);

        List<Tuple2<T, E>> result = new ArrayList<>();
        for (int i = 0; i < maxCollectionSize; i++) {
            result.add(
                    Tuple.of(
                            ofNullable(leftIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElse(defaultLeftElement),
                            ofNullable(rightIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElse(defaultRightElement)
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} containing pairs consisting of all elements of {@code sourceCollection} paired with
     * their index. Indices start at {@code 0}.
     *
     * <pre>
     *    zipWithIndex(                  Result:
     *       ["d", "h", "y"]              [(0, "d"), (1, "h"), (2, "y")]
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} to extract: index and element
     *
     * @return {@link List} of {@link Tuple2}s
     */
    public static <T> List<Tuple2<Integer, T>> zipWithIndex(final Collection<? extends T> sourceCollection) {
        if (isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        int i = 0;
        List<Tuple2<Integer, T>> result = new ArrayList<>(sourceCollection.size());
        for (T element: sourceCollection) {
            result.add(
                    Tuple.of(
                            i,
                            element
                    )
            );
            i++;
        }
        return result;
    }


    /**
     *    Returns a {@link Collection} with the elements of the given {@code sourceCollection} considering special use
     * cases like {@link PriorityQueue}, on which internal {@link Iterator} does not take into account internal ordering.
     *
     * @param sourceCollection
     *    {@link Collection} to iterate.
     *
     * @return {@link Collection}
     */
    private static <T> Collection<T> getCollectionKeepingInternalOrdination(final Collection<T> sourceCollection) {
        if (sourceCollection instanceof PriorityQueue) {
            final PriorityQueue<T> cloneQueue = new PriorityQueue<>(sourceCollection);
            List<T> result = new ArrayList<>(sourceCollection.size());
            for (int i = 0; i < sourceCollection.size(); i++) {
                result.add(cloneQueue.poll());
            }
            return result;
        }
        else {
            return sourceCollection;
        }
    }

}
