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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestUtil.compareCustomers;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
