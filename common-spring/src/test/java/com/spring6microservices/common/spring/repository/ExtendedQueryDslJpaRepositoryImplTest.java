package com.spring6microservices.common.spring.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.resources.configuration.TestPersistenceConfiguration;
import com.spring6microservices.common.spring.resources.data.QUser;
import com.spring6microservices.common.spring.resources.data.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = TestPersistenceConfiguration.class)
@ExtendWith(SpringExtension.class)
public class ExtendedQueryDslJpaRepositoryImplTest {

    @Autowired
    private EntityManager entityManager;

    @Mock
    JpaEntityInformation<User, Long> mockJpaEntityInformation;

    private ExtendedQueryDslJpaRepository<User, Long> repository;


    @BeforeEach
    public void init() {
        when(mockJpaEntityInformation.getJavaType())
                .thenReturn(User.class);
        when(mockJpaEntityInformation.getEntityName())
                .thenReturn(User.class.getSimpleName());

        repository = new ExtendedQueryDslJpaRepositoryImpl<>(
                mockJpaEntityInformation,
                entityManager
        );
    }


    @Test
    @DisplayName("getHQLQuery: with JPAQuery as parameter, when null query is provided then empty string is returned")
    public void getHQLQueryWithJPAQuery_whenNullJPAQueryIsProvided_thenEmptyStringIsReturned() {
        assertEquals(
                StringUtil.EMPTY_STRING,
                repository.getHQLQuery((JPAQuery<User>) null)
        );
    }


    @Test
    @DisplayName("getHQLQuery: with JPAQuery as parameter, when valid query is provided then equivalent HQL is returned")
    public void getHQLQueryWithJPAQuery_whenValidJPAQueryIsProvided_thenEquivalentHQLIsReturned() {
        QUser user = QUser.user;
        JPAQuery<Tuple> query = repository.getJPAQuery()
                .select(
                        user.id,
                        user.username
                )
                .from(user)
                .where(user.id.gt(10));

        String expectedResult = format(
                        """
                        select user.id, user.username
                        from %s user
                        where user.id > ?1""",
                User.class.getSimpleName()
        );

        assertEquals(
                expectedResult,
                repository.getHQLQuery(query)
        );
    }


    @Test
    @DisplayName("getNativeQuery: with JPASQLQuery as parameter, when null query is provided then empty string is returned")
    public void getNativeQueryWithJPASQLQuery_whenNullJPASQLQueryIsProvided_thenEmptyStringIsReturned() {
        assertEquals(
                StringUtil.EMPTY_STRING,
                repository.getNativeQuery(null)
        );
    }


    @Test
    @DisplayName("getNativeQuery: with JPASQLQuery as parameter, when valid query is provided then equivalent HQL is returned")
    public void getNativeQueryWithJPASQLQuery_whenValidJPASQLQueryIsProvided_thenEquivalentHQLIsReturned() {
        QUser user = QUser.user;
        JPASQLQuery<Tuple> query = repository.getJPASQLQuery()
                .select(
                        user.id,
                        user.username
                )
                .from(user)
                .where(user.id.gt(10));

        String expectedResult =
                """
                select "user".id, "user".username
                from  "user"
                where "user".id > ?1""";

        assertEquals(
                expectedResult,
                repository.getNativeQuery(query)
        );
    }


    @Test
    @DisplayName("selectFrom: when entityPath is provided then equivalent select-from clause is returned")
    public void selectFrom_whenEntityPathIsProvided_thenEquivalentSelectFromClauseReturned() {
        JPAQuery<User> query = repository.selectFrom(
                QUser.user
        );

        String expectedResult = format(
                """
                select user
                from %s user""",
                User.class.getSimpleName()
        );

        assertEquals(
                expectedResult,
                repository.getHQLQuery(query)
        );
    }


    @Test
    @DisplayName("nativeSelectFrom: when entityPath is provided then equivalent select-from clause is returned")
    public void nativeSelectFrom_whenEntityPathIsProvided_thenEquivalentSelectFromClauseReturned() {
        JPASQLQuery<User> query = repository.nativeSelectFrom(
                QUser.user
        );

        String expectedResult =
                """
                select {"user".*}
                from  "user\"""";

        assertEquals(
                expectedResult,
                repository.getNativeQuery(query)
        );
    }

}
