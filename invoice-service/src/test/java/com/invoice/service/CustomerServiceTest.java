package com.invoice.service;

import com.invoice.TestDataFactory;
import com.invoice.model.Customer;
import com.invoice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestUtil.compareCustomers;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(
        SpringExtension.class
)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository mockRepository;

    private CustomerService service;


    @BeforeEach
    public void init() {
        service = new CustomerService(
                mockRepository
        );
    }


    static Stream<Arguments> findAllTestCases() {
        PageRequest pageable = PageRequest.of(
                0,
                5
        );

        PageImpl<Customer> emptyResult = new PageImpl<>(
                new ArrayList<>()
        );
        PageImpl<Customer> notEmptyResult = new PageImpl<>(
                List.of(
                        buildCustomer()
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
                                  Page<Customer> repositoryResult,
                                  Page<Customer> expectedResult) {

        when(mockRepository.findAllNoMemoryPagination(pageable))
                .thenReturn(repositoryResult);

        when(mockRepository.findAll())
                .thenReturn(repositoryResult.getContent());

        Page<Customer> result = service.findAll(
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
        Customer customer = buildCustomer();
        return Stream.of(
                //@formatter:off
                //            id,                   repositoryResult,   expectedResult
                Arguments.of( null,                 empty(),            empty() ),
                Arguments.of( "NotFound",           empty(),            empty() ),
                Arguments.of( customer.getCode(),   of(customer),       of(customer) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByCodeTestCases")
    @DisplayName("findByCode: test cases")
    public void findByCode_testCases(String code,
                                     Optional<Customer> repositoryResult,
                                     Optional<Customer> expectedResult) {
        when(mockRepository.findByCode(code))
                .thenReturn(repositoryResult);

        Optional<Customer> result = service.findByCode(
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
            compareCustomers(
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
        Customer customer = buildCustomer();
        return Stream.of(
                //@formatter:off
                //            id,                 repositoryResult,   expectedResult
                Arguments.of( null,               empty(),            empty() ),
                Arguments.of( 21,                 empty(),            empty() ),
                Arguments.of( customer.getId(),   of(customer),       of(customer) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByIdTestCases")
    @DisplayName("findById: test cases")
    public void findById_testCases(Integer id,
                                   Optional<Customer> repositoryResult,
                                   Optional<Customer> expectedResult) {
        when(mockRepository.findById(id))
                .thenReturn(repositoryResult);

        Optional<Customer> result = service.findById(
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
            compareCustomers(
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
        Customer customer = buildCustomer();
        return Stream.of(
                //@formatter:off
                //            customer,   repositoryResult,   expectedResult
                Arguments.of( null,       null,               empty() ),
                Arguments.of( customer,   null,               empty() ),
                Arguments.of( customer,   customer,           of(customer) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(Customer customer,
                               Customer repositoryResult,
                               Optional<Customer> expectedResult) {
        when(mockRepository.save(customer))
                .thenReturn(repositoryResult);

        Optional<Customer> result = service.save(
                customer
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
            compareCustomers(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockRepository, times(1))
                    .save(
                            customer
                    );
        }
    }


    private static Customer buildCustomer() {
        return TestDataFactory.buildCustomer(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
    }

}
