package com.invoice.util.converter;

import com.invoice.TestDataFactory;
import com.invoice.dto.CustomerDto;
import com.invoice.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestUtil.*;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                CustomerConverterImpl.class
        }
)
public class CustomerConverterTest {

    @Autowired
    private CustomerConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(
                converter.fromDtoToModel(null)
        );
    }


    static Stream<Arguments> fromDtoToModelWithDataTestCases() {
        CustomerDto emptyDto = new CustomerDto();
        CustomerDto notEmptyDto = buildCustomerDto();

        Customer expectedResultEmptyDto = new Customer();
        Customer expectedResultNotEmptyDto = buildCustomer();
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,   expectedResult
                Arguments.of( emptyDto,       expectedResultEmptyDto ),
                Arguments.of( notEmptyDto,    expectedResultNotEmptyDto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelWithDataTestCases")
    @DisplayName("fromDtoToModel: when the given dto contains data then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoContainsData_thenEquivalentModelIsReturned(CustomerDto dtoToConvert,
                                                                                      Customer expectedResult) {
        Customer result = converter.fromDtoToModel(
                dtoToConvert
        );

        if (null == expectedResult.getId()) {
            verifyEmptyCustomer(
                    result
            );
        }
        else {
            compareCustomers(
                    expectedResult,
                    result
            );
        }
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<Customer> result = converter.fromDtoToOptionalModel(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    static Stream<Arguments> fromDtoToOptionalModelWithDataTestCases() {
        CustomerDto emptyDto = new CustomerDto();
        CustomerDto notEmptyDto = buildCustomerDto();

        Customer expectedResultEmptyDto = new Customer();
        Customer expectedResultNotEmptyDto = buildCustomer();
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,   expectedResult
                Arguments.of( emptyDto,       of(expectedResultEmptyDto) ),
                Arguments.of( notEmptyDto,    of(expectedResultNotEmptyDto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToOptionalModelWithDataTestCases")
    @DisplayName("fromDtoToOptionalModel: when the given dto contains data then then Optional with equivalent model is returned")
    public void fromDtoToOptionalModel_whenGivenDtoContainsData_thenOptionalOfEquivalentModelIsReturned(CustomerDto dtoToConvert,
                                                                                                        Optional<Customer> expectedResult) {
        Optional<Customer> result = converter.fromDtoToOptionalModel(
                dtoToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertTrue(
                expectedResult.isPresent()
        );
        if (null == expectedResult.get().getId()) {
            verifyEmptyCustomer(
                    result.get()
            );
        }
        else {
            compareCustomers(
                    expectedResult.get(),
                    result.get()
            );
        }
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is null then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromDtosToModels(null)
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is empty then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromDtosToModels(List.of())
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when the given collection contains data then a List of equivalent models is returned")
    public void fromDtosToModels_whenGivenCollectionContainsData_thenEquivalentCollectionModelsIsReturned() {
        List<CustomerDto> dtosToConvert = List.of(
                buildCustomerDto()
        );
        List<Customer> expectedResult = List.of(
                buildCustomer()
        );

        List<Customer> result = converter.fromDtosToModels(
                dtosToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareCustomers(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }


    @Test
    @DisplayName("fromModelToDto: when given model is null then null dto is returned")
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        assertNull(
                converter.fromModelToDto(null)
        );
    }


    static Stream<Arguments> fromModelToDtoWithDataTestCases() {
        Customer emptyModel = new Customer();
        Customer notEmptyModel = buildCustomer();

        CustomerDto expectedResultEmptyModel = new CustomerDto();
        CustomerDto expectedResultNotEmptyModel = buildCustomerDto();
        return Stream.of(
                //@formatter:off
                //            modelToConvert,   expectedResult
                Arguments.of( emptyModel,       expectedResultEmptyModel ),
                Arguments.of( notEmptyModel,    expectedResultNotEmptyModel )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoWithDataTestCases")
    @DisplayName("fromModelToDto: when the given model contains data then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(Customer modelToConvert,
                                                                                      CustomerDto expectedResult) {
        CustomerDto result = converter.fromModelToDto(
                modelToConvert
        );

        if (null == expectedResult.getId()) {
            verifyEmptyCustomerDto(
                    result
            );
        }
        else {
            compareCustomerDtos(
                    expectedResult,
                    result
            );
        }
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<CustomerDto> result = converter.fromModelToOptionalDto(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    static Stream<Arguments> fromModelToOptionalDtoWithDataTestCases() {
        Customer emptyModel = new Customer();
        Customer notEmptyModel = buildCustomer();

        CustomerDto expectedResultEmptyModel = new CustomerDto();
        CustomerDto expectedResultNotEmptyModel = buildCustomerDto();
        return Stream.of(
                //@formatter:off
                //            modelToConvert,   expectedResult
                Arguments.of( emptyModel,       of(expectedResultEmptyModel) ),
                Arguments.of( notEmptyModel,    of(expectedResultNotEmptyModel) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToOptionalDtoWithDataTestCases")
    @DisplayName("fromModelToOptionalDto: when the given model contains data then then Optional with equivalent dto is returned")
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(Customer modelToConvert,
                                                                                                        Optional<CustomerDto> expectedResult) {
        Optional<CustomerDto> result = converter.fromModelToOptionalDto(
                modelToConvert
        );

        assertNotNull(result);
        assertTrue(
                result.isPresent()
        );
        assertTrue(
                expectedResult.isPresent()
        );
        if (null == expectedResult.get().getId()) {
            verifyEmptyCustomerDto(
                    result.get()
            );
        }
        else {
            compareCustomerDtos(
                    expectedResult.get(),
                    result.get()
            );
        }
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromModelsToDtos(null)
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is empty then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        assertTrue(
                converter.fromModelsToDtos(List.of())
                        .isEmpty()
        );
    }


    @Test
    @DisplayName("fromModelsToDtos: when the given collection contains data then a List of equivalent dtos is returned")
    public void fromModelsToDtos_whenGivenCollectionContainsData_thenEquivalentCollectionDtosIsIsReturned() {
        List<Customer> modelsToConvert = List.of(
                buildCustomer()
        );

        List<CustomerDto> expectedResult = List.of(
                buildCustomerDto()
        );

        List<CustomerDto> result = converter.fromModelsToDtos(
                modelsToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareCustomerDtos(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }


    private static Customer buildCustomer() {
        return TestDataFactory.buildCustomer(
                1,
                "Customer 1",
                "Address 1",
                "Phone 1",
                "test@test.es"
        );
    }


    private static CustomerDto buildCustomerDto() {
        return TestDataFactory.buildCustomerDto(
                1,
                "Customer 1",
                "Address 1",
                "Phone 1",
                "test@test.es"
        );
    }

}
