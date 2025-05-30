package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.functional.either.Either;
import com.spring6microservices.common.core.functional.either.Left;
import com.spring6microservices.common.core.functional.either.Right;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.spring6microservices.common.core.functional.either.Either.left;
import static com.spring6microservices.common.core.functional.either.Either.right;
import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedRootError;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@UtilityClass
public class NumberUtil {

    /**
     *    Compares provided {@link BigDecimal}s taking into account the number of decimals included in the parameter
     * {@code numberOfDecimals}. {@link RoundingMode#HALF_UP} will be used in the comparison.
     *
     * <pre>
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), 4)  =  < 0
     *    compare(new BigDecimal("100.1242"), new BigDecimal("100.1241"), 4)  =  > 0
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), 3)  =  0
     * </pre>
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param numberOfDecimals
     *    Number of decimals used for comparison
     *
     * @return {@code one#compareTo(two)} using {@code numberOfDecimals} as precision.
     *
     * @throws IllegalArgumentException if {@code numberOfDecimals} is less than {@code zero}
     */
    public static int compare(final BigDecimal one,
                              final BigDecimal two,
                              final int numberOfDecimals) {
        return compare(
                one,
                two,
                numberOfDecimals,
                RoundingMode.HALF_UP
        );
    }


    /**
     *    Compares provided {@link BigDecimal}s taking into account the number of decimals included in the parameter
     * {@code numberOfDecimals}.
     *
     * <pre>
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), 4, RoundingMode.HALF_UP)  =  < 0
     *    compare(new BigDecimal("100.1242"), new BigDecimal("100.1241"), 4, RoundingMode.HALF_UP)  =  > 0
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), 3, RoundingMode.HALF_UP)  =  0
     * </pre>
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param numberOfDecimals
     *    Number of decimals used for comparison
     * @param roundingMode
     *    {@link RoundingMode} used in the comparison. {@link RoundingMode#HALF_UP} if {@code null}
     *
     * @return {@code one#compareTo(two)} using {@code numberOfDecimals} as precision.
     *
     * @throws IllegalArgumentException if {@code numberOfDecimals} is less than {@code zero}
     */
    public static int compare(final BigDecimal one,
                              final BigDecimal two,
                              final int numberOfDecimals,
                              final RoundingMode roundingMode) {
        AssertUtil.isTrue(
                0 <= numberOfDecimals,
                "numberOfDecimals must be equals or greater than 0"
        );
        if (null == one) {
            return null == two
                    ? 0
                    : -1;
        }
        if (null == two) {
            return 1;
        }
        final RoundingMode finalRoundingMode = getOrElse(
                roundingMode,
                RoundingMode.HALF_UP
        );
        final BigDecimal oneWithProvidedPrecision = one.setScale(
                numberOfDecimals,
                finalRoundingMode
        );
        final BigDecimal twoWithProvidedPrecision = two.setScale(
                numberOfDecimals,
                finalRoundingMode
        );
        return oneWithProvidedPrecision.compareTo(twoWithProvidedPrecision);
    }


    /**
     *  Compares provided {@link BigDecimal}s taking into account the given tolerance {@code epsilon}.
     *
     * <pre>
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), new BigDecimal(0.0001))  =  < 0
     *    compare(new BigDecimal("100.1242"), new BigDecimal("100.1241"), new BigDecimal(0.0001))  =  > 0
     *    compare(new BigDecimal("100.1241"), new BigDecimal("100.1242"), new BigDecimal(0.001))   =  0
     * </pre>
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param epsilon
     *    Tolerance used to know what is the maximum difference to consider {@code one} and {@code two} as equals values.
     *    {@link BigDecimal#ZERO} if {@code null}.
     *
     * @return {@code one#compareTo(two)} using {@code epsilon} as tolerance value.
     *
     * @throws IllegalArgumentException if {@code epsilon} is less than {@code zero}
     */
    public static int compare(final BigDecimal one,
                              final BigDecimal two,
                              final BigDecimal epsilon) {
        final BigDecimal finalEpsilon = getOrElse(
                epsilon,
                BigDecimal.ZERO
        );
        AssertUtil.isTrue(
                0 <= finalEpsilon.doubleValue(),
                "epsilon must be equals or greater than 0"
        );
        if (null == one) {
            return null == two
                    ? 0
                    : -1;
        }
        if (null == two) {
            return 1;
        }
        final BigDecimal diff = one.subtract(two);
        if (0 >= diff.abs().compareTo(finalEpsilon)) {
            return 0;
        }
        return 0 > diff.compareTo(finalEpsilon)
                ? -1
                : 1;
    }


    /**
     *    Returns an {@link Right} with {@link Integer} instance if is possible to do the conversion of given {@code potentialNumber}.
     * {@link Left} with the error message otherwise.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance.
     *
     * @return {@link Right} with {@link Optional} of {@link Integer} if provided {@code potentialNumber} could be converted,
     *         {@link Left} with the error message otherwise.
     */
    public static Either<String, Optional<Integer>> fromString(final String potentialNumber) {
        return fromString(
                potentialNumber,
                Integer.class
        );
    }


    /**
     *    Returns an {@link Right} with {@code clazzReturnedInstance} instance if is possible to do the conversion of given
     * {@code potentialNumber}. {@link Left} with the error message otherwise.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Number} instance.
     * @param clazzReturnedInstance
     *    {@link Number} subclass of the returned instance.
     *
     * @return {@link Right} with {@link Optional} of {@code clazzReturnedInstance} instance if provided {@code potentialNumber}
     *         could be converted. {@link Left} with the error message otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> Either<String, Optional<T>> fromString(final String potentialNumber,
                                                                            final Class<T> clazzReturnedInstance) {
        if (StringUtil.isBlank(potentialNumber)) {
            return right(
                    empty()
            );
        }
        final Class<T> finalClazzReturnedInstance = (Class<T>) getOrElse(
                clazzReturnedInstance,
                Integer.class
        );
        try {
            Constructor<T> ctor = finalClazzReturnedInstance.getConstructor(String.class);
            return right(
                    of(
                            ctor.newInstance(potentialNumber)
                    )
            );
        }
        catch (Exception e) {
            return left(
                    format("There was an error trying to convert the string: %s to an instance of: %s. %s",
                            potentialNumber,
                            finalClazzReturnedInstance.getName(),
                            getFormattedRootError(e)
                    )
            );
        }
    }


    /**
     * Converts a {@link String} to an {@link Byte}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Byte} instance
     *
     * @return {@link Byte} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Byte toByte(final String potentialNumber) {
        return toByte(
                potentialNumber,
                (byte) 0
        );
    }


    /**
     * Converts a {@link String} to an {@link Byte}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Byte} instance
     * @param defaultValue
     *    {@code byte} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Byte} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Byte toByte(final String potentialNumber,
                              final byte defaultValue) {
        return fromString(
                potentialNumber,
                Byte.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Double}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Double} instance
     *
     * @return {@link Double} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Double toDouble(final String potentialNumber) {
        return toDouble(
                potentialNumber,
                0.0d
        );
    }


    /**
     * Converts a {@link String} to an {@link Double}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Double} instance
     * @param defaultValue
     *    {@code double} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Double} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Double toDouble(final String potentialNumber,
                                  final double defaultValue) {
        return fromString(
                potentialNumber,
                Double.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Float}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Float} instance
     *
     * @return {@link Float} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Float toFloat(final String potentialNumber) {
        return toFloat(
                potentialNumber,
                0.0f
        );
    }


    /**
     * Converts a {@link String} to an {@link Float}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Float} instance
     * @param defaultValue
     *    {@code float} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Float} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Float toFloat(final String potentialNumber,
                                final float defaultValue) {
        return fromString(
                potentialNumber,
                Float.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Integer}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance
     *
     * @return {@link Integer} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Integer toInteger(final String potentialNumber) {
        return toInteger(
                potentialNumber,
                0
        );
    }


    /**
     * Converts a {@link String} to an {@link Integer}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance
     * @param defaultValue
     *    {@code int} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Integer} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Integer toInteger(final String potentialNumber,
                                    final int defaultValue) {
        return fromString(
                potentialNumber,
                Integer.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Long}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Long} instance
     *
     * @return {@link Long} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Long toLong(final String potentialNumber) {
        return toLong(
                potentialNumber,
                0
        );
    }


    /**
     * Converts a {@link String} to an {@link Long}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Long} instance
     * @param defaultValue
     *    {@code long} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Long} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Long toLong(final String potentialNumber,
                              final long defaultValue) {
        return fromString(
                potentialNumber,
                Long.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Short}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Short} instance
     *
     * @return {@link Short} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Short toShort(final String potentialNumber) {
        return toShort(
                potentialNumber,
                (short) 0
        );
    }


    /**
     * Converts a {@link String} to an {@link Short}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Short} instance
     * @param defaultValue
     *    {@code short} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Short} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Short toShort(final String potentialNumber,
                                final short defaultValue) {
        return fromString(
                potentialNumber,
                Short.class
        )
        .map(opt ->
                opt.orElse(defaultValue)
        )
        .getOrElse(defaultValue);
    }

}
