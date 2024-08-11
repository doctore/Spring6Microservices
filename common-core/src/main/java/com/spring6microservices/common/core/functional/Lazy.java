package com.spring6microservices.common.core.functional;

import com.spring6microservices.common.core.util.AssertUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 *    Used to manage a lazy evaluated value, useful when getting it has an important performance cost. Internally,
 * it uses a cached value so when the provided {@link Supplier} is invoked its result will be cached.
 *
 * <pre>
 * Example:
 *   final Lazy<Double> l = Lazy.of(Math::random);
 *   l.isEvaluated();   // false
 *   l.get();           // 0.123 (random generated)
 *   l.isEvaluated();   // true
 *   l.get();           // 0.123 (memoized)
 * </pre>
 */
public final class Lazy<T> implements Supplier<T> {

    private transient volatile Supplier<? extends T> supplier;
    private T value;


    /**
     * Construct a {@code Lazy}
     *
     * @param supplier
     *    {@link Supplier} used to get the value in a lazy way
     */
    private Lazy(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }


    /**
     * Creates a {@code Lazy} that requests its value from a given {@code Supplier}.
     *
     * @param supplier
     *    {@link Supplier} used to get the value in a lazy way
     *
     * @return {@code Lazy}
     *
     * @throws IllegalArgumentException if {@code supplier} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Lazy<T> of(final Supplier<? extends T> supplier) {
        AssertUtil.notNull(supplier, "supplier must be not null");
        if (supplier instanceof Lazy) {
            return (Lazy<T>) supplier;
        } else {
            return new Lazy<>(supplier);
        }
    }


    /**
     * Returns {@code true} if the argument is a {@link Lazy} object and its internal lazy value is equal to this one.
     *
     * @apiNote
     *    This method will not invoke the provided {@link Supplier} creating the instance, if the internal lazy value
     * was not previously calculated.
     *
     * @param o
     *    The reference {@link Object} with which to compare.
     *
     * @return  {@code true} if this {@link Lazy} is the same as the {@code o} argument, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Lazy)) {
            return false;
        } else {
            final Lazy<?> that = (Lazy<?>) o;
            if (!isEvaluated() || !that.isEvaluated()) {
                return false;
            }
            return Objects.equals(
                    get(),
                    that.get()
            );
        }
    }


    /**
     * Returns the hash code of internal lazy value if it was previously calculated or 0 if it does not.
     *
     * @apiNote
     *    This method will not invoke the provided {@link Supplier} creating the instance, if the internal lazy value
     * was not previously calculated.
     *
     * @return the hash code of internal lazy value if it was previously calculated, 0 otherwise
     */
    @Override
    public int hashCode() {
        return isEvaluated()
                ? Objects.hashCode(
                get()
        )
                : Objects.hashCode(null);
    }


    @Override
    public String toString() {
        return "Lazy (" + (!isEvaluated() ? "?" : value) + ")";
    }


    /**
     * Returns the internal lazy value if it was calculated previously, or invokes provided {@link Supplier} otherwise.
     *
     * @return internal lazy value
     */
    @Override
    public T get() {
        return computeValue();
    }


    /**
     *    Using the provided {@link Predicate} return the internal lazy value in an {@link Optional} if satisfies
     * {@code predicate} or given {@link Predicate} is {@code null}. {@link Optional#empty()} otherwise.
     *
     * @apiNote
     *    This method will invoke the provided {@link Supplier} creating the instance, if the internal lazy value was not
     * previously calculated.
     *
     * @param predicate
     *    {@link Predicate} to filter the internal lazy value
     *
     * @return {@link Optional} with internal lazy value: cached or the result of provided {@link Supplier} if satisfies
     *         {@code predicate} or given {@link Predicate} is {@code null}. {@link Optional#empty()} otherwise.
     */
    public Optional<T> filter(final Predicate<? super T> predicate) {
        final T v = get();
        return null == predicate || predicate.test(v)
                ? Optional.of(v)
                : empty();
    }


    /**
     * Check if current internal lazy value has been evaluated.
     *
     * @return {@code true} if the internal lazy value was evaluated, {@code false} otherwise.
     */
    public boolean isEvaluated() {
        return null == supplier;
    }


    /**
     *    Transform the cached value (or the one returned by provided {@link Supplier}) in a new one using the provided
     * {@code mapper}.
     *
     * @param mapper
     *    {@link Function} used to convert internal lazy value
     *
     * @return {@link Lazy}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Lazy<U> map(final Function<? super T, ? extends U> mapper) {
        AssertUtil.notNull(mapper, "mapper must be not null");
        return Lazy.of(
                () ->
                        mapper.apply(
                                get()
                        )
        );
    }


    /**
     * Performs the given {@code action} to the cached value (or the one returned by provided {@link Supplier}).
     *
     * @apiNote
     *    If given {@code code action} is not {@code null}, this method will invoke the provided {@link Supplier}
     * creating the instance, if the internal lazy value was not previously calculated.
     *
     * @param action
     *    {@link Consumer} invoked for the internal lazy value of the current {@link Lazy} instance.
     *
     * @return {@code Lazy}
     */
    public Lazy<T> peek(final Consumer<? super T> action) {
        if (null != action) {
            action.accept(
                    get()
            );
        }
        return this;
    }


    /**
     * Wrap the internal lazy value into an {@link Optional}.
     *
     * @apiNote
     *    This method will invoke the provided {@link Supplier} creating the instance, if the internal lazy value was not
     * previously calculated.
     *
     * @return {@link Optional}
     */
    public Optional<T> toOptional() {
        return ofNullable(
                get()
        );
    }


    /**
     *    Returns the internal lazy value if it has been previously computed. Otherwise, invokes provided {@link Supplier}
     * when the current instance was created.
     *
     * @return internal lazy value
     */
    private synchronized T computeValue() {
        if (null != supplier) {
            value = supplier.get();
            supplier = null;
        }
        return value;
    }

}
