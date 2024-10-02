package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Optional.empty;

@UtilityClass
public class EnumUtil {

    /**
     *    Using the given {@link Enum} {@link Class} {@code enumClass} containing internal properties, gets the
     * {@link Enum} constant which internal property value matches with {@code internalPropertyValueToSearch} after
     * applying {@code mapper}.
     *
     * <pre>
     *    public enum ExtendedHttpStatus {
     *      TOKEN_EXPIRED(440);
     *
     *      private final int value;
     *
     *      ExtendedHttpStatus(final int value) {
     *        this.value = value;
     *      }
     *
     *      public int getValue() {
     *        return this.value;
     *      }
     *    }
     *
     *
     *    getByInternalProperty(                      Result:
     *       ExtendedHttpStatus.class,                 Optional(TOKEN_EXPIRED)
     *       440,
     *       ExtendedHttpStatus::getValue
     *    )
     * </pre>
     *
     * @param enumClass
     *    {@link Class} of the {@link Enum} to query
     * @param internalPropertyValueToSearch
     *    Value of the internal property of {@link Enum} to search
     * @param mapper
     *    {@link Function} to apply the {@link Enum} constants to get their internal property to compare
     *
     * @return {@link Optional} containing the first {@link Enum} constant with an internal property that matches with
     *         {@code internalPropertyValueToSearch}, {@link Optional#empty()} otherwise
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and {@code enumClass} is not {@code null}
     */
    public static <T, E extends Enum<E>> Optional<E> getByInternalProperty(final Class<E> enumClass,
                                                                           final T internalPropertyValueToSearch,
                                                                           final Function<? super E, ? extends T> mapper) {
        if (null == enumClass) {
            return empty();
        }
        AssertUtil.notNull(mapper, "mapper must be not null");
        return Arrays.stream(
                        enumClass.getEnumConstants()
                )
                .filter(e ->
                        Objects.equals(
                                internalPropertyValueToSearch,
                                mapper.apply(e)
                        )
                )
                .findFirst();
    }


    /**
     *    Gets the {@link Enum} constant included in the given {@code enumClass} that matches with {@code enumName}.
     * This method performs case-insensitive matching of the name.
     *
     * @apiNote
     *    If {@code enumClass} or {@code enumClass} are {@code null} then an {@link Optional#empty()} will be returned.
     *
     * @param enumClass
     *    {@link Class} of the {@link Enum} to query
     * @param enumName
     *    {@link Enum#name()} to search
     *
     * @return {@link Optional} containing the first {@link Enum} constant with a name that matches with {@code enumName},
     *         {@link Optional#empty()} otherwise
     */
    public static <E extends Enum<E>> Optional<E> getByNameIgnoreCase(final Class<E> enumClass,
                                                                      final String enumName) {
        if (null == enumClass || null == enumName) {
            return empty();
        }
        return Arrays.stream(
                        enumClass.getEnumConstants()
                )
                .filter(e ->
                        enumName.equalsIgnoreCase(
                                e.name()
                        )
                )
                .findFirst();
    }


    /**
     *    Gets the {@link Enum} constant included in the given {@code enumClass} that matches with {@code enumName}.
     * This method performs case-insensitive matching of the name.
     *
     * @param enumClass
     *    {@link Class} of the {@link Enum} to query
     * @param enumName
     *    {@link Enum#name()} to search
     *
     * @return {@link Enum} constant containing the first {@link Enum#name()} that matches with {@code enumName}
     *
     * @throws IllegalArgumentException if {@code enumClass} or {@code enumName} are {@code null}, or there is no
     *                                  {@link Enum} constant with a name that matches with {@code enumName}
     */
    public static <E extends Enum<E>> E getByNameIgnoreCaseOrThrow(final Class<E> enumClass,
                                                                   final String enumName) {
        AssertUtil.notNull(enumClass, "enumClass must be not null");
        AssertUtil.notNull(enumName, "enumName must be not null");
        return getByNameIgnoreCase(
                enumClass,
                enumName
        )
        .orElseThrow(() ->
                new IllegalArgumentException(
                        format("No enum constant in %s that matches in a case-insensitive way with %s",
                                enumClass.getCanonicalName(),
                                enumName
                        )
                )
        );
    }

}
