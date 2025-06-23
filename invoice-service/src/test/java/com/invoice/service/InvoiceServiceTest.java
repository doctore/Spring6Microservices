package com.invoice.service;

import com.invoice.TestDataFactory;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.buildCustomer;
import static com.invoice.TestUtil.compareInvoices;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository mockRepository;

    private InvoiceService service;


    @BeforeEach
    public void init() {
        service = new InvoiceService(
                mockRepository
        );
    }


    static Stream<Arguments> findByCodeTestCases() {
        Invoice invoice = buildInvoice();
        return Stream.of(
                //@formatter:off
                //            id,                  repositoryResult,   expectedResult
                Arguments.of( null,                empty(),            empty() ),
                Arguments.of( "NotFound",          empty(),            empty() ),
                Arguments.of( invoice.getCode(),   of(invoice),        of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Optional<Invoice> repositoryResult,
                                     Optional<Invoice> expectedResult) {
        when(mockRepository.findByCode(code))
                .thenReturn(repositoryResult);

        Optional<Invoice> result = service.findByCode(
                code
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareInvoices(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockRepository, times(1))
                    .findByCode(
                            code
                    );
        }
    }


    static Stream<Arguments> findByIdTestCases() {
        Invoice invoice = buildInvoice();
        return Stream.of(
                //@formatter:off
                //            id,                repositoryResult,   expectedResult
                Arguments.of( null,              empty(),            empty() ),
                Arguments.of( 21,                empty(),            empty() ),
                Arguments.of( invoice.getId(),   of(invoice),        of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Optional<Invoice> repositoryResult,
                                   Optional<Invoice> expectedResult) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);

        Optional<Invoice> result = service.findById(
                id
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareInvoices(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockRepository, times(1))
                    .findById(
                            id
                    );
        }
    }


    static Stream<Arguments> saveTestCases() {
        Invoice invoice = buildInvoice();
        return Stream.of(
                //@formatter:off
                //            invoice,   repositoryResult,   expectedResult
                Arguments.of( null,      null,               empty() ),
                Arguments.of( invoice,   null,               empty() ),
                Arguments.of( invoice,   invoice,            of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(Invoice invoice,
                               Invoice repositoryResult,
                               Optional<Invoice> expectedResult) {
        when(mockRepository.save(invoice))
                .thenReturn(repositoryResult);

        Optional<Invoice> result = service.save(
                invoice
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareInvoices(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockRepository, times(1))
                    .save(
                            invoice
                    );
        }
    }


    private static Invoice buildInvoice() {
        Customer customer = buildCustomer(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
        return TestDataFactory.buildInvoice(
                1,
                "Invoice 1",
                customer,
                1,
                10.1d
        );
    }

}
