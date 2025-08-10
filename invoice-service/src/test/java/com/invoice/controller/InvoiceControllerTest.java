package com.invoice.controller;

import com.invoice.InvoiceServiceApplication;
import com.invoice.TestDataFactory;
import com.invoice.TestUtil;
import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.invoice.service.InvoiceService;
import com.invoice.service.OrderService;
import com.invoice.util.converter.InvoiceConverter;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.invoice.CustomerDto;
import com.spring6microservices.common.spring.dto.invoice.InvoiceDto;
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

    @MockitoBean
    private OrderService mockOrderService;

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

        InvoiceDto dtoWithNoOrder = buildNewInvoiceDto();
        dtoWithNoOrder.setOrder(null);

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
        ErrorResponseDto responseDtoWithNoOrder = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'invoiceDto' on field 'order' due to: must not be null")
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
                Arguments.of( dtoWithNoOrder,        responseDtoWithNoOrder ),
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
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        empty()
                );

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
                .thenReturn(
                        beforeModel
                );
        when(mockService.save(beforeModel))
                .thenReturn(
                        of(afterModel)
                );
        when(mockConverter.fromModelToDto(afterModel))
                .thenReturn(
                        afterDto
                );

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
                .thenReturn(
                        dto
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
                .jsonPath("$.content.[0].id").isEqualTo(dto.getId())
                .jsonPath("$.content.[0].code").isEqualTo(dto.getCode())
                .jsonPath("$.content.[0].customer.id").isEqualTo(dto.getCustomer().getId())
                .jsonPath("$.content.[0].customer.code").isEqualTo(dto.getCustomer().getCode())
                .jsonPath("$.content.[0].customer.address").isEqualTo(dto.getCustomer().getAddress())
                .jsonPath("$.content.[0].customer.phone").isEqualTo(dto.getCustomer().getPhone())
                .jsonPath("$.content.[0].customer.email").isEqualTo(dto.getCustomer().getEmail())
                .jsonPath("$.content.[0].customer.createdAt").isEqualTo(TestUtil.localDateTimeToJSONFormat(dto.getCustomer().getCreatedAt()))
                .jsonPath("$.content.[0].order.id").isEqualTo(dto.getOrder().getId())
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


    @Test
    @DisplayName("findByCode: when no logged user is given then unauthorized Http code is returned")
    public void findByCode_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        String code = StringUtil.urlEncode(
                "Invoice 1"
        );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("findByCode: when no valid authority is given then forbidden Http code is returned")
    public void findByCode_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        String code = StringUtil.urlEncode(
                "Invoice 1"
        );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        String code = StringUtil.urlEncode(
                "Invoice 1"
        );

        when(mockService.findByCode(code))
                .thenReturn(
                        empty()
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(mockService, times(1))
                .findByCode(
                        code
                );
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        InvoiceDto dto = buildInvoiceDto();
        Invoice model = buildInvoice();

        when(mockService.findByCode(dto.getCode()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );
        when(mockOrderService.findById(dto.getOrder().getId()))
                .thenReturn(
                        of(dto.getOrder())
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_CODE + "/" + dto.getCode())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(InvoiceDto.class)
                .isEqualTo(dto);

        verify(mockService, times(1))
                .findByCode(
                        dto.getCode()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
        verify(mockOrderService, times(1))
                .findById(
                        dto.getOrder().getId()
                );
    }


    @Test
    @DisplayName("findById: when no logged user is given then unauthorized Http code is returned")
    public void findById_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("findById: when no valid authority is given then forbidden Http code is returned")
    public void findById_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    static Stream<Arguments> findById_invalidParametersTestCases() {
        ErrorResponseDto responseInvalidOrderId = new ErrorResponseDto(
                VALIDATION,
                List.of("id: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            id,   expectedResponse
                Arguments.of( -1,   responseInvalidOrderId ),
                Arguments.of( 0,    responseInvalidOrderId )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @MethodSource("findById_invalidParametersTestCases")
    @DisplayName("findById: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findById_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(Integer id,
                                                                                                                        ErrorResponseDto expectedResponse) {
        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findById: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        Integer id = 1;

        when(mockService.findById(id))
                .thenReturn(
                        empty()
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(mockService, times(1))
                .findById(
                        id
                );
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findById: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        InvoiceDto dto = buildInvoiceDto();
        Invoice model = buildInvoice();

        when(mockService.findById(dto.getId()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );
        when(mockOrderService.findById(dto.getOrder().getId()))
                .thenReturn(
                        of(dto.getOrder())
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ID + "/" + dto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(InvoiceDto.class)
                .isEqualTo(dto);

        verify(mockService, times(1))
                .findById(
                        dto.getId()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
        verify(mockOrderService, times(1))
                .findById(
                        dto.getOrder().getId()
                );
    }


    @Test
    @DisplayName("findByOrderId: when no logged user is given then unauthorized Http code is returned")
    public void findByOrderId_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ORDERID + "/" + id)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_INVOICE }
    )
    @DisplayName("findByOrderId: when no valid authority is given then forbidden Http code is returned")
    public void findByOrderId_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ORDERID + "/" + id)
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    static Stream<Arguments> findByOrderId_invalidParametersTestCases() {
        ErrorResponseDto responseInvalidOrderId = new ErrorResponseDto(
                VALIDATION,
                List.of("orderId: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            orderId,   expectedResponse
                Arguments.of( -1,        responseInvalidOrderId ),
                Arguments.of( 0,         responseInvalidOrderId )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @MethodSource("findByOrderId_invalidParametersTestCases")
    @DisplayName("findByOrderId: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findByOrderId_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(Integer orderId,
                                                                                                                             ErrorResponseDto expectedResponse) {
        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ORDERID + "/" + orderId)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findByOrderId: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findByOrderId_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        Integer orderId = 1;

        when(mockService.findByOrderId(orderId))
                .thenReturn(
                        empty()
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ORDERID + "/" + orderId)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(mockService, times(1))
                .findByOrderId(
                        orderId
                );
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockOrderService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_INVOICE }
    )
    @DisplayName("findByOrderId: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findByOrderId_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        InvoiceDto dto = buildInvoiceDto();
        Invoice model = buildInvoice();

        when(mockService.findByOrderId(dto.getOrder().getId()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        webTestClient.get()
                .uri(RestRoutes.INVOICE.ROOT + RestRoutes.INVOICE.BY_ORDERID + "/" + dto.getOrder().getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(InvoiceDto.class)
                .isEqualTo(dto);

        verify(mockService, times(1))
                .findByOrderId(
                        dto.getOrder().getId()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
        verifyNoInteractions(mockOrderService);
    }



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
                buildOrderDto(),
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
