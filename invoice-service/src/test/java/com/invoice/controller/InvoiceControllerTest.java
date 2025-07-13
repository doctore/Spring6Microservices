package com.invoice.controller;

import com.invoice.InvoiceServiceApplication;
import com.invoice.TestDataFactory;
import com.invoice.TestUtil;
import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.dto.CustomerDto;
import com.invoice.dto.InvoiceDto;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.invoice.service.InvoiceService;
import com.invoice.util.converter.InvoiceConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.page.PageDto;
import com.spring6microservices.common.spring.dto.page.SortDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.*;
import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(
        classes = InvoiceServiceApplication.class
)
public class InvoiceControllerTest extends BaseControllerTest {

    @MockitoBean
    private InvoiceConverter mockConverter;

    @MockitoBean
    private InvoiceService mockService;

    private WebTestClient webTestClient;


    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }


    @Test
    @DisplayName("create: when no logged user is given then unauthorized Http code is returned")
    public void create_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        InvoiceDto dto = buildNewInvoiceDto();

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT)
                .body(
                        Mono.just(dto),
                        InvoiceDto.class
                )
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("create: when no valid authority is given then forbidden Http code is returned")
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        InvoiceDto dto = buildNewInvoiceDto();

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT)
                .body(
                        Mono.just(dto),
                        InvoiceDto.class
                )
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    static Stream<Arguments> create_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(300, "a"));

        InvoiceDto dtoWithNoCode = buildNewInvoiceDto();
        dtoWithNoCode.setCode(null);

        InvoiceDto dtoWithLongCode = buildNewInvoiceDto();
        dtoWithLongCode.setCode(longString);

        InvoiceDto dtoWithNoCustomer = buildNewInvoiceDto();
        dtoWithNoCustomer.setCustomer(null);

        InvoiceDto dtoWithNoOrderId = buildNewInvoiceDto();
        dtoWithNoOrderId.setOrderId(null);

        InvoiceDto dtoWithNoCost = buildNewInvoiceDto();
        dtoWithNoCost.setCost(null);

        InvoiceDto dtoWithNegativeCost = buildNewInvoiceDto();
        dtoWithNegativeCost.setCost(-1d);

        ErrorResponseDto responseDtoWithNoCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'code' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'code' due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithNoCustomer= new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'customer' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithNoOrderId = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'orderId' due to: must not be null")
        );
        ErrorResponseDto responseDtoNoCost = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'cost' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithNegativeCost = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'cost' due to: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,           expectedResponse
                Arguments.of( dtoWithNoCode,         responseDtoWithNoCode ),
                Arguments.of( dtoWithLongCode,       responseDtoWithLongCode ),
                Arguments.of( dtoWithNoCustomer,     responseDtoWithNoCustomer ),
                Arguments.of( dtoWithNoOrderId,      responseDtoWithNoOrderId ),
                Arguments.of( dtoWithNoCost,         responseDtoNoCost ),
                Arguments.of( dtoWithNegativeCost,   responseDtoWithNegativeCost )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @MethodSource("create_invalidParametersTestCases")
    @DisplayName("create: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void create_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(InvoiceDto dtoToCreate,
                                                                                                                      ErrorResponseDto expectedResponse) {
        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT)
                .body(
                        Mono.just(dtoToCreate),
                        InvoiceDto.class
                )
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("create: when given parameters verifies validations but service returns empty then Http code Unprocessable Entity is returned")
    public void create_whenGivenParametersVerifiesValidationsButServiceReturnsEmpty_thenHttpCodeUnprocessableEntityIsReturned() {
        InvoiceDto dto = buildNewInvoiceDto();
        Invoice model = buildNewInvoice();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(model);

        when(mockService.save(model))
                .thenReturn(empty());

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT)
                .body(
                        Mono.just(dto),
                        InvoiceDto.class
                )
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody().isEmpty();

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        dto
                );
        verify(mockService, times(1))
                .save(
                        model
                );
        verify(mockConverter, never())
                .fromModelToDto(
                        any(Invoice.class)
                );
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("create: when given parameters verifies validations and service returns a model then Http code Created with the new Dto is returned")
    public void create_whenGivenParametersVerifiesValidationsAndServiceReturnAModel_thenHttpCodeCreatedWithTheNewDtoIsReturned() {
        InvoiceDto beforeDto = buildNewInvoiceDto();
        InvoiceDto afterDto = buildNewInvoiceDto();
        afterDto.setId(1);

        Invoice beforeModel = buildNewInvoice();
        Invoice afterModel = buildNewInvoice();
        afterModel.setId(
                afterDto.getId()
        );

        when(mockConverter.fromDtoToModel(beforeDto))
                .thenReturn(beforeModel);

        when(mockService.save(beforeModel))
                .thenReturn(of(afterModel));

        when(mockConverter.fromModelToDto(afterModel))
                .thenReturn(afterDto);

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT)
                .body(
                        Mono.just(beforeDto),
                        InvoiceDto.class
                )
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(InvoiceDto.class)
                .isEqualTo(afterDto);

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        beforeDto
                );
        verify(mockService, times(1))
                .save(
                        beforeModel
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        afterModel
                );
    }


    @Test
    @DisplayName("findAll: when no logged user is given then unauthorized Http code is returned")
    public void findAll_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        PageDto dto = buildPageDto();

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.FIND_ALL)
                .body(
                        Mono.just(dto),
                        PageDto.class
                )
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("findAll: when no valid authority is given then forbidden Http code is returned")
    public void findAll_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        PageDto dto = buildPageDto();

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.FIND_ALL)
                .body(
                        Mono.just(dto),
                        PageDto.class
                )
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    static Stream<Arguments> findAll_invalidParametersTestCases() {
        PageDto dtoWithNegativeSize = buildPageDto();
        dtoWithNegativeSize.setSize(-1);

        PageDto dtoWithNullPropertyToSort = buildPageDto();
        dtoWithNullPropertyToSort.getSort().getFirst().setProperty(null);

        PageDto dtoWithEmptyPropertyToSort = buildPageDto();
        dtoWithEmptyPropertyToSort.getSort().getFirst().setProperty("   ");

        ErrorResponseDto responseDtoWithNegativeSize = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'pageDto' on field 'size' due to: must be greater than 0")
        );
        ErrorResponseDto responseDtoWithNullPropertyToSort = new ErrorResponseDto(
                VALIDATION,
                List.of("There was an error in the provided information")
        );
        ErrorResponseDto responseDtoWithEmptyPropertyToSort = new ErrorResponseDto(
                VALIDATION,
                List.of("There was an error in the provided information")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,                  expectedResponse
                Arguments.of( dtoWithNegativeSize,          responseDtoWithNegativeSize ),
                Arguments.of( dtoWithNullPropertyToSort,    responseDtoWithNullPropertyToSort ),
                Arguments.of( dtoWithEmptyPropertyToSort,   responseDtoWithEmptyPropertyToSort )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @MethodSource("findAll_invalidParametersTestCases")
    @DisplayName("findAll: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findAll_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(PageDto dto,
                                                                                                                       ErrorResponseDto expectedResponse) {
        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.FIND_ALL)
                .body(
                        Mono.just(dto),
                        PageDto.class
                )
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findAll: when no results are found then empty page is returned")
    public void findAll_whenNoResultsAreFound_thenEmptyPageIsReturned() {
        PageDto pageDto = buildPageDto();

        when(mockService.findAll(pageDto.toPageable()))
                .thenReturn(
                        buildEmptyPage(pageDto.toPageable())
                );

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.FIND_ALL)
                .body(
                        Mono.just(pageDto),
                        PageDto.class
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isEqualTo(new ArrayList<>())
                .jsonPath("$.numberOfElements").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(pageDto.getSize())
                .jsonPath("$.totalPages").isEqualTo(0)
                .jsonPath("$.totalElements").isEqualTo(0);

        verify(mockService, times(1))
                .findAll(
                        pageDto.toPageable()
                );
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findAll: when results are found then expected page is returned")
    public void findAll_whenResultsAreFound_thenExpectedPageIsReturned() {
        PageDto pageDto = buildPageDto();
        InvoiceDto dto = buildInvoiceDto();
        Invoice model = buildInvoice();

        when(mockService.findAll(pageDto.toPageable()))
                .thenReturn(
                        buildPage(
                                pageDto.toPageable(),
                                List.of(model)
                        )
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(dto);

        webTestClient.post()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.FIND_ALL)
                .body(
                        Mono.just(pageDto),
                        PageDto.class
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.[0].id").isEqualTo(dto.getId())
                .jsonPath("$.content.[0].code").isEqualTo(dto.getCode())
                .jsonPath("$.content.[0].customer.id").isEqualTo(dto.getCustomer().getId())
                .jsonPath("$.content.[0].customer.code").isEqualTo(dto.getCustomer().getCode())
                .jsonPath("$.content.[0].customer.address").isEqualTo(dto.getCustomer().getAddress())
                .jsonPath("$.content.[0].customer.phone").isEqualTo(dto.getCustomer().getPhone())
                .jsonPath("$.content.[0].customer.email").isEqualTo(dto.getCustomer().getEmail())
                .jsonPath("$.content.[0].customer.createdAt").isEqualTo(TestUtil.localDateTimeToJSONFormat(dto.getCustomer().getCreatedAt()))
                .jsonPath("$.content.[0].orderId").isEqualTo(dto.getOrderId())
                .jsonPath("$.content.[0].cost").isEqualTo(dto.getCost())
                .jsonPath("$.content.[0].createdAt").isEqualTo(TestUtil.localDateTimeToJSONFormat(dto.getCreatedAt()))
                .jsonPath("$.content.[0].createdAt").isEqualTo(TestUtil.localDateTimeToJSONFormat(dto.getCreatedAt()))
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.size").isEqualTo(pageDto.getSize())
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.totalElements").isEqualTo(1);

        verify(mockService, times(1))
                .findAll(
                        pageDto.toPageable()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }



    // TODO: PENDING TO COMPLETE



    private static Invoice buildInvoice() {
        Invoice model = buildNewInvoice();
        model.setId(1);
        return model;
    }


    private static Invoice buildNewInvoice() {
        Customer customer = buildCustomer(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
        return TestDataFactory.buildInvoice(
                null,
                "Invoice 1",
                customer,
                1,
                10.1d
        );
    }


    private static InvoiceDto buildInvoiceDto() {
        InvoiceDto dto = buildNewInvoiceDto();
        dto.setId(1);
        return dto;
    }


    private static InvoiceDto buildNewInvoiceDto() {
        CustomerDto customer = buildCustomerDto(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
        return TestDataFactory.buildInvoiceDto(
                null,
                "Invoice 1",
                customer,
                1,
                10.1d
        );
    }


    private static PageDto buildPageDto() {
        List<SortDto> sortDtos = List.of(
                TestDataFactory.buildSortDto(
                        Invoice.ID_PROPERTY,
                        true
                )
        );
        return TestDataFactory.buildPageDto(
                0,
                10,
                sortDtos
        );
    }

}
