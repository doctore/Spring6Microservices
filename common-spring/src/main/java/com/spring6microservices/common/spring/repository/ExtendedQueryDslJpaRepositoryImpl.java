package com.spring6microservices.common.spring.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.io.Serializable;

import static com.spring6microservices.common.core.util.ExceptionUtil.getFormattedRootError;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Extended {@link ExtendedJpaRepositoryImpl} to include QueryDSL functionality we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@Log4j2
public class ExtendedQueryDslJpaRepositoryImpl<T, ID extends Serializable> extends ExtendedJpaRepositoryImpl<T, ID> implements ExtendedQueryDslJpaRepository<T, ID> {

    public ExtendedQueryDslJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation,
                                             final EntityManager entityManager) {
        super(
                entityInformation,
                entityManager
        );
    }


    @Override
    public String getHQLQuery(final JPAQuery<?> query) {
        return ofNullable(query)
                .map(q -> {
                    try {
                        return q.toString();

                    } catch (Exception e) {
                        log.debug(
                                format("The was an error getting the HQL equivalent. %s",
                                        getFormattedRootError(e)
                                ),
                                e
                        );
                        return null;
                    }
                })
                .orElse("");
    }


    @Override
    public String getNativeQuery(final JPASQLQuery<?> query) {
        return ofNullable(query)
                .map(q -> {
                    try {
                        return q.toString();

                    } catch (Exception e) {
                        log.debug(
                                format("The was an error getting the native SQL equivalent. %s",
                                        getFormattedRootError(e)
                                ),
                                e
                        );
                        return null;
                    }
                })
                .orElse("");
    }


    @Override
    public JPAQuery<T> getJPAQuery() {
        return new JPAQuery<>(entityManager);
    }


    @Override
    public JPASQLQuery<T> getJPASQLQuery() {
        return new JPASQLQuery<>(
                entityManager,
                getSQLTemplates()
        );
    }


    @Override
    public JPAQuery<T> selectFrom(final EntityPath<T> entityPath) {
        return getJPAQuery()
                .select(entityPath)
                .from(entityPath);
    }


    @Override
    public JPASQLQuery<T> nativeSelectFrom(final EntityPath<T> entityPath) {
        return getJPASQLQuery()
                .select(entityPath)
                .from(entityPath);
    }


    private SQLTemplates getSQLTemplates() {
        return PostgreSQLTemplates
                .builder()
                .printSchema()
                .build();
    }

}
