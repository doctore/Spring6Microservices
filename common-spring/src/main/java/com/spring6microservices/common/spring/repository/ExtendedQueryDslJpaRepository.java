package com.spring6microservices.common.spring.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Extended {@link ExtendedJpaRepository} to include QueryDSL functionality we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@NoRepositoryBean
public interface ExtendedQueryDslJpaRepository<T, ID extends Serializable> extends ExtendedJpaRepository<T, ID> {

    /**
     * Returns the HQL representation of the internal query of the given {@link JPAQuery}.
     *
     * @param query
     *    {@link JPAQuery} to get its SQL query
     *
     * @return {@link String} with the HQL representation of the internal query,
     *         empty {@link String} is there is any error getting it.
     */
    String getHQLQuery(final JPAQuery<?> query);


    /**
     * Returns the native SQL of the internal query of the given {@link JPASQLQuery}.
     *
     * @param query
     *    {@link JPASQLQuery} to get its SQL query
     *
     * @return {@link String} with the native SQL representation of the internal query,
     *         empty {@link String} is there is any error getting it.
     */
    String getNativeQuery(final JPASQLQuery<?> query);


    /**
     * Used to get {@link JPAQuery} instance and create custom JPA queries.
     *
     * @return {@link JPAQuery}
     */
    JPAQuery<T> getJPAQuery();


    /**
     * Used to get {@link JPASQLQuery} instance and create custom JPA native queries.
     *
     * @return {@link JPASQLQuery}
     */
    JPASQLQuery<T> getJPASQLQuery();


    /**
     * Generates a {@link JPAQuery} object with a {@code select} and {@code from} using the provided {@code entityPath}.
     *
     * @param entityPath
     *    {@link EntityPath} used in {@code select} and {@code from} clauses.
     *
     * @return @return {@link JPAQuery}
     */
    JPAQuery<T> selectFrom(final EntityPath<T> entityPath);


    /**
     * Generates a {@link JPASQLQuery} object with a {@code select} and {@code from} using the provided {@code entityPath}.
     *
     * @param entityPath
     *    {@link EntityPath} used in {@code select} and {@code from} clauses.
     *
     * @return @return {@link JPASQLQuery}
     */
    JPASQLQuery<T> nativeSelectFrom(final EntityPath<T> entityPath);

}