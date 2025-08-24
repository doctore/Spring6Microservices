package com.invoice.controller;

import com.invoice.configuration.Constants;
import com.invoice.configuration.rest.RestRoutes;
import com.invoice.configuration.security.annotation.CreateInvoicePermission;
import com.invoice.configuration.security.annotation.GetInvoicePermission;
import com.invoice.model.Invoice;
import com.invoice.service.InvoiceService;
import com.invoice.service.OrderService;
import com.invoice.util.converter.InvoiceConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.invoice.InvoiceDto;
import com.spring6microservices.common.spring.dto.page.PageDto;
import com.spring6microservices.common.spring.validator.group.CreateAction;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rest services to work with {@link Invoice}.
 */
@Log4j2
@RestController
@RequestMapping(
        RestRoutes.INVOICE.ROOT
)
@Validated
public class InvoiceController {

    private final InvoiceConverter converter;

    private final InvoiceService service;

    private final OrderService orderService;


    @Autowired
    public InvoiceController(@Lazy final InvoiceConverter invoiceConverter,
                             @Lazy final InvoiceService invoiceService,
                             @Lazy final OrderService orderService) {

        this.converter = invoiceConverter;
        this.service = invoiceService;
        this.orderService = orderService;
    }


    /**
     * Creates a new {@link Invoice} using provided {@link InvoiceDto}.
     *
     * @param invoiceDto
     *    {@link InvoiceDto} to create
     *
     * @return if {@code invoiceDto} is not {@code null}: {@link HttpStatus#CREATED} and created {@link InvoiceDto}
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise
     */
    @Operation(
            summary = "Creates an invoice",
            description = "Creates an invoice (only allowed for users with permission: " + Constants.PERMISSIONS.CREATE_INVOICE
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given invoice was successfully created",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = InvoiceDto.class
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
                            description = "The invoice could not be created"
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
    @CreateInvoicePermission
    public Mono<ResponseEntity<InvoiceDto>> create(@RequestBody @Validated(CreateAction.class) final InvoiceDto invoiceDto) {
        log.info(
                format("Creating the invoice: %s",
                        invoiceDto
                )
        );
        if (orderService.findById(invoiceDto.getOrder().getId()).isEmpty()) {
            log.error(
                    format("The order identifier: %s was not found",
                            invoiceDto.getOrder().getId()
                    )
            );
            return Mono.just(
                    new ResponseEntity<>(
                            UNPROCESSABLE_ENTITY
                    )
            );
        }
        return Mono.just(
                service.save(
                        converter.fromDtoToModel(
                                invoiceDto
                        )
                )
                .map(
                        converter::fromModelToDto
                )
                .map(i ->
                        new ResponseEntity<>(
                                i,
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
     * Returns a {@link Page} of {@link InvoiceDto}s using provided {@link PageDto}.
     *
     * @param page
     *    {@link PageDto} to paginate the results
     *
     * @return {@link HttpStatus#OK} and the {@link Page} of {@link InvoiceDto} based on provided {@code page}
     */
    @Operation(
            summary = "Returns the invoices that matches with page",
            description = "Returns the invoices (only allowed for users with permission: " + Constants.PERMISSIONS.GET_INVOICE
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "With the required page of invoices",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = InvoiceDto.class
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
            RestRoutes.INVOICE.FIND_ALL
    )
    @Transactional(
            readOnly = true
    )
    @GetInvoicePermission
    public Mono<ResponseEntity<Page<InvoiceDto>>> findAll(@RequestBody @Valid final PageDto page) {
        log.info(
                format("Searching the page of invoices based on provided request: %s",
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
     * Returns an existing {@link InvoiceDto} using provided {@link Invoice#getCode()}.
     *
     * @param code
     *    {@link Invoice#getCode()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link InvoiceDto} that matches with {@code code},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the invoice that matches with provided code",
            description = "Returns an invoice (only allowed for users with permission: " + Constants.PERMISSIONS.GET_INVOICE
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is an invoice with the given code",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = InvoiceDto.class
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
                            description = "There is no an invoice matching with provided information"
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
            RestRoutes.INVOICE.BY_CODE + "/{code}"
    )
    @Transactional(
            readOnly = true
    )
    @GetInvoicePermission
    public Mono<ResponseEntity<InvoiceDto>> findByCode(@PathVariable @Size(min = 1) final String code) {
        log.info(
                format("Searching the invoice with code: %s",
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
                .map(dto -> {
                        dto.setOrder(
                                orderService.findById(
                                        dto.getOrder().getId()
                                )
                                .orElse(
                                        dto.getOrder()
                                )
                        );
                        return dto;
                })
                .map(i ->
                        new ResponseEntity<>(
                                i,
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
     * Returns an existing {@link InvoiceDto} using provided {@link Invoice#getId()}.
     *
     * @param id
     *    {@link Invoice#getId()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link InvoiceDto} that matches with {@code id},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the invoice that matches with provided identifier",
            description = "Returns am invoice (only allowed for users with permission: " + Constants.PERMISSIONS.GET_INVOICE
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is am invoice with the given identifier",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = InvoiceDto.class
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
                            description = "There is no an invoice matching with provided information"
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
            RestRoutes.INVOICE.BY_ID + "/{id}"
    )
    @Transactional(
            readOnly = true
    )
    @GetInvoicePermission
    public Mono<ResponseEntity<InvoiceDto>> findById(@PathVariable @Positive final Integer id) {
        log.info(
                format("Searching the invoice with identifier: %s",
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
                .map(dto -> {
                        dto.setOrder(
                                orderService.findById(
                                        dto.getOrder().getId()
                                )
                                .orElse(
                                        dto.getOrder()
                                )
                            );
                            return dto;
                })
                .map(i ->
                        new ResponseEntity<>(
                                i,
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
     * Returns an existing {@link InvoiceDto} using provided {@link Invoice#getOrderId()}.
     *
     * @param orderId
     *    {@link Invoice#getOrderId()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link InvoiceDto} that matches with {@code orderId},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the invoice that matches with provided order's identifier",
            description = "Returns am invoice (only allowed for users with permission: " + Constants.PERMISSIONS.GET_INVOICE
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is am invoice with the given order's identifier",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = InvoiceDto.class
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
                            description = "There is no an invoice matching with provided information"
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
            RestRoutes.INVOICE.BY_ORDERID + "/{orderId}"
    )
    @Transactional(
            readOnly = true
    )
    @GetInvoicePermission
    public Mono<ResponseEntity<InvoiceDto>> findByOrderId(@PathVariable @Positive final Integer orderId) {
        log.info(
                format("Searching the invoice with order's identifier: %s",
                        orderId
                )
        );
        return Mono.just(
                service.findByOrderId(
                                orderId
                )
                .map(
                        converter::fromModelToDto
                )
                .map(dto -> {
                    dto.setOrder(
                            orderService.findById(
                                    dto.getOrder().getId()
                            )
                            .orElse(
                                    dto.getOrder()
                            )
                    );
                    return dto;
                })
                .map(i ->
                        new ResponseEntity<>(
                                i,
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
