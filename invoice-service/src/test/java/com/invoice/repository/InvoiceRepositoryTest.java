package com.invoice.repository;

import com.invoice.TestDataFactory;
import com.invoice.configuration.persistence.PersistenceConfiguration;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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


    static Stream<Arguments> findByCostRangeTestCases() {
        Customer customer = TestDataFactory.buildCustomer(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
        Invoice invoice1 = TestDataFactory.buildInvoice(
                1,
                "Invoice 1",
                customer,
                1,
                10.1d
        );
        Invoice invoice2 = TestDataFactory.buildInvoice(
                2,
                "Invoice 2",
                customer,
                2,
                911.5
        );
        return Stream.of(
                //@formatter:off
                //            costGreaterOrEqual,   costLessOrEqual,   expectedResult
                Arguments.of( 0,                    10,                List.of() ),
                Arguments.of( 10,                   20,                List.of(invoice1) ),
                Arguments.of( 10,                   1000,              List.of(invoice1, invoice2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCostRangeTestCases")
    @DisplayName("findById: test cases")
    public void findByCostRange_testCases(double costGreaterOrEqual,
                                          double costLessOrEqual,
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
                assertThat(
                        result.get(i),
                        samePropertyValuesAs(
                                expectedResult.get(i),
                                "createdAt",
                                "customer"
                        )
                );
            }
        }
    }

}
