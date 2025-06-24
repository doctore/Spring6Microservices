package com.spring6microservices.common.spring.repository;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Extended {@link JpaRepository} to include custom methods we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    String RAW_SQL_SEPARATOR = ",";

    int DEFAULT_PAGE_SIZE = 25;


    /**
     * Return the internal {@link EntityManager} to provide more functionality to the repositories.
     *
     * @return {@link EntityManager}
     */
    EntityManager getEntityManager();


    /**
     * Returns the HQL representation of the internal query of the given {@link Query}.
     *
     * @param query
     *    {@link Query} to get its SQL query
     *
     * @return {@link String} with the HQL representation of the internal query,
     *         empty {@link String} is there is any error getting it.
     */
    String getHQLQuery(final Query query);


    /**
     * Returns the HQL representation of the internal query of the given {@link TypedQuery}.
     *
     * @param query
     *    {@link TypedQuery} to get its SQL query
     *
     * @return {@link String} with the HQL representation of the internal query,
     *         empty {@link String} is there is any error getting it.
     */
    String getHQLQuery(final TypedQuery<?> query);


    /**
     * Returns default {@link Pageable} when no one is provided.
     *
     * @return {@link Pageable}
     */
    Pageable getDefaultPageable();


    /**
     *    Returns SQL {@code equals} operator based on provided {@code sourcePath} and {@code value}, only if {@code value}
     * is not empty.
     *
     * @param criteriaBuilder
     *    {@link CriteriaBuilder} used to build the {@code equals} clause
     * @param sourcePath
     *    {@link Expression} with the property to compare with given {@code value}
     * @param value
     *    {@link String} with the value to search in the returned SQL {@code equals} operator
     *
     * @return {@link Optional} with SQL {@code equals} operator if {@code value} is not empty,
     *         {@link Optional#empty()} otherwise
     */
    Optional<Predicate> buildEqualsIfNotEmpty(final CriteriaBuilder criteriaBuilder,
                                              final Expression<String> sourcePath,
                                              final String value);


    /**
     *    Returns SQL {@code in} operator based on provided {@code sourcePath} and {@code values}, only if {@code values}
     * is not empty.
     *
     * @param sourcePath
     *    {@link Expression} with the property to compare with given {@code value}
     * @param values
     *    {@link Collection} with the values to search in the returned SQL {@code in} operator
     *
     * @return {@link Optional} with SQL {@code in} operator if {@code values} is not empty,
     *         {@link Optional#empty()} otherwise
     */
    <E> Optional<Predicate> buildInIfNotEmpty(final Expression<E> sourcePath,
                                              final Collection<E> values);


    /**
     * Returns a {@link List} with the {@link Order} used in the {@code order by} clause of the query.
     *
     * @param criteriaBuilder
     *    {@link CriteriaBuilder} used to build the {@code order by} clause
     * @param sourcePathAndIsAsc
     *    {@link Tuple2} with: {@link Expression} with the property to use in the {@code order by} as {@code left},
     *    {@code true} if the ordination should be asc or {@code false} otherwise, as {@code right}
     *
     * @return {@link List} of {@link Order}
     */
    List<Order> buildOrdersBy(final CriteriaBuilder criteriaBuilder,
                              final Tuple2<Expression<String>, Boolean>... sourcePathAndIsAsc);


    /**
     * Returns the {@code order} clause of a SQL sentence, based on provided {@link Sort}.
     *
     * @param sort
     *    {@link Sort} used to create the SQL order clause
     *
     * @return {@link String} with SQL order clause
     */
    String buildRawOrder(final Sort sort);

}
