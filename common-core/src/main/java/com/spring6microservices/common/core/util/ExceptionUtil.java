package com.spring6microservices.common.core.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Helper class to manage and get information related with {@link Throwable}.
 */
@UtilityClass
public class ExceptionUtil {

    /**
     *    Introspects the {@link Throwable} to obtain the root cause, returning a formatted message including
     * the class name and error message of {@code sourceThrowable} and similar with the root cause.
     *
     * @param sourceThrowable
     *    {@link Throwable} to get the root cause for
     *
     * @return {@link String} including the class name and error message of {@code sourceThrowable} and similar with the root cause,
     *         {@link StringUtil#EMPTY_STRING} if {@code sourceThrowable} is {@code null}
     */
    public static String getFormattedCurrentAndRootError(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(t -> {
                    Throwable rootT = getRootCause(t).orElse(t);
                    StringBuilder errorMessage = new StringBuilder(
                            format("The class of the exception is: %s with error message: %s",
                                    t.getClass().getName(),
                                    t.getMessage()
                            )
                    );
                    if (!t.getClass().getName().equals(rootT.getClass().getName()) ||
                            !Objects.equals(t.getMessage(), rootT.getMessage())) {
                        errorMessage.append(
                                format(". The root cause was an exception of class: %s with error message: %s",
                                        rootT.getClass().getName(),
                                        rootT.getMessage()
                                )
                        );
                    }
                    return errorMessage.toString();
                })
                .orElse(StringUtil.EMPTY_STRING);
    }


    /**
     *    Introspects the {@link Throwable} to obtain the root cause, returning a formatted message including
     * the class name of the root cause and the original error message.
     *
     * @param sourceThrowable
     *    {@link Throwable} to get the root cause for
     *
     * @return {@link String} including the class name of the root cause and the original error message,
     *         {@link StringUtil#EMPTY_STRING} if {@code sourceThrowable} is {@code null}
     */
    public static String getFormattedRootError(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(t -> {
                    Throwable rootT = getRootCause(t).orElse(t);
                    return format("The root cause was an exception of class: %s with error message: %s",
                            rootT.getClass().getName(),
                            rootT.getMessage()
                    );
                })
                .orElse(StringUtil.EMPTY_STRING);
    }


    /**
     *    Introspects the {@link Throwable} to obtain the root cause, walking through the exception chain to the last element,
     * "root" of the tree. If a cyclic is detected returns the previous {@link Throwable} to the repeated one.
     *
     * @param sourceThrowable
     *    {@link Throwable} to get the root cause for
     *
     * @return {@link Optional} containing the root cause of the provided {@link Throwable} or {@code sourceThrowable} if it has no cause,
     *         {@link Optional#empty()} if {@code sourceThrowable} is {@code null}.
     */
    public static Optional<Throwable> getRootCause(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(ExceptionUtil::getThrowableList)
                .filter(l -> !l.isEmpty())
                .map(List::getLast);
    }


    /**
     *    Gets the {@link List} of {@link Throwable} objects in the exception chain included in provided {@code sourceThrowable}.
     * A {@link Throwable} without cause will return a list containing one element: {@code sourceThrowable}.
     * <p>
     *    This method handles recursive cause structures that might otherwise cause infinite loops. The cause chain is processed
     * until the end is reached, or until the next item in the chain is already in the result set.
     *
     * @param sourceThrowable
     *    {@link Throwable} to inspect
     *
     * @return {@link List} of {@link Throwable}
     */
    public static List<Throwable> getThrowableList(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(t -> {
                    final Set<Throwable> throwables = new LinkedHashSet<>();
                    for(Throwable current = t; null != current && !throwables.contains(current); current = current.getCause()) {
                        throwables.add(current);
                    }
                    return new ArrayList<>(throwables);
                })
                .orElseGet(ArrayList::new);
    }


    /**
     * Returns the first {@link Throwable} that matches the specified {@code type} in the exception chain.
     *
     * @param sourceThrowable
     *    {@link Throwable} to inspect
     * @param type
     *    {@link Class} to search in the exception chain included in provided {@code sourceThrowable}
     *
     * @return {@link Optional} containing the {@link Throwable} of the {@code type} within throwables nested in the specified {@code sourceThrowable},
     *         {@link Optional#empty()} if {@code sourceThrowable} is {@code null}.
     */
    public static <T extends Throwable> Optional<T> throwableOfType(final Throwable sourceThrowable,
                                                                    final Class<T> type) {
        return throwableOfType(
                sourceThrowable,
                type,
                false
        );
    }


    /**
     *    Returns the first {@link Throwable} that matches the specified {@code type} in the exception chain. If {@code subclass}
     * is {@code true}, compares with {@link Class#isAssignableFrom(Class)}, otherwise compares using references.
     *
     * @param sourceThrowable
     *    {@link Throwable} to inspect
     * @param type
     *    {@link Class} to search in the exception chain included in provided {@code sourceThrowable}
     * @param subclass
     *    If {@code true}, compares with {@link Class#isAssignableFrom(Class)}, otherwise compares using references
     *
     * @return {@link Optional} containing the {@link Throwable} of the {@code type} within throwables nested in the specified {@code sourceThrowable},
     *         {@link Optional#empty()} if {@code sourceThrowable} is {@code null}.
     */
    public static <T extends Throwable> Optional<T> throwableOfType(final Throwable sourceThrowable,
                                                                    final Class<T> type,
                                                                    final boolean subclass) {
        if (null == type) {
            return empty();
        }
        return getThrowableList(sourceThrowable)
                .stream()
                .filter(Objects::nonNull)
                .filter(t ->
                        subclass
                                ? type.isAssignableFrom(t.getClass())
                                : type.equals(t.getClass())
                )
                .map(type::cast)
                .findFirst();
    }

}
