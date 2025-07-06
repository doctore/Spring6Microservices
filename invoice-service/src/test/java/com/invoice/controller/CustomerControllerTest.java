package com.invoice.controller;

import com.invoice.InvoiceServiceApplication;
import com.invoice.TestDataFactory;
import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.dto.CustomerDto;
import com.invoice.model.Customer;
import com.invoice.service.CustomerService;
import com.invoice.util.converter.CustomerConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static org.mockito.Mockito.verifyNoInteractions;

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
    @SneakyThrows
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
    @SneakyThrows
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
    @SneakyThrows
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


    // TODO PENDING TO DECIDE IF CREATE 2 TESTS OR JUST ONE PARAMETRIZED FOR "VALID" TESTS



    private static Customer buildNewCustomer() {
        return TestDataFactory.buildCustomer(
                null,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
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

}
