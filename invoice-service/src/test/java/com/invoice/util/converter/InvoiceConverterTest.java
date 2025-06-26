package com.invoice.util.converter;

import com.invoice.TestDataFactory;
import com.invoice.dto.CustomerDto;
import com.invoice.dto.InvoiceDto;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
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
                InvoiceConverterImpl.class,
                CustomerConverterImpl.class
        }
)
public class InvoiceConverterTest {

    @Autowired
    private InvoiceConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(
                converter.fromDtoToModel(null)
        );
    }


    static Stream<Arguments> fromDtoToModelWithDataTestCases() {
        InvoiceDto emptyDto = new InvoiceDto();
        InvoiceDto notEmptyDto = buildInvoiceDto();

        Invoice expectedResultEmptyDto = new Invoice();
        Invoice expectedResultNotEmptyDto = buildInvoice();
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
    public void fromDtoToModel_whenGivenDtoContainsData_thenEquivalentModelIsReturned(InvoiceDto dtoToConvert,
                                                                                      Invoice expectedResult) {
        Invoice result = converter.fromDtoToModel(
                dtoToConvert
        );

        if (null == expectedResult.getId()) {
            verifyEmptyInvoice(
                    result
            );
        }
        else {
            compareInvoices(
                    expectedResult,
                    result
            );
        }
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<Invoice> result = converter.fromDtoToOptionalModel(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    static Stream<Arguments> fromDtoToOptionalModelWithDataTestCases() {
        InvoiceDto emptyDto = new InvoiceDto();
        InvoiceDto notEmptyDto = buildInvoiceDto();

        Invoice expectedResultEmptyDto = new Invoice();
        Invoice expectedResultNotEmptyDto = buildInvoice();
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
    public void fromDtoToOptionalModel_whenGivenDtoContainsData_thenOptionalOfEquivalentModelIsReturned(InvoiceDto dtoToConvert,
                                                                                                        Optional<Invoice> expectedResult) {
        Optional<Invoice> result = converter.fromDtoToOptionalModel(
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
            verifyEmptyInvoice(
                    result.get()
            );
        }
        else {
            compareInvoices(
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
        List<InvoiceDto> dtosToConvert = List.of(
                buildInvoiceDto()
        );
        List<Invoice> expectedResult = List.of(
                buildInvoice()
        );

        List<Invoice> result = converter.fromDtosToModels(
                dtosToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareInvoices(
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
        Invoice emptyModel = new Invoice();
        Invoice notEmptyModel = buildInvoice();

        InvoiceDto expectedResultEmptyModel = new InvoiceDto();
        InvoiceDto expectedResultNotEmptyModel = buildInvoiceDto();
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
    public void fromModelToDto_whenGivenModelContainsData_thenEquivalentDtoIsReturned(Invoice modelToConvert,
                                                                                      InvoiceDto expectedResult) {
        InvoiceDto result = converter.fromModelToDto(
                modelToConvert
        );

        if (null == expectedResult.getId()) {
            verifyEmptyInvoiceDto(
                    result
            );
        }
        else {
            compareInvoiceDtos(
                    expectedResult,
                    result
            );
        }
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<InvoiceDto> result = converter.fromModelToOptionalDto(
                null
        );

        assertNotNull(result);
        assertTrue(
                result.isEmpty()
        );
    }


    static Stream<Arguments> fromModelToOptionalDtoWithDataTestCases() {
        Invoice emptyModel = new Invoice();
        Invoice notEmptyModel = buildInvoice();

        InvoiceDto expectedResultEmptyModel = new InvoiceDto();
        InvoiceDto expectedResultNotEmptyModel = buildInvoiceDto();
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
    public void fromModelToOptionalDto_whenGivenModelContainsData_thenOptionalOfEquivalentDtoIsReturned(Invoice modelToConvert,
                                                                                                        Optional<InvoiceDto> expectedResult) {
        Optional<InvoiceDto> result = converter.fromModelToOptionalDto(
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
            verifyEmptyInvoiceDto(
                    result.get()
            );
        }
        else {
            compareInvoiceDtos(
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
        List<Invoice> modelsToConvert = List.of(
                buildInvoice()
        );

        List<InvoiceDto> expectedResult = List.of(
                buildInvoiceDto()
        );

        List<InvoiceDto> result = converter.fromModelsToDtos(
                modelsToConvert
        );

        assertNotNull(result);
        assertEquals(
                expectedResult.size(),
                result.size()
        );
        for (int i = 0; i < result.size(); i++) {
            compareInvoiceDtos(
                    expectedResult.get(i),
                    result.get(i)
            );
        }
    }


    private static Invoice buildInvoice() {
        Customer customer = TestDataFactory.buildCustomer(
                1,
                "Customer 1",
                "Address 1",
                "Phone 1",
                "test@test.es"
        );
        return TestDataFactory.buildInvoice(
                1,
                "Invoice 1",
                customer,
                11,
                99d
        );
    }


    private static InvoiceDto buildInvoiceDto() {
        CustomerDto customer = TestDataFactory.buildCustomerDto(
                1,
                "Customer 1",
                "Address 1",
                "Phone 1",
                "test@test.es"
        );
        return TestDataFactory.buildInvoiceDto(
                1,
                "Invoice 1",
                customer,
                11,
                99d
        );
    }

}
