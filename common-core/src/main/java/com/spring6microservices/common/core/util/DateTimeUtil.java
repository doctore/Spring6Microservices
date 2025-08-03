package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.lang.Math.abs;

@UtilityClass
public class DateTimeUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


    /**
     * Compares provided {@link Date}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link Date} of the "left side" of compare method
     * @param two
     *    {@link Date} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link Date} values. If less than zero then 0 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     */
    public static int compare(final Date one,
                              final Date two,
                              final long epsilon,
                              final ChronoUnit timeUnit) {
        if (null == one) {
            return null == two
                    ? 0
                    : -1;
        }
        if (null == two) {
            return 1;
        }
        final long finalEpsilon = 0 < epsilon
                ? epsilon
                : 0L;
        if (0 == finalEpsilon) {
            return one.compareTo(two);
        }
        return compare(
                DateTimeUtil.fromDateToLocalDateTime(one),
                DateTimeUtil.fromDateToLocalDateTime(two),
                finalEpsilon,
                timeUnit
        );
    }


    /**
     * Compares provided {@link LocalDateTime}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link LocalDateTime} of the "left side" of compare method
     * @param two
     *    {@link LocalDateTime} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link LocalDateTime} values. If less than zero then 0 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     */
    public static int compare(final LocalDateTime one,
                              final LocalDateTime two,
                              final long epsilon,
                              final ChronoUnit timeUnit) {
        if (null == one) {
            return null == two
                    ? 0
                    : -1;
        }
        if (null == two) {
            return 1;
        }
        final long finalEpsilon = 0 < epsilon
                ? epsilon
                : 0L;
        if (0 == finalEpsilon) {
            return one.compareTo(two);
        }
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        long difference = finalTimeUnit.between(one, two);
        return abs(difference) <= finalEpsilon
                ? 0
                : 0 < difference ? -1 : 1;
    }


    /**
     * Formats a {@link Date} into a date-time string using {@link DateTimeUtil#DEFAULT_DATETIME_FORMAT}.
     *
     * @param sourceDate
     *    The time value to be formatted into a date-time string. If {@code null} then new {@link Date} will be used
     *
     * @return the formatted date-time string
     */
    public static String format(final Date sourceDate) {
        return format(
                sourceDate,
                DEFAULT_DATETIME_FORMAT
        );
    }


    /**
     * Formats a {@link Date} into a date-time string using provided {@code pattern}.
     *
     * @param sourceDate
     *    The time value to be formatted into a date-time string. If {@code null} then new {@link Date} will be used
     * @param pattern
     *    The pattern describing the date and time format
     *
     * @return the formatted date-time string
     *
     * @throws IllegalArgumentException if the given {@code pattern} is invalid
     */
    public static String format(final Date sourceDate,
                                final String pattern) {
        final Date finalSourceDate = getOrElse(
                sourceDate,
                new Date()
        );
        final String finalPatter = getOrElse(
                pattern,
                DEFAULT_DATETIME_FORMAT
        );
        return new SimpleDateFormat(finalPatter)
                .format(finalSourceDate);
    }


    /**
     * Formats a {@link TemporalAccessor} into a date-time string using {@link DateTimeUtil#DEFAULT_DATETIME_FORMAT}.
     *
     * @param sourceTemporal
     *    The time value to be formatted into a date-time string. If {@code null} then {@link LocalDateTime#now()} will be used
     *
     * @return the formatted date-time string
     */
    public static String format(final TemporalAccessor sourceTemporal) {
        return format(
                sourceTemporal,
                DEFAULT_DATETIME_FORMAT
        );
    }


    /**
     * Formats a {@link TemporalAccessor} into a date-time string using provided {@code pattern}.
     *
     * @param sourceTemporal
     *    The time value to be formatted into a date-time string. If {@code null} then new {@link Date} will be used
     * @param pattern
     *    The pattern describing the date and time format
     *
     * @return the formatted date-time string
     *
     * @throws IllegalArgumentException if the given {@code pattern} is invalid
     */
    public static String format(final TemporalAccessor sourceTemporal,
                                final String pattern) {
        final TemporalAccessor finalSourceTemporal = getOrElse(
                sourceTemporal,
                LocalDateTime.now()
        );
        final String finalPatter = getOrElse(
                pattern,
                DEFAULT_DATETIME_FORMAT
        );
        return DateTimeFormatter.ofPattern(finalPatter)
                .format(finalSourceTemporal);
    }


    /**
     * Converts to an instance of {@link LocalDate} the given {@link Date} using {@link ZoneId#systemDefault()}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     *
     * @return {@link LocalDate}
     */
    public static LocalDate fromDateToLocalDate(final Date sourceDate) {
        return fromDateToLocalDate(
                sourceDate,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link LocalDate} the given {@link Date} using the provided {@link ZoneId}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDate}
     */
    public static LocalDate fromDateToLocalDate(final Date sourceDate,
                                                final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final Date finalSourceDate = getOrElse(
                sourceDate,
                new Date()
        );
        return finalSourceDate.toInstant()
                .atZone(finalZoneId)
                .toLocalDate();
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using {@link ZoneId#systemDefault()}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime fromDateToLocalDateTime(final Date sourceDate) {
        return fromDateToLocalDateTime(
                sourceDate,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using the provided {@link ZoneId}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime fromDateToLocalDateTime(final Date sourceDate,
                                                        final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final Date finalSourceDate = getOrElse(
                sourceDate,
                new Date()
        );
        return finalSourceDate.toInstant()
                .atZone(finalZoneId)
                .toLocalDateTime();
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDate} using {@link ZoneId#systemDefault()}
     *
     * @param sourceLocalDate
     *    {@link LocalDate} value to convert. If {@code null} then {@link LocalDate#now()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateToDate(final LocalDate sourceLocalDate) {
        return fromLocalDateToDate(
                sourceLocalDate,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDate} using the provided {@link ZoneId}
     *
     * @param sourceLocalDate
     *    {@link LocalDate} value to convert. If {@code null} then {@link LocalDate#now()} with {@code zoneId} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateToDate(final LocalDate sourceLocalDate,
                                           final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDate finalSourceLocalDate = getOrElse(
                sourceLocalDate,
                LocalDate.now(finalZoneId)
        );
        return Date.from(
                finalSourceLocalDate
                        .atStartOfDay(finalZoneId)
                        .toInstant()
        );
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using {@link ZoneId#systemDefault()}
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value to convert. If {@code null} then {@link LocalDateTime#now()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateTimeToDate(final LocalDateTime sourceLocalDateTime) {
        return fromLocalDateTimeToDate(
                sourceLocalDateTime,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using the provided {@link ZoneId}
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value to convert. If {@code null} then {@link LocalDateTime#now()} with {@code zoneId} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateTimeToDate(final LocalDateTime sourceLocalDateTime,
                                               final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now(finalZoneId)
        );
        return Date.from(
                finalSourceLocalDateTime
                        .atZone(finalZoneId)
                        .toInstant()
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     *
     * <pre>
     *   1. [ sourceDate - difference, sourceDate ]                if difference is lower than 0.
     *   2. [ sourceDate,              sourceDate + difference ]   if difference is greater than 0
     * </pre>
     *
     * @param sourceDate
     *    {@link Date} value from which to add/subtract the specified {@code difference}. If {@code null} then new {@link Date} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<Date, Date> getDateIntervalFromGiven(final Date sourceDate,
                                                              final long difference,
                                                              final ChronoUnit timeUnit) {
        return getDateIntervalFromGiven(
                sourceDate,
                difference,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <pre>
     *   1. [ sourceDate - difference, sourceDate ]                if difference is lower than 0.
     *   2. [ sourceDate,              sourceDate + difference ]   if difference is greater than 0
     * </pre>
     *
     * @param sourceDate
     *    {@link Date} value from which to add/subtract the specified {@code difference}. If {@code null} then new {@link Date} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<Date, Date> getDateIntervalFromGiven(final Date sourceDate,
                                                              final long difference,
                                                              final ChronoUnit timeUnit,
                                                              final ZoneId zoneId) {
        Tuple2<LocalDateTime, LocalDateTime> localDateTimeInterval = getLocalDateTimeIntervalFromGiven(
                fromDateToLocalDateTime(
                        sourceDate,
                        zoneId
                ),
                difference,
                timeUnit,
                zoneId
        );
        return Tuple2.of(
                fromLocalDateTimeToDate(
                        localDateTimeInterval._1,
                        zoneId
                ),
                fromLocalDateTimeToDate(
                        localDateTimeInterval._2,
                        zoneId
                )
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <pre>
     *   1. [ sourceLocalDateTime - difference, sourceLocalDateTime ]                if difference is lower than 0.
     *   2. [ sourceLocalDateTime,              sourceLocalDateTime + difference ]   if difference is greater than 0
     * </pre>
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add/subtract the specified {@code difference}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<LocalDateTime, LocalDateTime> getLocalDateTimeIntervalFromGiven(final LocalDateTime sourceLocalDateTime,
                                                                                         final long difference,
                                                                                         final ChronoUnit timeUnit) {
        return getLocalDateTimeIntervalFromGiven(
                sourceLocalDateTime,
                difference,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <pre>
     *   1. [ sourceLocalDateTime - difference, sourceLocalDateTime ]                if difference is lower than 0.
     *   2. [ sourceLocalDateTime,              sourceLocalDateTime + difference ]   if difference is greater than 0
     * </pre>
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add/subtract the specified {@code difference}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<LocalDateTime, LocalDateTime> getLocalDateTimeIntervalFromGiven(final LocalDateTime sourceLocalDateTime,
                                                                                         final long difference,
                                                                                         final ChronoUnit timeUnit,
                                                                                         final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now(finalZoneId)
        )
                .atZone(finalZoneId)
                .toLocalDateTime();

        return 0 > difference
                ? Tuple2.of(
                minus(
                        finalSourceLocalDateTime,
                        Math.abs(difference),
                        timeUnit,
                        finalZoneId
                ),
                finalSourceLocalDateTime
        )
                : Tuple2.of(
                finalSourceLocalDateTime,
                plus(
                        finalSourceLocalDateTime,
                        difference,
                        timeUnit,
                        finalZoneId
                )
        );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceDate} with the specified {@code amountToSubtract} subtracted,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then new {@link Date} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link Date}
     */
    public static Date minus(final Date sourceDate,
                             final long amountToSubtract,
                             final ChronoUnit timeUnit) {
        return minus(
                sourceDate,
                amountToSubtract,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceDate} with the specified {@code amountToSubtract} subtracted,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then new {@link Date} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date minus(final Date sourceDate,
                             final long amountToSubtract,
                             final ChronoUnit timeUnit,
                             final ZoneId zoneId) {
        return fromLocalDateTimeToDate(
                minus(
                        fromDateToLocalDateTime(sourceDate, zoneId),
                        amountToSubtract,
                        timeUnit,
                        zoneId
                ),
                zoneId
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToSubtract}
     * subtracted, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime minus(final LocalDateTime sourceLocalDateTime,
                                      final long amountToSubtract,
                                      final ChronoUnit timeUnit) {
        return minus(
                sourceLocalDateTime,
                amountToSubtract,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToSubtract}
     * subtracted, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime minus(final LocalDateTime sourceLocalDateTime,
                                      final long amountToSubtract,
                                      final ChronoUnit timeUnit,
                                      final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now()
        );
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        return finalSourceLocalDateTime
                .atZone(finalZoneId)
                .minus(
                        amountToSubtract,
                        finalTimeUnit
                )
                .toLocalDateTime();
    }


    /**
     *    Returns a {@link Date} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd} added,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to add the specified {@code amountToAdd}. If {@code null} then new {@link Date} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link Date}
     */
    public static Date plus(final Date sourceDate,
                            final long amountToAdd,
                            final ChronoUnit timeUnit) {
        return plus(
                sourceDate,
                amountToAdd,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd} added,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to add the specified {@code amountToAdd}. If {@code null} then new {@link Date} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date plus(final Date sourceDate,
                            final long amountToAdd,
                            final ChronoUnit timeUnit,
                            final ZoneId zoneId) {
        return fromLocalDateTimeToDate(
                plus(
                        fromDateToLocalDateTime(sourceDate, zoneId),
                        amountToAdd,
                        timeUnit,
                        zoneId
                ),
                zoneId
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd}
     * added, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add the specified {@code amountToAdd}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime plus(final LocalDateTime sourceLocalDateTime,
                                     final long amountToAdd,
                                     final ChronoUnit timeUnit) {
        return plus(
                sourceLocalDateTime,
                amountToAdd,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd}
     * added, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add the specified {@code amountToAdd}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime plus(final LocalDateTime sourceLocalDateTime,
                                     final long amountToAdd,
                                     final ChronoUnit timeUnit,
                                     final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now()
        );
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        return finalSourceLocalDateTime
                .atZone(finalZoneId)
                .plus(
                        amountToAdd,
                        finalTimeUnit
                )
                .toLocalDateTime();
    }


    /**
     *    Converts to a {@link Date} the given {@code sourceDate} using {@link DateTimeUtil#DEFAULT_DATETIME_FORMAT} as
     * pattern.
     *
     * @param sourceDate
     *    {@link String} with the date value to convert. If empty a new {@link Date} will be used.
     *
     * @return {@link Date} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static Date toDate(final String sourceDate) {
        return toDate(
                sourceDate,
                DEFAULT_DATETIME_FORMAT
        );
    }


    /**
     * Converts to a {@link Date} the given {@code sourceDate} using provided {@code pattern}.
     *
     * @param sourceDate
     *     {@link String} with the date value to convert. If empty a new {@link Date} will be used.
     * @param pattern
     *    The pattern describing the date and time format
     *
     * @return {@link Date} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static Date toDate(final String sourceDate,
                              final String pattern) {
        final String finalPatter = getOrElse(
                pattern,
                DEFAULT_DATETIME_FORMAT
        );
        final String finalSourceDate = StringUtil.isBlank(sourceDate)
                ? new SimpleDateFormat(finalPatter)
                     .format(
                             new Date()
                     )
                : sourceDate;
        try {
            return new SimpleDateFormat(finalPatter)
                    .parse(
                            finalSourceDate
                    );

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("There was an error trying to convert to a Date value the string: %s using the format: %s",
                            finalSourceDate,
                            finalPatter
                    ),
                    e
            );
        }
    }


    /**
     *    Converts to a {@link LocalDate} the given {@code sourceDate} using {@link DateTimeUtil#DEFAULT_DATE_FORMAT} as
     * pattern.
     *
     * @param sourceDate
     *    {@link String} with the date value to convert. If empty a new {@link LocalDate} will be used.
     *
     * @return {@link LocalDate} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static LocalDate toLocalDate(final String sourceDate) {
        return toLocalDate(
                sourceDate,
                DEFAULT_DATE_FORMAT
        );
    }


    /**
     * Converts to a {@link LocalDate} the given {@code sourceDate} using provided {@code pattern}.
     *
     * @param sourceDate
     *     {@link String} with the date value to convert. If empty a new {@link LocalDate} will be used.
     * @param pattern
     *    The pattern describing the date and time format
     *
     * @return {@link LocalDate} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static LocalDate toLocalDate(final String sourceDate,
                                        final String pattern) {
        final String finalSourceDate = StringUtil.isBlank(sourceDate)
                ? LocalDate.now().toString()
                : sourceDate;
        final String finalPatter = getOrElse(
                pattern,
                DEFAULT_DATE_FORMAT
        );
        try {
            return LocalDate.from(
                    DateTimeFormatter.ofPattern(finalPatter)
                            .parse(
                                    finalSourceDate
                            )
            );

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("There was an error trying to convert to a LocalDate value the string: %s using the format: %s",
                            finalSourceDate,
                            finalPatter
                    ),
                    e
            );
        }
    }


    /**
     *    Converts to a {@link LocalDateTime} the given {@code sourceDate} using {@link DateTimeUtil#DEFAULT_DATETIME_FORMAT}
     * as pattern.
     *
     * @param sourceDate
     *    {@link String} with the date-time value to convert. If empty a new {@link LocalDateTime} will be used.
     *
     * @return {@link LocalDateTime} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static LocalDateTime toLocalDateTime(final String sourceDate) {
        return toLocalDateTime(
                sourceDate,
                DEFAULT_DATETIME_FORMAT
        );
    }


    /**
     * Converts to a {@link LocalDateTime} the given {@code sourceDate} using provided {@code pattern}.
     *
     * @param sourceDate
     *     {@link String} with the date-time value to convert. If empty a new {@link LocalDateTime} will be used.
     * @param pattern
     *    The pattern describing the date and time format
     *
     * @return {@link LocalDateTime} value equivalent to the given {@code sourceDate}
     *
     * @throws IllegalArgumentException if there was an error converting provided {@code sourceDate}
     */
    public static LocalDateTime toLocalDateTime(final String sourceDate,
                                                final String pattern) {
        final String finalPatter = getOrElse(
                pattern,
                DEFAULT_DATE_FORMAT
        );
        final String finalSourceDate = StringUtil.isBlank(sourceDate)
                ?  DateTimeFormatter.ofPattern(finalPatter)
                      .format(
                              LocalDateTime.now()
                      )
                : sourceDate;
        try {
            return LocalDateTime.from(
                    DateTimeFormatter.ofPattern(finalPatter)
                            .parse(
                                    finalSourceDate
                            )
            );

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("There was an error trying to convert to a LocalDateTime value the string: %s using the format: %s",
                            finalSourceDate,
                            finalPatter
                    ),
                    e
            );
        }
    }

}
