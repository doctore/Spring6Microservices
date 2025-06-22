package com.invoice.repository;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import com.invoice.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.*;
import static com.invoice.TestUtil.compareCustomers;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@DataJpaTest
@Import(
        PersistenceConfiguration.class
)
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        value = "classpath:db/invoice.sql"
)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;


    static Stream<Arguments> findAllNoMemoryPaginationTestCases() {
        Customer customer1 = buildExistingCustomer1InDatabase();
        Customer customer2 = buildExistingCustomer2InDatabase();

        PageRequest defaultSortPageable = PageRequest.of(
                0,
                5
        );
        PageRequest oldestPageable = PageRequest.of(
                0,
                1,
                Sort.by(
                        Sort.Direction.ASC,
                        Customer.ID_COLUMN
                )
        );

        Page<Customer> expectedResultNullPageable = new PageImpl<>(
                List.of(
                        customer1,
                        customer2
                )
        );
        Page<Customer> expectedResultDefaultSortPageable = new PageImpl<>(
                List.of(
                        customer2,
                        customer1
                ),
                defaultSortPageable,
                2
        );
        Page<Customer> expectedResultOldestPageable = new PageImpl<>(
                List.of(
                        customer1
                ),
                oldestPageable,
                2
        );
        return Stream.of(
                //@formatter:off
                //            pageable,              expectedResult
                Arguments.of( null,                  expectedResultNullPageable ),
                Arguments.of( defaultSortPageable,   expectedResultDefaultSortPageable ),
                Arguments.of( oldestPageable,        expectedResultOldestPageable )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findAllNoMemoryPaginationTestCases")
    @DisplayName("findAllNoMemoryPagination: test cases")
    public void findAllNoMemoryPagination_testCases(Pageable pageable,
                                                    Page<Customer> expectedResult) {
        Page<Customer> result = repository.findAllNoMemoryPagination(
                pageable
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.getTotalElements(),
                result.getTotalElements()
        );
        assertEquals(
                expectedResult.getNumberOfElements(),
                result.getNumberOfElements()
        );
        for (int i = 0; i < result.getContent().size(); i++) {
            compareCustomers(
                    result.getContent().get(i),
                    expectedResult.getContent().get(i)
            );
        }
    }


    static Stream<Arguments> findByCodeTestCases() {
        Customer customer = buildExistingCustomer1InDatabase();
        return Stream.of(
                //@formatter:off
                //            code,                 expectedResult
                Arguments.of( null,                 empty() ),
                Arguments.of( "NotFound",           empty() ),
                Arguments.of( customer.getCode(),   of(customer) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Optional<Customer> expectedResult) {
        Optional<Customer> result = repository.findByCode(
                code
        );
        assertNotNull(result);
        assertEquals(
                expectedResult.isEmpty(),
                result.isEmpty()
        );
        expectedResult.ifPresent(
                customer ->
                        compareCustomers(
                                result.get(),
                                customer
                        )
        );
    }

}
