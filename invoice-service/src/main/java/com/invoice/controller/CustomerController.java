package com.invoice.controller;

import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.configuration.security.annotation.CreateCustomerPermission;
import com.invoice.configuration.security.annotation.GetCustomerPermission;
import com.invoice.configuration.security.annotation.UpdateCustomerPermission;
import com.invoice.dto.CustomerDto;
import com.invoice.model.Customer;
import com.invoice.service.CustomerService;
import com.invoice.util.converter.CustomerConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.page.PageDto;
import com.spring6microservices.common.spring.validator.group.CreateAction;
import com.spring6microservices.common.spring.validator.group.UpdateAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rest services to work with {@link Customer}.
 */
@Log4j2
@RestController
@RequestMapping(
        RestRoutes.CUSTOMER.ROOT
)
@Validated
public class CustomerController {

    private final CustomerConverter converter;

    private final CustomerService service;


    @Autowired
    public CustomerController(@Lazy final CustomerConverter customerConverter,
                              @Lazy final CustomerService customerService) {
        this.converter = customerConverter;
        this.service = customerService;
    }


    /**
     * Creates a new {@link Customer} using provided {@link CustomerDto}.
     *
     * @param customerDto
     *    {@link CustomerDto} to create
     *
     * @return if {@code customerDto} is not {@code null}: {@link HttpStatus#CREATED} and created {@link CustomerDto}
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise
     */
    @Operation(
            summary = "Creates a customer",
            description = "Creates a customer (only allowed for users with permission: " + Constants.PERMISSIONS.CREATE_CUSTOMER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given customer was successfully created",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CustomerDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The customer could not be created"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @Transactional(
            rollbackFor = Exception.class
    )
    @CreateCustomerPermission
    public Mono<ResponseEntity<CustomerDto>> create(@RequestBody @Validated(CreateAction.class) final CustomerDto customerDto) {
        log.info(
                format("Creating the customer: %s",
                        customerDto
                )
        );
        return Mono.just(
                service.save(
                        converter.fromDtoToModel(
                                customerDto
                        )
                   )
                   .map(
                           converter::fromModelToDto
                   )
                   .map(c ->
                           new ResponseEntity<>(
                                   c,
                                   CREATED
                           )
                   )
                   .orElseGet(() ->
                           new ResponseEntity<>(
                                   UNPROCESSABLE_ENTITY
                           )
                   )
        );
    }


    /**
     * Returns a {@link Page} of {@link CustomerDto}s using provided {@link PageDto}.
     *
     * @param page
     *    {@link PageDto} to paginate the results
     *
     * @return {@link HttpStatus#OK} and the {@link Page} of {@link CustomerDto} based on provided {@code page}
     */
    @Operation(
            summary = "Returns the customers that matches with page",
            description = "Returns the customers (only allowed for users with permission: " + Constants.PERMISSIONS.GET_CUSTOMER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "With the required page of customers",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CustomerDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            RestRoutes.CUSTOMER.FIND_ALL
    )
    @Transactional(
            readOnly = true
    )
    @GetCustomerPermission
    public Mono<ResponseEntity<Page<CustomerDto>>> findAll(@RequestBody @Valid final PageDto page) {
        log.info(
                format("Searching the page of customers based on provided request: %s",
                        page
                )
        );
        return Mono.just(
                new ResponseEntity<>(
                        service.findAll(
                                page.toPageable()
                        )
                        .map(
                                converter::fromModelToDto
                        ),
                        OK
                )
        );
    }


    /**
     * Returns an existing {@link CustomerDto} using provided {@link Customer#getCode()}.
     *
     * @param code
     *    {@link Customer#getCode()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link CustomerDto} that matches with {@code code},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the customer that matches with provided code",
            description = "Returns a customer (only allowed for users with permission: " + Constants.PERMISSIONS.GET_CUSTOMER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is a customer with the given code",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CustomerDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no a customer matching with provided information"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @GetMapping(
            RestRoutes.CUSTOMER.BY_CODE + "/{code}"
    )
    @Transactional(
            readOnly = true
    )
    @GetCustomerPermission
    public Mono<ResponseEntity<CustomerDto>> findByCode(@PathVariable @Size(min = 1) final String code) {
        log.info(
                format("Searching the customer with code: %s",
                        code
                )
        );
        return Mono.just(
                service.findByCode(
                        code
                )
                .map(
                        converter::fromModelToDto
                )
                .map(c ->
                        new ResponseEntity<>(
                                c,
                                OK
                        )
                )
                .orElseGet(() ->
                        new ResponseEntity<>(
                                NOT_FOUND
                        )
                )
        );
    }


    /**
     * Returns an existing {@link CustomerDto} using provided {@link Customer#getId()}.
     *
     * @param id
     *    {@link Customer#getId()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link CustomerDto} that matches with {@code id},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the customer that matches with provided identifier",
            description = "Returns a customer (only allowed for users with permission: " + Constants.PERMISSIONS.GET_CUSTOMER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is a customer with the given identifier",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CustomerDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no a customer matching with provided information"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @GetMapping(
            RestRoutes.CUSTOMER.BY_ID + "/{id}"
    )
    @Transactional(
            readOnly = true
    )
    @GetCustomerPermission
    public Mono<ResponseEntity<CustomerDto>> findById(@PathVariable @Positive final Integer id) {
        log.info(
                format("Searching the customer with identifier: %s",
                        id
                )
        );
        return Mono.just(
                service.findById(
                        id
                )
                .map(
                        converter::fromModelToDto
                )
                .map(c ->
                        new ResponseEntity<>(
                                c,
                                OK
                        )
                )
                .orElseGet(() ->
                        new ResponseEntity<>(
                                NOT_FOUND
                        )
                )
        );
    }


    /**
     * Updates an existing {@link Customer} using provided {@link CustomerDto}.
     *
     * @param customerDto
     *    {@link CustomerDto} to update
     *
     * @return if {@code customerDto} is not {@code null} and exists: {@link HttpStatus#OK} and updated {@link CustomerDto}
     *         if {@code customerDto} is {@code null} or not exists: {@link HttpStatus#NOT_FOUND}
     */
    @Operation(
            summary = "Updates a customer",
            description = "Updates a customer (only allowed for users with permission: " + Constants.PERMISSIONS.UPDATE_CUSTOMER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The given customer was successfully updated",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CustomerDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no a customer matching with provided information"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PutMapping
    @Transactional(
            rollbackFor = Exception.class
    )
    @UpdateCustomerPermission
    public Mono<ResponseEntity<CustomerDto>> update(@RequestBody @Validated(UpdateAction.class) final CustomerDto customerDto) {
        log.info(
                format("Updating the customer: %s",
                        customerDto
                )
        );
        return Mono.just(
                service.save(
                        converter.fromDtoToModel(
                                customerDto
                        )
                )
                .map(
                        converter::fromModelToDto
                )
                .map(c ->
                        new ResponseEntity<>(
                                c,
                                OK
                        )
                )
                .orElseGet(() ->
                        new ResponseEntity<>(
                                NOT_FOUND
                        )
                )
        );
    }

}
