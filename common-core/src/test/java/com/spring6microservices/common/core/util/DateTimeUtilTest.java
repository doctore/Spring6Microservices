package com.spring6microservices.common.core.util;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.stream.Stream;

import static com.spring6microservices.common.core.util.DateTimeUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class DateTimeUtilTest {

    static Stream<Arguments> compareDateTestCases() {
        Date d1 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 31, 0).getTime();
        Date d2 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 31, 30).getTime();
        Date d3 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 33, 0).getTime();
        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedResult
                Arguments.of( null,   null,   -1,        null,                 CompareToResult.ZERO ),
                Arguments.of( null,   null,   1,         null,                 CompareToResult.ZERO ),
                Arguments.of( d1,     null,   1,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   d1,     1,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d2,     0,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d2,     d1,     0,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d1,     d1,     -1,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d2,     -1,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d2,     d1,     -1,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d1,     d2,     29,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d2,     30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d2,     31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d2,     d1,     29,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d2,     d1,     30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d2,     d1,     31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d3,     1,         ChronoUnit.MINUTES,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d3,     2,         ChronoUnit.MINUTES,   CompareToResult.ZERO ),
                Arguments.of( d3,     d1,     1,         ChronoUnit.MINUTES,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d3,     d1,     2,         ChronoUnit.MINUTES,   CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareDateTestCases")
    @DisplayName("compare: Date test cases")
    public void compareDate_testCases(Date one,
                                      Date two,
                                      long epsilon,
                                      ChronoUnit timeUnit,
                                      CompareToResult expectedResult) {
        int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> compareLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 11, 11, 12, 31, 00);
        LocalDateTime ldt2 = LocalDateTime.of(2020, 11, 11, 12, 31, 30);
        LocalDateTime ldt3 = LocalDateTime.of(2020, 11, 11, 12, 33, 00);
        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedResult
                Arguments.of( null,   null,   -1,        null,                 CompareToResult.ZERO ),
                Arguments.of( null,   null,   1,         null,                 CompareToResult.ZERO ),
                Arguments.of( ldt1,   null,   1,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   ldt1,   1,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   0,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   0,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt1,   -1,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   -1,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   -1,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   29,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   29,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt3,   1,         ChronoUnit.MINUTES,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt3,   2,         ChronoUnit.MINUTES,   CompareToResult.ZERO ),
                Arguments.of( ldt3,   ldt1,   1,         ChronoUnit.MINUTES,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt3,   ldt1,   2,         ChronoUnit.MINUTES,   CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareLocalDateTimeTestCases")
    @DisplayName("compare: LocalDateTime test cases")
    public void compareLocalDateTime_testCases(LocalDateTime one,
                                               LocalDateTime two,
                                               long epsilon,
                                               ChronoUnit timeUnit,
                                               CompareToResult expectedResult) {
        int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> formatWithDateTestCases() {
        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 10, 5);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceDate,      expectedResult
                Arguments.of( gc1.getTime(),   "2020-10-10T12:00:00" ),
                Arguments.of( gc2.getTime(),   "2022-11-12T23:10:05" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("formatWithDateTestCases")
    @DisplayName("format: with Date test cases")
    public void formatWithDate_testCases(Date sourceDate,
                                         String expectedResult) {
        assertEquals(
                expectedResult,
                format(sourceDate)
        );
    }


    static Stream<Arguments> formatWithDateAllParametersTestCases() {
        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 10, 5);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceDate,      pattern,    expectedException,   expectedResult
                Arguments.of( gc1.getTime(),   "NotValid",                IllegalArgumentException.class,   null ),
                Arguments.of( gc1.getTime(),   null,                      null,                             "2020-10-10T12:00:00" ),
                Arguments.of( gc1.getTime(),   "yyyy-MM-dd'T'HH:mm:ss",   null,                             "2020-10-10T12:00:00" ),
                Arguments.of( gc2.getTime(),   "yyyy.MM.dd",              null,                             "2022.11.12" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("formatWithDateAllParametersTestCases")
    @DisplayName("format: using Date with all parameters test cases")
    public void formatWithDateAllParameters_testCases(Date sourceDate,
                                                      String pattern,
                                                      Class<? extends Exception> expectedException,
                                                      String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    format(
                            sourceDate,
                            pattern
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    format(
                            sourceDate,
                            pattern
                    )
            );
        }
    }


    static Stream<Arguments> formatWithTemporalAccessorTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 10, 5);
        return Stream.of(
                //@formatter:off
                //            sourceTemporal,   expectedResult
                Arguments.of( ldt1,             "2020-10-10T12:00:00" ),
                Arguments.of( ldt2,             "2022-11-12T23:10:05" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("formatWithTemporalAccessorTestCases")
    @DisplayName("format: with TemporalAccessor test cases")
    public void formatWithTemporalAccessor_testCases(TemporalAccessor sourceTemporal,
                                                     String expectedResult) {
        assertEquals(
                expectedResult,
                format(sourceTemporal)
        );
    }


    static Stream<Arguments> formatWithTemporalAccessorAllParametersTestCases() {
        LocalDate ld = LocalDate.of(2025, 9, 19);
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 10, 5);
        return Stream.of(
                //@formatter:off
                //            sourceTemporal,   pattern,    expectedException,   expectedResult
                Arguments.of( ld,               "yyyy-MM-dd HH:mm:ss",     UnsupportedTemporalTypeException.class,   null ),
                Arguments.of( ldt1,             "NotValid",                IllegalArgumentException.class,           null ),
                Arguments.of( ld,               "yyyy-MM-dd",              null,                                     "2025-09-19" ),
                Arguments.of( ldt1,             null,                      null,                                     "2020-10-10T12:00:00" ),
                Arguments.of( ldt1,             "yyyy-MM-dd'T'HH:mm:ss",   null,                                     "2020-10-10T12:00:00" ),
                Arguments.of( ldt2,             "yyyy.MM.dd",              null,                                     "2022.11.12" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("formatWithTemporalAccessorAllParametersTestCases")
    @DisplayName("format: using TemporalAccessor with all parameters test cases")
    public void formatWithTemporalAccessorAllParameters_testCases(TemporalAccessor sourceTemporal,
                                                                  String pattern,
                                                                  Class<? extends Exception> expectedException,
                                                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    format(
                            sourceTemporal,
                            pattern
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    format(
                            sourceTemporal,
                            pattern
                    )
            );
        }
    }


    static Stream<Arguments> fromDateToLocalDateWithDateTestCases() {
        LocalDate ld1 = LocalDate.of(2020, 10, 10);
        LocalDate ld2 = LocalDate.of(2022, 11, 12);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 0, 0);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceDate,      expectedResult
                Arguments.of( null,            LocalDate.now() ),
                Arguments.of( gc1.getTime(),   ld1 ),
                Arguments.of( gc2.getTime(),   ld2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateWithDateTestCases")
    @DisplayName("fromDateToLocalDate: with Date test cases")
    public void fromDateToLocalDateWithDate_testCases(Date sourceDate,
                                                      LocalDate expectedResult) {
        assertEquals(
                expectedResult,
                fromDateToLocalDate(sourceDate)
        );
    }


    static Stream<Arguments> fromDateToLocalDateAllParametersTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDate ld1 = LocalDate.of(2020, 10, 10);
        LocalDate ld2 = LocalDate.of(2022, 11, 12);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));

        LocalDate expectedResultNullSourceAndZoneId = LocalDate.now(ZoneId.systemDefault());
        LocalDate expectedResultNullSourceAndGmt = LocalDate.now(gmtZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,      zoneId,      expectedResult
                Arguments.of( null,            null,        expectedResultNullSourceAndZoneId ),
                Arguments.of( null,            gmtZoneId,   expectedResultNullSourceAndGmt ),
                Arguments.of( gc1.getTime(),   gmtZoneId,   ld1 ),
                Arguments.of( gc2.getTime(),   gmtZoneId,   ld2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateAllParametersTestCases")
    @DisplayName("fromDateToLocalDate: with all parameters test cases")
    public void fromDateToLocalDateAllParameters_testCases(Date sourceDate,
                                                           ZoneId zoneId,
                                                           LocalDate expectedResult) {
        assertEquals(
                expectedResult,
                fromDateToLocalDate(sourceDate, zoneId)
        );
    }


    static Stream<Arguments> fromDateToLocalDateTimeWithDateTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 0, 0);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceDate,      expectedResult
                Arguments.of( null,            null ),
                Arguments.of( gc1.getTime(),   ldt1 ),
                Arguments.of( gc2.getTime(),   ldt2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateTimeWithDateTestCases")
    @DisplayName("fromDateToLocalDateTime: with Date test cases")
    public void fromDateToLocalDateTimeWithDate_testCases(Date sourceDate,
                                                          LocalDateTime expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    fromDateToLocalDateTime(sourceDate),
                    LocalDateTime.now(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    fromDateToLocalDateTime(sourceDate)
            );
        }
    }


    static Stream<Arguments> fromDateToLocalDateTimeAllParametersTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));

        LocalDateTime expectedResultNullSourceAndZoneId = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime expectedResultNullSourceAndGmt = LocalDateTime.now(gmtZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,      zoneId,      expectedResult
                Arguments.of( null,            null,        expectedResultNullSourceAndZoneId ),
                Arguments.of( null,            gmtZoneId,   expectedResultNullSourceAndGmt ),
                Arguments.of( gc1.getTime(),   gmtZoneId,   ldt1 ),
                Arguments.of( gc2.getTime(),   gmtZoneId,   ldt2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateTimeAllParametersTestCases")
    @DisplayName("fromDateToLocalDateTime: with all parameters test cases")
    public void fromDateToLocalDateTimeAllParameters_testCases(Date sourceDate,
                                                               ZoneId zoneId,
                                                               LocalDateTime expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    fromDateToLocalDateTime(sourceDate, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    fromDateToLocalDateTime(sourceDate, zoneId)
            );
        }
    }


    static Stream<Arguments> fromLocalDateToDateWithLocalDateTestCases() {
        LocalDate ld = LocalDate.of(2020, 10, 10);

        GregorianCalendar gc = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 0, 0, 0);
        gc.setTimeZone(TimeZone.getDefault());

        Date expectedResultNullLocalDate = Date.from(
                LocalDate.now()
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        )
                        .toInstant()
        );
        return Stream.of(
                //@formatter:off
                //            sourceLocalDate,   expectedResult
                Arguments.of( null,              expectedResultNullLocalDate ),
                Arguments.of( ld,                gc.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateToDateWithLocalDateTestCases")
    @DisplayName("fromLocalDateToDate: with LocalDate test cases")
    public void fromLocalDateToDateWithLocalDate_testCases(LocalDate sourceLocalDate,
                                                           Date expectedResult) {
        assertEquals(
                expectedResult,
                fromLocalDateToDate(sourceLocalDate)
        );
    }


    static Stream<Arguments> fromLocalDateToDateAllParametersTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDate ld1 = LocalDate.of(2020, 10, 10);
        LocalDate ld2 = LocalDate.of(2022, 11, 12);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 1, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 2, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));

        Date expectedResultNullZoneIdAndLocalDate = Date.from(
                LocalDate.now()
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        )
                        .toInstant()
        );
        Date expectedResultNullAndLocalDate = Date.from(
                LocalDate.now(gmtZoneId)
                        .atStartOfDay(
                                gmtZoneId
                        )
                        .toInstant()
        );
        return Stream.of(
                //@formatter:off
                //            sourceLocalDate,   zoneId,      expectedResult
                Arguments.of( null,              null,        expectedResultNullZoneIdAndLocalDate ),
                Arguments.of( null,              gmtZoneId,   expectedResultNullAndLocalDate ),
                Arguments.of( ld1,               gmtZoneId,   gc1.getTime() ),
                Arguments.of( ld2,               gmtZoneId,   gc2.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateToDateAllParametersTestCases")
    @DisplayName("fromLocalDateToDate: with all parameters test cases")
    public void fromLocalDateToDateAllParameters_testCases(LocalDate sourceLocalDate,
                                                           ZoneId zoneId,
                                                           Date expectedResult) {
        assertEquals(
                expectedResult,
                fromLocalDateToDate(sourceLocalDate, zoneId)
        );
    }


    static Stream<Arguments> fromLocalDateTimeToDateWithLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 0, 0);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   expectedResult
                Arguments.of( null,                  null ),
                Arguments.of( ldt1,                  gc1.getTime() ),
                Arguments.of( ldt2,                  gc2.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateTimeToDateWithLocalDateTimeTestCases")
    @DisplayName("fromLocalDateTimeToDate: with LocalDateTime test cases")
    public void fromLocalDateTimeToDateWithLocalDateTime_testCases(LocalDateTime sourceLocalDateTime,
                                                                   Date expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    fromLocalDateTimeToDate(sourceLocalDateTime),
                    new Date(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    fromLocalDateTimeToDate(sourceLocalDateTime)
            );
        }
    }


    static Stream<Arguments> fromLocalDateTimeToDateAllParametersTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   zoneId,      expectedResult
                Arguments.of( null,                  null,        null ),
                Arguments.of( null,                  gmtZoneId,   null ),
                Arguments.of( ldt1,                  gmtZoneId,   gc1.getTime() ),
                Arguments.of( ldt2,                  gmtZoneId,   gc2.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateTimeToDateAllParametersTestCases")
    @DisplayName("fromLocalDateTimeToDate: with all parameters test cases")
    public void fromLocalDateTimeToDateAllParameters_testCases(LocalDateTime sourceLocalDateTime,
                                                               ZoneId zoneId,
                                                               Date expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    fromLocalDateTimeToDate(sourceLocalDateTime),
                    new Date(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    fromLocalDateTimeToDate(sourceLocalDateTime, zoneId)
            );
        }
    }


    static Stream<Arguments> getDateIntervalFromGivenWithDateAndDifferenceAndChronoUnitTestCases() {
        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        Date nullSource = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        Date nullSourceNegativeAmountNullUnit = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1), ZoneId.systemDefault());
        Date nullSourceNegativeAmountAndSeconds = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusSeconds(1), ZoneId.systemDefault());
        Date nullSourcePositiveAmountAndMinutes = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1), ZoneId.systemDefault());

        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountNullUnit = Tuple2.of(
                nullSourceNegativeAmountNullUnit,
                nullSource
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountAndSeconds = Tuple2.of(
                nullSourceNegativeAmountAndSeconds,
                nullSource
        );
        Tuple2<Date, Date> expectedResultNullSourcePositiveAmountAndMinutes = Tuple2.of(
                nullSource,
                nullSourcePositiveAmountAndMinutes
        );
        return Stream.of(
                //@formatter:off
                //            sourceDate,   difference,   timeUnit,             expectedResult
                Arguments.of( null,         -1,           null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,         -1,           ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,         1,            ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      Tuple2.of(d1, d1) ),
                Arguments.of( d2,           2,            ChronoUnit.MONTHS,    Tuple2.of(d2, d1) ),
                Arguments.of( d3,           1,            ChronoUnit.HOURS,     Tuple2.of(d3, d2) ),
                Arguments.of( d4,           5,            ChronoUnit.MINUTES,   Tuple2.of(d4, d3) ),
                Arguments.of( d1,          -2,            ChronoUnit.MONTHS,    Tuple2.of(d2, d1) ),
                Arguments.of( d2,          -1,            ChronoUnit.HOURS,     Tuple2.of(d3, d2) ),
                Arguments.of( d3,          -5,            ChronoUnit.MINUTES,   Tuple2.of(d4, d3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getDateIntervalFromGivenWithDateAndDifferenceAndChronoUnitTestCases")
    @DisplayName("getDateIntervalFromGiven: with Date and Difference and ChronoUnit test cases")
    public void getDateIntervalFromGivenWithDateAndDifferenceAndChronoUnit_testCases(Date sourceDate,
                                                                                     long difference,
                                                                                     ChronoUnit timeUnit,
                                                                                     Tuple2<Date, Date> expectedResult) {
        Tuple2<Date, Date> result = getDateIntervalFromGiven(sourceDate, difference, timeUnit);
        if (null == sourceDate) {
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        }
        else {
            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> getDateIntervalFromGivenAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date nullSourceAndZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        Date nullSourceAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId), utcZoneId);
        Date nullSourceNegativeAmountNullUnitZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1), ZoneId.systemDefault());
        Date nullSourceNegativeAmountNullUnitAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minusMinutes(1), utcZoneId);
        Date nullSourceNegativeAmountAndSecondsAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusSeconds(1), ZoneId.systemDefault());
        Date nullSourceNegativeAmountAndSecondsAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minusSeconds(1), utcZoneId);
        Date nullSourcePositiveAmountAndMinutesAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1), ZoneId.systemDefault());
        Date nullSourcePositiveAmountAndMinutesAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plusMinutes(1), utcZoneId);

        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountNullUnitZoneId = Tuple2.of(
                nullSourceNegativeAmountNullUnitZoneId,
                nullSourceAndZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountNullUnitAndUtc = Tuple2.of(
                nullSourceNegativeAmountNullUnitAndUtc,
                nullSourceAndUtc
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndNullZoneId,
                nullSourceAndZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountAndSecondsAndUtc = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndUtc,
                nullSourceAndUtc
        );
        Tuple2<Date, Date> expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = Tuple2.of(
                nullSourceAndZoneId,
                nullSourcePositiveAmountAndMinutesAndNullZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourcePositiveAmountAndMinutesAndUtc = Tuple2.of(
                nullSourceAndUtc,
                nullSourcePositiveAmountAndMinutesAndUtc
        );
        return Stream.of(
                //@formatter:off
                //            sourceDate,   difference,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,           null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,           null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,           ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,           ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,            ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,            ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     null,        Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     utcZoneId,   Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      null,        Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      utcZoneId,   Tuple2.of(d1, d1) ),
                Arguments.of( d2,           2,            ChronoUnit.MONTHS,    null,        Tuple2.of(d2, d1) ),
                Arguments.of( d3,           1,            ChronoUnit.HOURS,     null,        Tuple2.of(d3, d2) ),
                Arguments.of( d4,           5,            ChronoUnit.MINUTES,   null,        Tuple2.of(d4, d3) ),
                Arguments.of( d1,          -2,            ChronoUnit.MONTHS,    null,        Tuple2.of(d2, d1) ),
                Arguments.of( d2,          -1,            ChronoUnit.HOURS,     null,        Tuple2.of(d3, d2) ),
                Arguments.of( d3,          -5,            ChronoUnit.MINUTES,   null,        Tuple2.of(d4, d3) ),
                Arguments.of( gc2Date,      2,            ChronoUnit.MONTHS,    gmtZoneId,   Tuple2.of(gc2Date, gc1Date) ),
                Arguments.of( gc1Date,     -2,            ChronoUnit.MONTHS,    gmtZoneId,   Tuple2.of(gc2Date, gc1Date) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getDateIntervalFromGivenAllParametersTestCases")
    @DisplayName("getDateIntervalFromGiven: with all parameters test cases")
    public void getDateIntervalFromGivenAllParameters_testCases(Date sourceDate,
                                                                long difference,
                                                                ChronoUnit timeUnit,
                                                                ZoneId zoneId,
                                                                Tuple2<Date, Date> expectedResult) {
        Tuple2<Date, Date> result = getDateIntervalFromGiven(sourceDate, difference, timeUnit, zoneId);
        if (null == sourceDate) {
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        }
        else {
            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> getLocalDateTimeIntervalFromGivenWithLocalDateTimeAndDifferenceAndChronoUnitTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime nullSource = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nullSourceNegativeAmountNullUnit = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        LocalDateTime nullSourceNegativeAmountAndSeconds = LocalDateTime.now(ZoneId.systemDefault()).minusSeconds(1);
        LocalDateTime nullSourcePositiveAmountAndMinutes = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1);

        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountNullUnit = Tuple2.of(
                nullSourceNegativeAmountNullUnit,
                nullSource
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountAndSeconds = Tuple2.of(
                nullSourceNegativeAmountAndSeconds,
                nullSource
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourcePositiveAmountAndMinutes = Tuple2.of(
                nullSource,
                nullSourcePositiveAmountAndMinutes
        );
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   difference,   timeUnit,             expectedResult
                Arguments.of( null,                  -1,           null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,                  -1,           ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,                  1,            ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( ldt1,                  0,            ChronoUnit.HOURS,     Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.DAYS,      Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt2,                  2,            ChronoUnit.MONTHS,    Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt3,                  1,            ChronoUnit.HOURS,     Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt4,                  5,            ChronoUnit.MINUTES,   Tuple2.of(ldt4, ldt3) ),
                Arguments.of( ldt1,                  -2,           ChronoUnit.MONTHS,    Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt2,                  -1,           ChronoUnit.HOURS,     Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt3,                  -5,           ChronoUnit.MINUTES,   Tuple2.of(ldt4, ldt3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getLocalDateTimeIntervalFromGivenWithLocalDateTimeAndDifferenceAndChronoUnitTestCases")
    @DisplayName("getLocalDateTimeIntervalFromGiven: with LocalDateTime and Difference and ChronoUnit test cases")
    public void getLocalDateTimeIntervalFromGivenWithLocalDateTimeAndDifferenceAndChronoUnit_testCases(LocalDateTime sourceLocalDateTime,
                                                                                                       long difference,
                                                                                                       ChronoUnit timeUnit,
                                                                                                       Tuple2<LocalDateTime, LocalDateTime> expectedResult) {
        Tuple2<LocalDateTime, LocalDateTime> result = getLocalDateTimeIntervalFromGiven(sourceLocalDateTime, difference, timeUnit);
        if (null == sourceLocalDateTime) {
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        }
        else {
            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> getLocalDateTimeIntervalFromGivenAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime nullSourceAndZoneId = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nullSourceAndUtc = LocalDateTime.now(utcZoneId);
        LocalDateTime nullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        LocalDateTime nullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now(utcZoneId).minusMinutes(1);
        LocalDateTime nullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now(ZoneId.systemDefault()).minusSeconds(1);
        LocalDateTime nullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now(utcZoneId).minusSeconds(1);
        LocalDateTime nullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1);
        LocalDateTime nullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now(utcZoneId).plusMinutes(1);

        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountNullUnitZoneId = Tuple2.of(
                nullSourceNegativeAmountNullUnitZoneId,
                nullSourceAndZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountNullUnitAndUtc = Tuple2.of(
                nullSourceNegativeAmountNullUnitAndUtc,
                nullSourceAndUtc
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndNullZoneId,
                nullSourceAndZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountAndSecondsAndUtc = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndUtc,
                nullSourceAndUtc
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = Tuple2.of(
                nullSourceAndZoneId,
                nullSourcePositiveAmountAndMinutesAndNullZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourcePositiveAmountAndMinutesAndUtc = Tuple2.of(
                nullSourceAndUtc,
                nullSourcePositiveAmountAndMinutesAndUtc
        );
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   difference,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,                  -1,           null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,           null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,           ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,           ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,            ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,            ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,            ChronoUnit.HOURS,     null,        Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.HOURS,     utcZoneId,   Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.DAYS,      null,        Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.DAYS,      utcZoneId,   Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt2,                  2,            ChronoUnit.MONTHS,    null,        Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt3,                  1,            ChronoUnit.HOURS,     null,        Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt4,                  5,            ChronoUnit.MINUTES,   null,        Tuple2.of(ldt4, ldt3) ),
                Arguments.of( ldt1,                  -2,           ChronoUnit.MONTHS,    null,        Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt2,                  -1,           ChronoUnit.HOURS,     null,        Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt3,                  -5,           ChronoUnit.MINUTES,   null,        Tuple2.of(ldt4, ldt3) ),
                Arguments.of( ldtUtcPlus1,           2,            ChronoUnit.MONTHS,    utcZoneId,   Tuple2.of(ldtUtcPlus1, ldtUtc) ),
                Arguments.of( ldtUtc,                -2,           ChronoUnit.MONTHS,    utcZoneId,   Tuple2.of(ldtUtcPlus1, ldtUtc) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getLocalDateTimeIntervalFromGivenAllParametersTestCases")
    @DisplayName("getLocalDateTimeIntervalFromGiven: with all parameters test cases")
    public void getLocalDateTimeIntervalFromGivenAllParameters_testCases(LocalDateTime sourceLocalDateTime,
                                                                         long difference,
                                                                         ChronoUnit timeUnit,
                                                                         ZoneId zoneId,
                                                                         Tuple2<LocalDateTime, LocalDateTime> expectedResult) {
        Tuple2<LocalDateTime, LocalDateTime> result = getLocalDateTimeIntervalFromGiven(sourceLocalDateTime, difference, timeUnit, zoneId);
        if (null == sourceLocalDateTime) {
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        }
        else {
            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> minusDateWithAmountToSubtractAndTimeUnitTestCases() {
        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        Date expectedResultNullSourceNegativeAmountNullUnit = fromLocalDateTimeToDate(LocalDateTime.now().plusMinutes(1));
        Date expectedResultNullSourceNegativeAmountAndSeconds = fromLocalDateTimeToDate(LocalDateTime.now().plusSeconds(1));
        Date expectedResultNullSourcePositiveAmountAndMinutes = fromLocalDateTimeToDate(LocalDateTime.now().minusMinutes(1));
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToSubtract,   timeUnit,             expectedResult
                Arguments.of( null,         -1,                 null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      d1 ),
                Arguments.of( d1,           2,                  ChronoUnit.MONTHS,    d2 ),
                Arguments.of( d2,           1,                  ChronoUnit.HOURS,     d3 ),
                Arguments.of( d3,           5,                  ChronoUnit.MINUTES,   d4 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusDateWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("minus: Date with amountToSubtract and TimeUnit test cases")
    public void minusDateWithAmountToSubtractAndTimeUnit_testCases(Date sourceDate,
                                                                   long amountToSubtract,
                                                                   ChronoUnit timeUnit,
                                                                   Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceDate, amountToSubtract, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(expectedResult, minus(sourceDate, amountToSubtract, timeUnit));
        }
    }


    static Stream<Arguments> minusDateAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date expectedResultNullSourceNegativeAmountNullUnitZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountNullUnitAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plusMinutes(1), utcZoneId);
        Date expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(1), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountAndSecondsAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plusSeconds(1), utcZoneId);
        Date expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1), ZoneId.systemDefault());
        Date expectedResultNullSourcePositiveAmountAndMinutesAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minusMinutes(1), utcZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToSubtract,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,                 null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,                 null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     null,        d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     utcZoneId,   d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      null,        d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      utcZoneId,   d1 ),
                Arguments.of( d1,           2,                  ChronoUnit.MONTHS,    null,        d2 ),
                Arguments.of( d2,           1,                  ChronoUnit.HOURS,     null,        d3 ),
                Arguments.of( d3,           5,                  ChronoUnit.MINUTES,   null,        d4 ),
                Arguments.of( gc1Date,      2,                  ChronoUnit.MONTHS,    gmtZoneId,   gc2Date )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusDateAllParametersTestCases")
    @DisplayName("minus: Date with all parameters test cases")
    public void minusDateAllParameters_testCases(Date sourceDate,
                                                 long amountToSubtract,
                                                 ChronoUnit timeUnit,
                                                 ZoneId zoneId,
                                                 Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceDate, amountToSubtract, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    minus(sourceDate, amountToSubtract, timeUnit, zoneId)
            );
        }
    }


    static Stream<Arguments> minusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnit = LocalDateTime.now().plusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSeconds = LocalDateTime.now().minusSeconds(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutes = LocalDateTime.now().minusMinutes(1);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToSubtract,   timeUnit,             expectedResult
                Arguments.of( null,                  -1,                 null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      ldt1 ),
                Arguments.of( ldt1,                  2,                  ChronoUnit.MONTHS,    ldt2 ),
                Arguments.of( ldt2,                  1,                  ChronoUnit.HOURS,     ldt3 ),
                Arguments.of( ldt3,                  5,                  ChronoUnit.MINUTES,   ldt4 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("minus: LocalDateTime with amountToSubtract and TimeUnit test cases")
    public void minusLocalDateTimeWithAmountToSubtractAndTimeUnit_testCases(LocalDateTime sourceLocalDateTime,
                                                                            long amountToSubtract,
                                                                            ChronoUnit timeUnit,
                                                                            LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit)
            );
        }
    }


    static Stream<Arguments> minusLocalDateTimeAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now().plusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now().plusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now().minusSeconds(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now().minusSeconds(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now().minusMinutes(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now().minusMinutes(1);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToSubtract,   timeUnit,   zoneId,                expectedResult
                Arguments.of( null,                  -1,                 null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,                 null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     null,        ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      null,        ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  2,                  ChronoUnit.MONTHS,    null,        ldt2 ),
                Arguments.of( ldt2,                  1,                  ChronoUnit.HOURS,     null,        ldt3 ),
                Arguments.of( ldt3,                  5,                  ChronoUnit.MINUTES,   null,        ldt4 ),
                Arguments.of( ldtUtc,                2,                  ChronoUnit.MONTHS,    null,        ldtUtcPlus1 ),
                Arguments.of( ldtUtc,                2,                  ChronoUnit.MONTHS,    utcZoneId,   ldtUtcPlus1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusLocalDateTimeAllParametersTestCases")
    @DisplayName("minus: LocalDateTime with all parameters test cases")
    public void minusLocalDateTimeAllParameters_testCases(LocalDateTime sourceLocalDateTime,
                                                          long amountToSubtract,
                                                          ChronoUnit timeUnit,
                                                          ZoneId zoneId,
                                                          LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
           int compareResult = DateTimeUtil.compare(
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit, zoneId)
            );
        }
    }


    static Stream<Arguments> plusDateWithAmountToSubtractAndTimeUnitTestCases() {
        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        Date expectedResultNullSourceNegativeAmountNullUnit = fromLocalDateTimeToDate(LocalDateTime.now().minusMinutes(1));
        Date expectedResultNullSourceNegativeAmountAndSeconds = fromLocalDateTimeToDate(LocalDateTime.now().minusSeconds(1));
        Date expectedResultNullSourcePositiveAmountAndMinutes = fromLocalDateTimeToDate(LocalDateTime.now().plusMinutes(1));
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToAdd,   timeUnit,             expectedResult
                Arguments.of( null,         -1,            null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      d1 ),
                Arguments.of( d2,           2,             ChronoUnit.MONTHS,    d1 ),
                Arguments.of( d3,           1,             ChronoUnit.HOURS,     d2 ),
                Arguments.of( d4,           5,             ChronoUnit.MINUTES,   d3 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusDateWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("plus: Date with amountToSubtract and TimeUnit test cases")
    public void plusDateWithAmountToAddAndTimeUnit_testCases(Date sourceDate,
                                                             long amountToAdd,
                                                             ChronoUnit timeUnit,
                                                             Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceDate, amountToAdd, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    plus(sourceDate, amountToAdd, timeUnit)
            );
        }
    }


    static Stream<Arguments> plusDateAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date expectedResultNullSourceNegativeAmountNullUnitZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minusMinutes(1), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountNullUnitAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minusMinutes(1), utcZoneId);
        Date expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(1), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountAndSecondsAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plusSeconds(1), utcZoneId);
        Date expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(1), ZoneId.systemDefault());
        Date expectedResultNullSourcePositiveAmountAndMinutesAndUtc = fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plusMinutes(1), utcZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToAdd,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,            null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,            null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     null,        d1 ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     utcZoneId,   d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      null,        d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      utcZoneId,   d1 ),
                Arguments.of( d2,           2,             ChronoUnit.MONTHS,    null,        d1 ),
                Arguments.of( d3,           1,             ChronoUnit.HOURS,     null,        d2 ),
                Arguments.of( d4,           5,             ChronoUnit.MINUTES,   null,        d3 ),
                Arguments.of( gc2Date,      2,             ChronoUnit.MONTHS,    gmtZoneId,   gc1Date )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusDateAllParametersTestCases")
    @DisplayName("plus: Date with all parameters test cases")
    public void plusDateAllParameters_testCases(Date sourceDate,
                                                long amountToAdd,
                                                ChronoUnit timeUnit,
                                                ZoneId zoneId,
                                                Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceDate, amountToAdd, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    plus(sourceDate, amountToAdd, timeUnit, zoneId)
            );
        }
    }


    static Stream<Arguments> plusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnit = LocalDateTime.now().minusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSeconds = LocalDateTime.now().plusSeconds(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutes = LocalDateTime.now().plusMinutes(1);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToAdd,   timeUnit,             expectedResult
                Arguments.of( null,                  -1,            null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      ldt1 ),
                Arguments.of( ldt2,                  2,             ChronoUnit.MONTHS,    ldt1 ),
                Arguments.of( ldt3,                  1,             ChronoUnit.HOURS,     ldt2 ),
                Arguments.of( ldt4,                  5,             ChronoUnit.MINUTES,   ldt3 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("plus: LocalDateTime with amountToSubtract and TimeUnit test cases")
    public void plusLocalDateTimeWithAmountToAddAndTimeUnit_testCases(LocalDateTime sourceLocalDateTime,
                                                                      long amountToAdd,
                                                                      ChronoUnit timeUnit,
                                                                      LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceLocalDateTime, amountToAdd, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    plus(sourceLocalDateTime, amountToAdd, timeUnit)
            );
        }
    }


    static Stream<Arguments> plusLocalDateTimeAllParametersTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now().minusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now().minusMinutes(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now().plusSeconds(1);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now().plusSeconds(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now().plusMinutes(1);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now().plusMinutes(1);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToAdd,   timeUnit,   zoneId,                expectedResult
                Arguments.of( null,                  -1,            null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,            null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     null,        ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      null,        ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      utcZoneId,   ldt1 ),
                Arguments.of( ldt2,                  2,             ChronoUnit.MONTHS,    null,        ldt1 ),
                Arguments.of( ldt3,                  1,             ChronoUnit.HOURS,     null,        ldt2 ),
                Arguments.of( ldt4,                  5,             ChronoUnit.MINUTES,   null,        ldt3 ),
                Arguments.of( ldtUtcPlus1,           2,             ChronoUnit.MONTHS,    null,        ldtUtc ),
                Arguments.of( ldtUtcPlus1,           2,             ChronoUnit.MONTHS,    utcZoneId,   ldtUtc )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusLocalDateTimeAllParametersTestCases")
    @DisplayName("plus: LocalDateTime with all parameters test cases")
    public void plusLocalDateTimeAllParameters_testCases(LocalDateTime sourceLocalDateTime,
                                                         long amountToAdd,
                                                         ChronoUnit timeUnit,
                                                         ZoneId zoneId,
                                                         LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceLocalDateTime, amountToAdd, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        }
        else {
            assertEquals(
                    expectedResult,
                    plus(sourceLocalDateTime, amountToAdd, timeUnit, zoneId)
            );
        }
    }


    static Stream<Arguments> toDateWithSourceDateTestCases() throws ParseException {
        String sourceDate = "2020-10-10T12:00:00";
        Date expectedResult = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT)
                .parse(sourceDate);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   expectedException,                expectedResult
                Arguments.of( null,         null,                             null ),
                Arguments.of( "",           null,                             null ),
                Arguments.of( "NotValid",   IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toDateWithSourceDateTestCases")
    @DisplayName("toDate: with sourceDate test cases")
    public void toDateWithSourceDate_testCases(String sourceDate,
                                               Class<? extends Exception> expectedException,
                                               Date expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toDate(
                            sourceDate
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toDate(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toDate(
                            sourceDate
                    )
            );
        }
    }


    static Stream<Arguments> toDateAllParametersTestCases() throws ParseException {
        String sourceDate1 = "2020-10-10T12:00:00";
        String sourceDate2 = "2025-11-19 05:30:00";

        String dateTimeFormat2 = "yyyy-MM-dd HH:mm:ss";

        Date expectedResult1 = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT)
                .parse(sourceDate1);
        Date expectedResult2 = new SimpleDateFormat(dateTimeFormat2)
                .parse(sourceDate2);
        return Stream.of(
                //@formatter:off
                //            sourceDate,    pattern,                   expectedException,                expectedResult
                Arguments.of( null,          null,                      null,                             null ),
                Arguments.of( "",            null,                      null,                             null ),
                Arguments.of( "NotValid",    null,                      IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate1,   DEFAULT_DATETIME_FORMAT,   null,                             expectedResult1 ),
                Arguments.of( sourceDate2,   dateTimeFormat2,           null,                             expectedResult2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toDateAllParametersTestCases")
    @DisplayName("toDate: with all parameters test cases")
    public void toDateAllParameters_testCases(String sourceDate,
                                              String pattern,
                                              Class<? extends Exception> expectedException,
                                              Date expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toDate(
                            sourceDate,
                            pattern
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toDate(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toDate(
                            sourceDate,
                            pattern
                    )
            );
        }
    }


    static Stream<Arguments> toLocalDateWithSourceDateTestCases() throws ParseException {
        String sourceDate = "2020-10-10";
        LocalDate expectedResult = LocalDate.of(2020, 10, 10);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   expectedException,                expectedResult
                Arguments.of( null,         null,                             null ),
                Arguments.of( "",           null,                             null ),
                Arguments.of( "NotValid",   IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLocalDateWithSourceDateTestCases")
    @DisplayName("toLocalDate: with sourceDate test cases")
    public void toLocalDateWithSourceDate_testCases(String sourceDate,
                                                    Class<? extends Exception> expectedException,
                                                    LocalDate expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toLocalDate(
                            sourceDate
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toLocalDate(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toLocalDate(
                            sourceDate
                    )
            );
        }
    }


    static Stream<Arguments> toLocalDateAllParametersTestCases() throws ParseException {
        String sourceDate1 = "2020-10-10";
        String sourceDate2 = "2025.11.19";

        String dateFormat2 = "yyyy.MM.dd";

        LocalDate expectedResult1 = LocalDate.of(2020, 10, 10);
        LocalDate expectedResult2 = LocalDate.of(2025, 11, 19);
        return Stream.of(
                //@formatter:off
                //            sourceDate,    pattern,               expectedException,                expectedResult
                Arguments.of( null,          null,                  null,                             null ),
                Arguments.of( "",            null,                  null,                             null ),
                Arguments.of( "NotValid",    null,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate1,   DEFAULT_DATE_FORMAT,   null,                             expectedResult1 ),
                Arguments.of( sourceDate2,   dateFormat2,           null,                             expectedResult2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLocalDateAllParametersTestCases")
    @DisplayName("toLocalDate: with all parameters test cases")
    public void toLocalDateAllParameters_testCases(String sourceDate,
                                                   String pattern,
                                                   Class<? extends Exception> expectedException,
                                                   LocalDate expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toLocalDate(
                            sourceDate,
                            pattern
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toLocalDate(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toLocalDate(
                            sourceDate,
                            pattern
                    )
            );
        }
    }


    static Stream<Arguments> toLocalDateTimeWithSourceDateTestCases() throws ParseException {
        String sourceDate = "2020-10-10T12:00:00";
        LocalDateTime expectedResult = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   expectedException,                expectedResult
                Arguments.of( null,         null,                             null ),
                Arguments.of( "",           null,                             null ),
                Arguments.of( "NotValid",   IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLocalDateTimeWithSourceDateTestCases")
    @DisplayName("toLocalDateTime: with sourceDate test cases")
    public void toLocalDateTimeWithSourceDate_testCases(String sourceDate,
                                                        Class<? extends Exception> expectedException,
                                                        LocalDateTime expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toLocalDateTime(
                            sourceDate
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toLocalDateTime(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toLocalDateTime(
                            sourceDate
                    )
            );
        }
    }


    static Stream<Arguments> toLocalDateTimeAllParametersTestCases() throws ParseException {
        String sourceDate1 = "2020-10-10T12:00:00";
        String sourceDate2 = "2025-11-19 05:30:00";

        String dateTimeFormat2 = "yyyy-MM-dd HH:mm:ss";

        LocalDateTime expectedResult1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime expectedResult2 = LocalDateTime.of(2025, 11, 19, 5, 30, 0);
        return Stream.of(
                //@formatter:off
                //            sourceDate,    pattern,                   expectedException,                expectedResult
                Arguments.of( null,          null,                      null,                             null ),
                Arguments.of( "",            null,                      null,                             null ),
                Arguments.of( "NotValid",    null,                      IllegalArgumentException.class,   null ),
                Arguments.of( sourceDate1,   DEFAULT_DATETIME_FORMAT,   null,                             expectedResult1 ),
                Arguments.of( sourceDate2,   dateTimeFormat2,           null,                             expectedResult2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLocalDateTimeAllParametersTestCases")
    @DisplayName("toLocalDateTime: with all parameters test cases")
    public void toLocalDateTimeAllParameters_testCases(String sourceDate,
                                                       String pattern,
                                                       Class<? extends Exception> expectedException,
                                                       LocalDateTime expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    toLocalDateTime(
                            sourceDate,
                            pattern
                    )
            );
        }
        else if (StringUtil.isBlank(sourceDate)) {
            assertNotNull(
                    toLocalDateTime(
                            sourceDate
                    )
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    toLocalDateTime(
                            sourceDate,
                            pattern
                    )
            );
        }
    }


    private void verifyCompareToResult(final int actualResult,
                                       final CompareToResult expectedResult) {
        switch (expectedResult) {
            case LESS_THAN_ZERO -> assertTrue(0 > actualResult);
            case ZERO -> assertEquals(0, actualResult);
            case GREATER_THAN_ZERO -> assertTrue(0 < actualResult);
        }
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
