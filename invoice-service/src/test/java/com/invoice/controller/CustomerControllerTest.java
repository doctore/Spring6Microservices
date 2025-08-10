package com.invoice.controller;

import com.invoice.InvoiceServiceApplication;
import com.invoice.TestDataFactory;
import com.invoice.TestUtil;
import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.model.Customer;
import com.invoice.service.CustomerService;
import com.invoice.util.converter.CustomerConverter;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.invoice.CustomerDto;
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

import static com.invoice.TestDataFactory.buildEmptyPage;
import static com.invoice.TestDataFactory.buildPage;
import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(
        classes = InvoiceServiceApplication.class
)
public class CustomerControllerTest extends BaseControllerTest {

    @MockitoBean
    private CustomerConverter mockConverter;

    @MockitoBean
    private CustomerService mockService;

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
        CustomerDto dto = buildNewCustomerDto();

        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("create: when no valid authority is given then forbidden Http code is returned")
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        CustomerDto dto = buildNewCustomerDto();

        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    static Stream<Arguments> create_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(300, "a"));

        CustomerDto dtoWithNoCode = buildNewCustomerDto();
        dtoWithNoCode.setCode(null);

        CustomerDto dtoWithLongCode = buildNewCustomerDto();
        dtoWithLongCode.setCode(longString);

        CustomerDto dtoWithNoAddress = buildNewCustomerDto();
        dtoWithNoAddress.setAddress(null);

        CustomerDto dtoWithLongAddress = buildNewCustomerDto();
        dtoWithLongAddress.setAddress(longString);

        CustomerDto dtoWithNoPhone = buildNewCustomerDto();
        dtoWithNoPhone.setPhone(null);

        CustomerDto dtoWithLongPhone = buildNewCustomerDto();
        dtoWithLongPhone.setPhone(longString);

        CustomerDto dtoWithLongEmail = buildNewCustomerDto();
        dtoWithLongEmail.setEmail(longString);

        ErrorResponseDto responseDtoWithNoCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'code' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'code' due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithNoAddress = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'address' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongAddress = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'address' due to: size must be between 1 and 128")
        );
        ErrorResponseDto responseDtoWithNoPhone = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'phone' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongPhone = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'phone' due to: size must be between 1 and 16")
        );
        ErrorResponseDto responseDtoWithLongEmail = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'email' due to: size must be between 0 and 64")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,          expectedResponse
                Arguments.of( dtoWithNoCode,        responseDtoWithNoCode ),
                Arguments.of( dtoWithLongCode,      responseDtoWithLongCode ),
                Arguments.of( dtoWithNoAddress,     responseDtoWithNoAddress ),
                Arguments.of( dtoWithLongAddress,   responseDtoWithLongAddress ),
                Arguments.of( dtoWithNoPhone,       responseDtoWithNoPhone ),
                Arguments.of( dtoWithLongPhone,     responseDtoWithLongPhone ),
                Arguments.of( dtoWithLongEmail,     responseDtoWithLongEmail )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @MethodSource("create_invalidParametersTestCases")
    @DisplayName("create: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void create_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(CustomerDto dtoToCreate,
                                                                                                                      ErrorResponseDto expectedResponse) {
        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dtoToCreate),
                        CustomerDto.class
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
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @DisplayName("create: when given parameters verifies validations but service returns empty then Http code Unprocessable Entity is returned")
    public void create_whenGivenParametersVerifiesValidationsButServiceReturnsEmpty_thenHttpCodeUnprocessableEntityIsReturned() {
        CustomerDto dto = buildNewCustomerDto();
        Customer model = buildNewCustomer();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        empty()
                );

        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
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
                        any(Customer.class)
                );
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @DisplayName("create: when given parameters verifies validations and service returns a model then Http code Created with the new Dto is returned")
    public void create_whenGivenParametersVerifiesValidationsAndServiceReturnAModel_thenHttpCodeCreatedWithTheNewDtoIsReturned() {
        CustomerDto beforeDto = buildNewCustomerDto();
        CustomerDto afterDto = buildNewCustomerDto();
        afterDto.setId(1);

        Customer beforeModel = buildNewCustomer();
        Customer afterModel = buildNewCustomer();
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
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(beforeDto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDto.class)
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
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.FIND_ALL)
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
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @DisplayName("findAll: when no valid authority is given then forbidden Http code is returned")
    public void findAll_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        PageDto dto = buildPageDto();

        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.FIND_ALL)
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
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @MethodSource("findAll_invalidParametersTestCases")
    @DisplayName("findAll: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findAll_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(PageDto dto,
                                                                                                                       ErrorResponseDto expectedResponse) {
        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.FIND_ALL)
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
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findAll: when no results are found then empty page is returned")
    public void findAll_whenNoResultsAreFound_thenEmptyPageIsReturned() {
        PageDto pageDto = buildPageDto();

        when(mockService.findAll(pageDto.toPageable()))
                .thenReturn(
                        buildEmptyPage(pageDto.toPageable())
                );

        webTestClient.post()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.FIND_ALL)
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
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findAll: when results are found then expected page is returned")
    public void findAll_whenResultsAreFound_thenExpectedPageIsReturned() {
        PageDto pageDto = buildPageDto();
        CustomerDto dto = buildCustomerDto();
        Customer model = buildCustomer();

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
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.FIND_ALL)
                .body(
                        Mono.just(pageDto),
                        PageDto.class
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.[0].id").isEqualTo(dto.getId())
                .jsonPath("$.content.[0].code").isEqualTo(dto.getCode())
                .jsonPath("$.content.[0].address").isEqualTo(dto.getAddress())
                .jsonPath("$.content.[0].phone").isEqualTo(dto.getPhone())
                .jsonPath("$.content.[0].email").isEqualTo(dto.getEmail())
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
                "Customer 1"
        );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @DisplayName("findByCode: when no valid authority is given then forbidden Http code is returned")
    public void findByCode_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        String code = StringUtil.urlEncode(
                "Customer 1"
        );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        String code = StringUtil.urlEncode(
                "Customer 1"
        );

        when(mockService.findByCode(code))
                .thenReturn(
                        empty()
                );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_CODE + "/" + code)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(mockService, times(1))
                .findByCode(
                        code
                );
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        CustomerDto dto = buildCustomerDto();
        Customer model = buildCustomer();

        when(mockService.findByCode(dto.getCode()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_CODE + "/" + dto.getCode())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDto.class)
                .isEqualTo(dto);

        verify(mockService, times(1))
                .findByCode(
                        dto.getCode()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    @Test
    @DisplayName("findById: when no logged user is given then unauthorized Http code is returned")
    public void findById_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_CUSTOMER }
    )
    @DisplayName("findById: when no valid authority is given then forbidden Http code is returned")
    public void findById_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        int id = 1;

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
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
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @MethodSource("findById_invalidParametersTestCases")
    @DisplayName("findById: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findById_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(Integer id,
                                                                                                                        ErrorResponseDto expectedResponse) {
        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findById: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        Integer id = 1;

        when(mockService.findById(id))
                .thenReturn(
                        empty()
                );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_ID + "/" + id)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(mockService, times(1))
                .findById(
                        id
                );
        verifyNoInteractions(mockConverter);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("findById: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        CustomerDto dto = buildCustomerDto();
        Customer model = buildCustomer();

        when(mockService.findById(dto.getId()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        webTestClient.get()
                .uri(RestRoutes.CUSTOMER.ROOT + RestRoutes.CUSTOMER.BY_ID + "/" + dto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDto.class)
                .isEqualTo(dto);

        verify(mockService, times(1))
                .findById(
                        dto.getId()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    @Test
    @DisplayName("update: when no logged user is given then unauthorized Http code is returned")
    public void update_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        CustomerDto dto = buildCustomerDto();

        webTestClient.put()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus()
                .isUnauthorized();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_CUSTOMER }
    )
    @DisplayName("update: when no valid authority is given then forbidden Http code is returned")
    public void update_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        CustomerDto dto = buildCustomerDto();

        webTestClient.put()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus()
                .isForbidden();

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    static Stream<Arguments> update_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(300, "a"));

        CustomerDto dtoWithNoId = buildNewCustomerDto();

        CustomerDto dtoWithNoCode = buildCustomerDto();
        dtoWithNoCode.setCode(null);

        CustomerDto dtoWithLongCode = buildCustomerDto();
        dtoWithLongCode.setCode(longString);

        CustomerDto dtoWithNoAddress = buildCustomerDto();
        dtoWithNoAddress.setAddress(null);

        CustomerDto dtoWithLongAddress = buildCustomerDto();
        dtoWithLongAddress.setAddress(longString);

        CustomerDto dtoWithNoPhone = buildCustomerDto();
        dtoWithNoPhone.setPhone(null);

        CustomerDto dtoWithLongPhone = buildCustomerDto();
        dtoWithLongPhone.setPhone(longString);

        CustomerDto dtoWithLongEmail = buildCustomerDto();
        dtoWithLongEmail.setEmail(longString);

        ErrorResponseDto responseDtoWithNoId = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'id' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithNoCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'code' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'code' due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithNoAddress = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'address' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongAddress = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'address' due to: size must be between 1 and 128")
        );
        ErrorResponseDto responseDtoWithNoPhone = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'phone' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongPhone = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'phone' due to: size must be between 1 and 16")
        );
        ErrorResponseDto responseDtoWithLongEmail = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'customerDto' on field 'email' due to: size must be between 0 and 64")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,          expectedResponse
                Arguments.of( dtoWithNoId,          responseDtoWithNoId ),
                Arguments.of( dtoWithNoCode,        responseDtoWithNoCode ),
                Arguments.of( dtoWithLongCode,      responseDtoWithLongCode ),
                Arguments.of( dtoWithNoAddress,     responseDtoWithNoAddress ),
                Arguments.of( dtoWithLongAddress,   responseDtoWithLongAddress ),
                Arguments.of( dtoWithNoPhone,       responseDtoWithNoPhone ),
                Arguments.of( dtoWithLongPhone,     responseDtoWithLongPhone ),
                Arguments.of( dtoWithLongEmail,     responseDtoWithLongEmail )
        ); //@formatter:on
    }

    @ParameterizedTest
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.UPDATE_CUSTOMER }
    )
    @MethodSource("update_invalidParametersTestCases")
    @DisplayName("update: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void update_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(CustomerDto dtoToCreate,
                                                                                                                      ErrorResponseDto expectedResponse) {
        webTestClient.put()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dtoToCreate),
                        CustomerDto.class
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
            authorities = { Constants.PERMISSIONS.UPDATE_CUSTOMER }
    )
    @DisplayName("update: when given parameters verifies validations but service returns empty then Http code Not Found is returned")
    public void update_whenGivenParametersVerifiesValidationsButServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        CustomerDto dto = buildCustomerDto();
        Customer model = buildCustomer();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        empty()
                );

        webTestClient.put()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND)
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
                        any(Customer.class)
                );
    }


    @Test
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.UPDATE_CUSTOMER }
    )
    @DisplayName("update: when given parameters verifies validations and service returns a model then Http code Ok with the updated Dto is returned")
    public void update_whenGivenParametersVerifiesValidationsAndServiceReturnAModel_thenHttpCodeOkdWithTheUpdatedDtoIsReturned() {
        CustomerDto dto = buildCustomerDto();
        Customer model = buildCustomer();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        webTestClient.put()
                .uri(RestRoutes.CUSTOMER.ROOT)
                .body(
                        Mono.just(dto),
                        CustomerDto.class
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDto.class)
                .isEqualTo(dto);

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        dto
                );
        verify(mockService, times(1))
                .save(
                        model
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    private static Customer buildCustomer() {
        Customer model = buildNewCustomer();
        model.setId(1);
        return model;
    }


    private static Customer buildNewCustomer() {
        return TestDataFactory.buildCustomer(
                null,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
    }


    private static CustomerDto buildCustomerDto() {
        CustomerDto dto = buildNewCustomerDto();
        dto.setId(1);
        return dto;
    }


    private static CustomerDto buildNewCustomerDto() {
        return TestDataFactory.buildCustomerDto(
                null,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
    }


    private static PageDto buildPageDto() {
        List<SortDto> sortDtos = List.of(
                TestDataFactory.buildSortDto(
                        Customer.ID_PROPERTY,
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
