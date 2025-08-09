package com.invoice.repository;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import com.invoice.model.Invoice;
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

import static com.invoice.TestDataFactory.buildExistingInvoice1InDatabase;
import static com.invoice.TestDataFactory.buildExistingInvoice2InDatabase;
import static com.invoice.TestUtil.compareInvoices;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;

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
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository repository;


    static Stream<Arguments> findAllNoMemoryPaginationTestCases() {
        Invoice invoice1 = buildExistingInvoice1InDatabase();
        Invoice invoice2 = buildExistingInvoice2InDatabase();

        PageRequest defaultSortPageable = PageRequest.of(
                0,
                5
        );
        PageRequest mostExpensivePageable = PageRequest.of(
                0,
                1,
                Sort.by(
                        Sort.Direction.DESC,
                        Invoice.COST_COLUMN
                )
        );

        Page<Invoice> expectedResultNullPageable = new PageImpl<>(
                List.of(
                        invoice2,
                        invoice1
                )
        );
        Page<Invoice> expectedResultDefaultSortPageable = new PageImpl<>(
                List.of(
                        invoice2,
                        invoice1
                ),
                defaultSortPageable,
                2
        );
        Page<Invoice> expectedResultMostExpensivePageable = new PageImpl<>(
                List.of(
                        invoice2
                ),
                mostExpensivePageable,
                2
        );
        return Stream.of(
                //@formatter:off
                //            pageable,                expectedResult
                Arguments.of( null,                    expectedResultNullPageable ),
                Arguments.of( defaultSortPageable,     expectedResultDefaultSortPageable ),
                Arguments.of( mostExpensivePageable,   expectedResultMostExpensivePageable )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findAllNoMemoryPaginationTestCases")
    @DisplayName("findAllNoMemoryPagination: test cases")
    public void findAllNoMemoryPagination_testCases(Pageable pageable,
                                                    Page<Invoice> expectedResult) {
        Page<Invoice> result = repository.findAllNoMemoryPagination(
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
            compareInvoices(
                    result.getContent().get(i),
                    expectedResult.getContent().get(i)
            );
        }
    }


    static Stream<Arguments> findByCodeTestCases() {
        Invoice invoice = buildExistingInvoice1InDatabase();
        return Stream.of(
                //@formatter:off
                //            code,                expectedResult
                Arguments.of( null,                empty() ),
                Arguments.of( "NotFound",          empty() ),
                Arguments.of( invoice.getCode(),   of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Optional<Invoice> expectedResult) {
        Optional<Invoice> result = repository.findByCode(
                code
        );
        assertNotNull(result);
        assertEquals(
                expectedResult.isEmpty(),
                result.isEmpty()
        );
        expectedResult.ifPresent(
                invoice ->
                        compareInvoices(
                                result.get(),
                                invoice
                        )
        );
    }


    static Stream<Arguments> findByCostRangeTestCases() {
        Invoice invoice1 = buildExistingInvoice1InDatabase();
        Invoice invoice2 = buildExistingInvoice2InDatabase();
        return Stream.of(
                //@formatter:off
                //            costGreaterOrEqual,   costLessOrEqual,   expectedResult
                Arguments.of( null,                 null,              List.of(invoice1, invoice2) ),
                Arguments.of( 12d,                  null,              List.of(invoice2) ),
                Arguments.of( null,                 12d,               List.of(invoice1) ),
                Arguments.of( 0d,                   10d,               List.of() ),
                Arguments.of( 10d,                  20d,               List.of(invoice1) ),
                Arguments.of( 10d,                  1000d,             List.of(invoice1, invoice2) ),
                Arguments.of( 1000d,                2000d,             List.of() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCostRangeTestCases")
    @DisplayName("findByCostRange: test cases")
    public void findByCostRange_testCases(Double costGreaterOrEqual,
                                          Double costLessOrEqual,
                                          List<Invoice> expectedResult) {
        List<Invoice> result = repository.findByCostRange(
                costGreaterOrEqual,
                costLessOrEqual
        );
        if (expectedResult.isEmpty()) {
            assertNotNull(result);
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertNotNull(result);
            assertEquals(
                    expectedResult.size(),
                    result.size()
            );
            for (int i = 0; i < result.size(); i++) {
                compareInvoices(
                        result.get(i),
                        expectedResult.get(i)
                );
            }
        }
    }


    static Stream<Arguments> findByOrderIdTestCases() {
        Invoice invoice = buildExistingInvoice1InDatabase();
        return Stream.of(
                //@formatter:off
                //            orderId,                expectedResult
                Arguments.of( null,                   empty() ),
                Arguments.of( -12,                    empty() ),
                Arguments.of( invoice.getOrderId(),   of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByOrderIdTestCases")
    @DisplayName("findByOrderId: test cases")
    public void findByOrderId_testCases(Integer orderId,
                                        Optional<Invoice> expectedResult) {
        Optional<Invoice> result = repository.findByOrderId(
                orderId
        );
        assertNotNull(result);
        assertEquals(
                expectedResult.isEmpty(),
                result.isEmpty()
        );
        expectedResult.ifPresent(
                invoice ->
                        compareInvoices(
                                result.get(),
                                invoice
                        )
        );
    }

}
