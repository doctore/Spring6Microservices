package com.invoice.service;

import com.invoice.TestDataFactory;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.buildCustomer;
import static com.invoice.TestUtil.compareInvoices;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
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


    @Test
    @DisplayName("count: then repository result is returned")
    public void count_thenRepositoryResultIsReturned() {
        long expectedResult = 12;

        when(mockRepository.count())
                .thenReturn(
                        expectedResult
                );

        assertEquals(
                expectedResult,
                service.count()
        );
    }


    static Stream<Arguments> findAllTestCases() {
        PageRequest pageable = PageRequest.of(
                0,
                5
        );

        PageImpl<Invoice> emptyResult = new PageImpl<>(
                new ArrayList<>()
        );
        PageImpl<Invoice> notEmptyResult = new PageImpl<>(
                List.of(
                        buildInvoice()
                )
        );
        return Stream.of(
                //@formatter:off
                //            pageable,   repositoryResult,   expectedResult
                Arguments.of( null,       emptyResult,        emptyResult ),
                Arguments.of( null,       notEmptyResult,     notEmptyResult ),
                Arguments.of( pageable,   emptyResult,        emptyResult ),
                Arguments.of( pageable,   notEmptyResult,     notEmptyResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findAllTestCases")
    @DisplayName("findAll: test cases")
    public void findAll_testCases(Pageable pageable,
                                  Page<Invoice> repositoryResult,
                                  Page<Invoice> expectedResult) {

        when(mockRepository.findAllNoMemoryPagination(pageable))
                .thenReturn(
                        repositoryResult
                );
        when(mockRepository.findAll())
                .thenReturn(
                        repositoryResult.getContent()
                );

        Page<Invoice> result = service.findAll(
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

        if (null == pageable) {
            verify(mockRepository, times(1))
                    .findAll();
        }
        else {
            verify(mockRepository, times(1))
                    .findAllNoMemoryPagination(
                            pageable
                    );
        }
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
                .thenReturn(
                        repositoryResult
                );

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
                .thenReturn(
                        repositoryResult
                );

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
                .thenReturn(
                        repositoryResult
                );

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


    static Stream<Arguments> saveAllTestCases() {
        Invoice invoice1 = buildInvoice();
        Invoice invoice2 = TestDataFactory.buildInvoice(
                2,
                "Invoice 2",
                invoice1.getCustomer(),
                2,
                19d
        );;
        List<Invoice> allInvoices = List.of(
                invoice1,
                invoice2
        );
        return Stream.of(
                //@formatter:off
                //            invoices,            repositoryResult,    expectedResult
                Arguments.of( null,                null,                List.of() ),
                Arguments.of( null,                List.of(),           List.of() ),
                Arguments.of( List.of(invoice1),   List.of(),           List.of() ),
                Arguments.of( List.of(invoice1),   List.of(invoice1),   List.of(invoice1) ),
                Arguments.of( allInvoices,         allInvoices,         allInvoices )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveAllTestCases")
    @DisplayName("saveAll: test cases")
    public void saveAll_testCases(Collection<Invoice> invoices,
                                  List<Invoice> repositoryResult,
                                  List<Invoice> expectedResult) {
        when(mockRepository.saveAll(invoices))
                .thenReturn(
                        repositoryResult
                );

        List<Invoice> result = service.saveAll(
                invoices
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
        }
        else {
            for (int i = 0; i < expectedResult.size(); i++) {
                compareInvoices(
                        expectedResult.get(i),
                        result.get(i)
                );
            }
            verify(mockRepository, times(1))
                    .saveAll(
                            invoices
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
