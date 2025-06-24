package com.spring6microservices.common.spring.repository;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.core.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.SqmVisitableNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedCurrentAndRootError;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Extended {@link SimpleJpaRepository} to include custom methods we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@Log4j2
public class ExtendedJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedJpaRepository<T, ID> {

    private final String QUERY_CLASS_PROXY = "Proxy";
    private final String QUERY_METHOD_SQL_CONVERSION = "getSqmStatement";

    protected EntityManager entityManager;

    public ExtendedJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation,
                                     final EntityManager entityManager) {
        super(
                entityInformation,
                entityManager
        );
        this.entityManager = entityManager;
    }


    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }


    @Override
    public String getHQLQuery(final Query query) {
        return ofNullable(query)
                .map(q -> {
                    try {
                        return q.getClass().getName().contains(QUERY_CLASS_PROXY)
                                ? ((SqmVisitableNode) q.getClass().getMethod(QUERY_METHOD_SQL_CONVERSION).invoke(q)).toHqlString()
                                : q.unwrap(QuerySqmImpl.class).getSqmStatement().toHqlString();

                    } catch (Exception e) {
                        log.debug(
                                format("The was an error getting the HQL equivalent. %s",
                                        getFormattedCurrentAndRootError(e)
                                ),
                                e
                        );
                        return null;
                    }
                })
                .orElse(StringUtil.EMPTY_STRING);
    }


    @Override
    public String getHQLQuery(final TypedQuery<?> query) {
        return getHQLQuery((Query) query);
    }


    @Override
    public Pageable getDefaultPageable() {
        return PageRequest.of(
                0,
                DEFAULT_PAGE_SIZE
        );
    }


    @Override
    public Optional<Predicate> buildEqualsIfNotEmpty(final CriteriaBuilder criteriaBuilder,
                                                     final Expression<String> sourcePath,
                                                     final String value) {
        return ofNullable(value)
                .filter(s -> !StringUtil.isBlank(s))
                .map(v ->
                        criteriaBuilder.equal(
                                sourcePath,
                                v
                        )
                );
    }


    @Override
    public <E> Optional<Predicate> buildInIfNotEmpty(final Expression<E> sourcePath,
                                                     final Collection<E> values) {
        return ofNullable(values)
                .filter(v -> !CollectionUtil.isEmpty(v))
                .map(v ->
                        sourcePath.in(
                                values
                        )
                );
    }


    @Override
    @SafeVarargs
    public final List<Order> buildOrdersBy(final CriteriaBuilder criteriaBuilder,
                                           final Tuple2<Expression<String>, Boolean>... sourcePathAndIsAsc) {
        return ofNullable(sourcePathAndIsAsc)
                .map(tuples ->
                        Arrays.stream(tuples)
                                .map(tuple ->
                                        tuple._2
                                                ? criteriaBuilder.asc(tuple._1)
                                                : criteriaBuilder.desc(tuple._1)
                                )
                                .collect(toList())
                )
                .orElseGet(ArrayList::new);
    }


    @Override
    public String buildRawOrder(final Sort sort) {
        return ofNullable(sort)
                .map(s ->
                                s.stream().map(
                                        o ->
                                                o.getProperty() + " " + o.getDirection().name()
                                        )
                                        .collect(
                                                joining(
                                                        RAW_SQL_SEPARATOR
                                                )
                                        )
                )
                .orElse(
                        StringUtil.EMPTY_STRING
                );
    }

}
