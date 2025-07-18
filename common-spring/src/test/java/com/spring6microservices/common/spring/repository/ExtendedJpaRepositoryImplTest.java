package com.spring6microservices.common.spring.repository;

import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.configuration.TestPersistenceConfiguration;
import com.spring6microservices.common.spring.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = TestPersistenceConfiguration.class)
@ExtendWith(SpringExtension.class)
public class ExtendedJpaRepositoryImplTest {

    @Autowired
    private EntityManager entityManager;

    @Mock
    JpaEntityInformation<User, Long> mockJpaEntityInformation;

    private ExtendedJpaRepository<User, Long> repository;


    @BeforeEach
    public void init() {
        when(mockJpaEntityInformation.getJavaType())
                .thenReturn(
                        User.class
                );
        when(mockJpaEntityInformation.getEntityName())
                .thenReturn(
                        User.class.getSimpleName()
                );

        repository = new ExtendedJpaRepositoryImpl<>(
                mockJpaEntityInformation,
                entityManager
        );
    }


    @Test
    @DisplayName("getEntityManager: when it is invoked then EntityManager is returned")
    public void getEntityManager_whenGetEntityManagerIsInvoked_thenEntityManagerIsReturned() {
        EntityManager result = repository.getEntityManager();

        assertNotNull(result);
        assertEquals(
                entityManager,
                result
        );
    }


    static Stream<Arguments> getHQLQueryWithQueryTestCases() {
        String query = "select u from User u";
        String expectedResult = format(
                "select u from %s u",
                User.class.getName()
        );
        return Stream.of(
                //@formatter:off
                //            rawQuery,   expectedResult
                Arguments.of( null,       StringUtil.EMPTY_STRING ),
                Arguments.of( query,      expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getHQLQueryWithQueryTestCases")
    @DisplayName("getHQLQuery: with Query as parameter test cases")
    public void getHQLQueryWithQuery_testCases(String rawQuery,
                                               String expectedResult) {
        Query query = null == rawQuery
                ? null
                : repository.getEntityManager()
                     .createQuery(rawQuery);

        assertEquals(
                expectedResult,
                repository.getHQLQuery(query)
        );
    }


    @Test
    @DisplayName("getHQLQuery: with TypedQuery as parameter, when null query is provided then empty string is returned")
    public void getHQLQueryWithTypedQuery_whenNullTypedQueryIsProvided_thenEmptyStringIsReturned() {
        assertEquals(
                StringUtil.EMPTY_STRING,
                repository.getHQLQuery((TypedQuery<User>) null)
        );
    }


    @Test
    @DisplayName("getHQLQuery: with TypedQuery as parameter, when valid query is provided then equivalent HQL is returned")
    public void getHQLQueryWithTypedQuery_whenValidTypedQueryIsProvided_thenEquivalentHQLIsReturned() {
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> user = criteriaQuery.from(User.class);
        criteriaQuery.select(user.alias("u"));   // Just to avoid autogenerated alias in the query
        List<Predicate> whereClause = List.of(
                criteriaBuilder.equal(
                        user.get("username"),
                        "test username"
                )
        );
        criteriaQuery.where(
                whereClause.toArray(new Predicate[0])
        );
        criteriaQuery.orderBy(
                criteriaBuilder.desc(
                        user.get("id")
                )
        );
        TypedQuery<User> query = repository.getEntityManager()
                .createQuery(criteriaQuery);

        String expectedResult = format(
                "select u as u "
                        + "from %s u "
                        + "where u.username = test username "
                        + "order by u.id desc nulls last",
                User.class.getName()
        );

        assertEquals(
                expectedResult,
                repository.getHQLQuery(query)
        );
    }


    @Test
    @DisplayName("getDefaultPageable: then default Pageable is returned")
    public void getDefaultPageable_thenDefaultPageableIsReturned() {
        Pageable result = repository.getDefaultPageable();

        assertNotNull(result);
        assertEquals(
                0,
                result.getPageNumber()
        );
        assertEquals(
                ExtendedJpaRepository.DEFAULT_PAGE_SIZE,
                result.getPageSize()
        );
        assertEquals(
                Sort.unsorted(),
                result.getSort()
        );
    }


    @Test
    @DisplayName("buildEqualsIfNotEmpty: when null or empty value is provided then empty Optional is returned")
    public void buildEqualsIfNotEmpty_whenNullOrEmptyValueIsProvided_thenEmptyOptionalIsReturned() {
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> user = criteriaQuery.from(User.class);

        assertTrue(
                repository.buildEqualsIfNotEmpty(
                        criteriaBuilder,
                        user.get("username"),
                        null
                ).isEmpty()
        );
        assertTrue(
                repository.buildEqualsIfNotEmpty(
                        criteriaBuilder,
                        user.get("username"),
                        "   "
                ).isEmpty()
        );
    }


    @Test
    @DisplayName("buildEqualsIfNotEmpty: when a not null or empty value is provided then not empty Optional is returned")
    public void buildEqualsIfNotEmpty_whenANotNullOrEmptyValueIsProvided_thenNotEmptyOptionalIsReturned() {
        String expectedQuery = format(
                "select u as u "
                        + "from %s u "
                        + "where u.username = test username",
                User.class.getName()
        );
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> user = criteriaQuery.from(User.class);
        criteriaQuery.select(user.alias("u"));   // Just to avoid autogenerated alias in the query

        Optional<Predicate> result = repository.buildEqualsIfNotEmpty(
                criteriaBuilder,
                user.get("username"),
                "test username"
        );
        assertTrue(result.isPresent());

        criteriaQuery.where(result.get());
        assertEquals(
                expectedQuery,
                repository.getHQLQuery(
                        repository.getEntityManager()
                                .createQuery(criteriaQuery)
                )
        );
    }


    @Test
    @DisplayName("buildInIfNotEmpty: when null or empty values is provided then empty Optional is returned")
    public void buildInIfNotEmpty_whenNullOrEmptyValueIsProvided_thenEmptyOptionalIsReturned() {
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> user = criteriaQuery.from(User.class);

        assertTrue(
                repository.buildInIfNotEmpty(
                        user.get("username"),
                        null
                ).isEmpty()
        );
        assertTrue(
                repository.buildInIfNotEmpty(
                        user.get("username"),
                        List.of()
                ).isEmpty()
        );
    }


    @Test
    @DisplayName("buildInIfNotEmpty: when a not null or empty values is provided then not empty Optional is returned")
    public void buildInIfNotEmpty_whenANotNullOrEmptyValuesIsProvided_thenNotEmptyOptionalIsReturned() {
        String expectedQuery = format(
                "select u as u "
                        + "from %s u "
                        + "where u.username in (test username1, test username 2)",
                User.class.getName()
        );
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> user = criteriaQuery.from(User.class);
        criteriaQuery.select(user.alias("u"));   // Just to avoid autogenerated alias in the query

        Optional<Predicate> result = repository.buildInIfNotEmpty(
                user.get("username"),
                List.of("test username1", "test username 2")
        );
        assertTrue(result.isPresent());

        criteriaQuery.where(result.get());
        assertEquals(
                expectedQuery,
                repository.getHQLQuery(
                        repository.getEntityManager()
                                .createQuery(criteriaQuery)
                )
        );
    }


    @Test
    @DisplayName("buildOrdersBy: when null sourcePathAndIsAsc is provided then empty List is returned")
    public void buildOrdersBy_whenNullSourcePathAndIsAscIsProvided_thenEmptyListIsReturned() {
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();

        assertTrue(
                repository.buildOrdersBy(
                        criteriaBuilder
                ).isEmpty()
        );
    }


    @Test
    @DisplayName("buildOrdersBy: when a not null sourcePathAndIsAsc is provided then not empty List is returned")
    public void buildOrdersBy_whenANotNullSourcePathAndIsAscIsProvided_thenNotEmptyListIsReturned() {
        String expectedQuery = format(
                "select u as u "
                        + "from %s u "
                        + "order by u.id asc nulls last, "
                        + "u.username desc nulls last",
                User.class.getName()
        );
        CriteriaBuilder criteriaBuilder = repository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> user = criteriaQuery.from(User.class);
        criteriaQuery.select(user.alias("u"));   // Just to avoid autogenerated alias in the query

        List<Order> result = repository.buildOrdersBy(
                criteriaBuilder,
                Tuple2.of(
                        user.get("id"),
                        true
                ),
                Tuple2.of(
                        user.get("username"),
                        false
                )
        );
        assertFalse(result.isEmpty());

        criteriaQuery.orderBy(result);
        assertEquals(
                expectedQuery,
                repository.getHQLQuery(
                        repository.getEntityManager()
                                .createQuery(criteriaQuery)
                )
        );
    }


    static Stream<Arguments> buildRawOrderTestCases() {
        Sort sortOneOrder = Sort.by(
                Sort.Direction.ASC,
                "id"
        );
        Sort sortSeveralOrders = Sort.by(
                Sort.Order.asc(
                        "id"
                ),
                Sort.Order.desc(
                        "description"
                )
        );
        String expectedResultSortOneOrder = "id ASC";
        String expectedResultSeveralOrders = "id ASC,description DESC";
        return Stream.of(
                //@formatter:off
                //            sort,                expectedResult
                Arguments.of( null,                StringUtil.EMPTY_STRING ),
                Arguments.of( sortOneOrder,        expectedResultSortOneOrder ),
                Arguments.of( sortSeveralOrders,   expectedResultSeveralOrders )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("buildRawOrderTestCases")
    @DisplayName("buildRawOrder: test cases")
    public void buildRawOrder_testCases(Sort sort,
                                        String expectedResult) {
        assertEquals(
                expectedResult,
                repository.buildRawOrder(sort)
        );
    }

}
