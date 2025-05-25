package com.order.controller;

import com.order.configuration.Constants;
import com.order.configuration.rest.RestRoutes;
import com.order.configuration.security.annotation.CreateOrderPermission;
import com.order.configuration.security.annotation.DeleteOrderPermission;
import com.order.configuration.security.annotation.GetOrderPermission;
import com.order.configuration.security.annotation.UpdateOrderPermission;
import com.order.dto.OrderDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.service.OrderService;
import com.order.util.converter.OrderConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rest services to work with {@link Order}.
 */
@Log4j2
@RestController
@RequestMapping(
        RestRoutes.ORDER.ROOT
)
@Validated
public class OrderController {

    private final OrderConverter converter;

    private final OrderService service;


    @Autowired
    public OrderController(@Lazy final OrderConverter orderConverter,
                           @Lazy final OrderService orderService) {
        this.converter = orderConverter;
        this.service = orderService;
    }


    /**
     * Creates a new {@link Order} using provided {@link OrderDto}.
     *
     * @param orderDto
     *    {@link OrderDto} to create
     *
     * @return if {@code orderDto} is not {@code null}: {@link HttpStatus#CREATED} and created {@link OrderDto}
     *         if {@code orderDto} is {@code null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @Operation(
            summary = "Creates an order",
            description = "Creates an order (only allowed for users with permission: " + Constants.PERMISSIONS.CREATE_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given order was successfully created",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = OrderDto.class
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
                            description = "The order could not be created"
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
    @CreateOrderPermission
    public ResponseEntity<OrderDto> create(@RequestBody @Valid final OrderDto orderDto) {
        log.info(
                format("Creating the order: %s",
                        orderDto
                )
        );
        return service.save(
                converter.fromDtoToModel(
                        orderDto
                )
           )
           .map(
                   converter::fromModelToDto
           )
           .map(o ->
                   new ResponseEntity<>(
                           o,
                           CREATED
                   )
           )
           .orElseGet(() ->
                   new ResponseEntity<>(
                           UNPROCESSABLE_ENTITY
                   )
           );
    }


    /**
     * Deletes an existing {@link Order} using provided {@link Order#getCode()}}.
     *
     * @param code
     *    {@link Order#getCode()} to search and delete its related {@link Order}
     *
     * @return {@link HttpStatus#NO_CONTENT} if there related {@link Order} was removed successfully,
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Deletes an order using provided code",
            description = "Deletes an order (only allowed for users with permission: " + Constants.PERMISSIONS.DELETE_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The order with the given code was successfully deleted"
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
                            description = "There is no a order matches with provided information"
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
    @DeleteMapping(
            RestRoutes.ORDER.BY_CODE
    )
    @Transactional(
            rollbackFor = Exception.class
    )
    @DeleteOrderPermission
    public ResponseEntity<Void> deleteByCode(@PathVariable @Size(min = 1) final String code) {
        log.info(
                format("Removing the order with code: %s",
                        code
                )
        );
        return service.deleteByCode(
                code
           )
           ? new ResponseEntity<>(
                   NO_CONTENT
             )
           : new ResponseEntity<>(
                   NOT_FOUND
             );
    }


    /**
     * Deletes an existing {@link Order} using provided {@link Order#getId()}.
     *
     * @param id
     *    {@link Order#getId()} to search and delete its related {@link Order}
     *
     * @return {@link HttpStatus#NO_CONTENT} if there related {@link Order} was removed successfully,
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Deletes an order using provided identifier",
            description = "Deletes an order (only allowed for users with permission: " + Constants.PERMISSIONS.DELETE_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The order with the given identifier was successfully deleted"
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
                            description = "There is no a order matches with provided information"
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
    @DeleteMapping(
            RestRoutes.ORDER.BY_ID
    )
    @Transactional(
            rollbackFor = Exception.class
    )
    @DeleteOrderPermission
    public ResponseEntity<Void> deleteById(@PathVariable @Size(min = 1) final Integer id) {
        log.info(
                format("Removing the order with id: %s",
                        id
                )
        );
        return service.deleteById(
                id
           )
           ? new ResponseEntity<>(
                   NO_CONTENT
             )
           : new ResponseEntity<>(
                   NOT_FOUND
            );
    }


    /**
     * Returns an existing {@link Order} (and related {@link OrderLine}s) using provided {@link Order#getCode()}}.
     *
     * @param code
     *    {@link Order#getCode()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link OrderDto} that matches with {@code code},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the order (and order lines) that matches with provided code",
            description = "Returns an order (only allowed for users with permission: " + Constants.PERMISSIONS.GET_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is an order with the given code",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = OrderDto.class
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
                            description = "There is no a order matches with provided information"
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
            RestRoutes.ORDER.BY_CODE
    )
    @Transactional(
            readOnly = true
    )
    @GetOrderPermission
    public ResponseEntity<OrderDto> findByCode(@PathVariable @Size(min = 1) final String code) {
        log.info(
                format("Returning the order (and related order lines) with code: %s",
                        code
                )
        );
        return service.findByCode(
                code
           )
           .map(
                   converter::fromModelToDto
           )
           .map(o ->
                   new ResponseEntity<>(
                           o,
                           OK
                   )
           )
           .orElseGet(() ->
                   new ResponseEntity<>(
                           NOT_FOUND
                   )
           );
    }


    /**
     * Returns an existing {@link Order} (and related {@link OrderLine}s) using provided {@link Order#getId()}.
     *
     * @param id
     *    {@link Order#getId()} to search
     *
     * @return {@link HttpStatus#OK} and the {@link OrderDto} that matches with {@code id},
     *         {@link HttpStatus#NOT_FOUND} otherwise
     */
    @Operation(
            summary = "Returns the order (and order lines) that matches with provided identifier",
            description = "Returns an order (only allowed for users with permission: " + Constants.PERMISSIONS.GET_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is an order with the given identifier",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = OrderDto.class
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
                            description = "There is no a order matches with provided information"
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
            RestRoutes.ORDER.BY_ID
    )
    @Transactional(
            readOnly = true
    )
    @GetOrderPermission
    public ResponseEntity<OrderDto> findById(@PathVariable @Positive final Integer id) {
        log.info(
                format("Returning the order (and related order lines) with identifier: %s",
                        id
                )
        );
        return service.findById(
                id
           )
           .map(
                   converter::fromModelToDto
           )
           .map(o ->
                   new ResponseEntity<>(
                           o,
                           OK
                   )
           )
           .orElseGet(() ->
                   new ResponseEntity<>(
                           NOT_FOUND
                   )
           );
    }


    /**
     * Updates an existing {@link Order} using provided {@link OrderDto}.
     *
     * @param orderDto
     *    {@link OrderDto} to update
     *
     * @return if {@code orderDto} is not {@code null} and exists: {@link HttpStatus#OK} and updated {@link OrderDto}
     *         if {@code orderDto} is {@code null} or not exists: {@link HttpStatus#NOT_FOUND} and {@code null}
     */
    @Operation(
            summary = "Updates an order",
            description = "Updates an order (only allowed for users with permission: " + Constants.PERMISSIONS.UPDATE_ORDER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The given order was successfully updated",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = OrderDto.class
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
                            description = "There is no a order matches with provided information"
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
    @UpdateOrderPermission
    public ResponseEntity<OrderDto> update(@RequestBody @Valid final OrderDto orderDto) {
        log.info(
                format("Updating the order: %s",
                        orderDto
                )
        );
        return service.save(
                converter.fromDtoToModel(
                        orderDto
                )
           )
           .map(
                   converter::fromModelToDto
           )
           .map(o ->
                   new ResponseEntity<>(
                           o,
                           OK
                   )
           )
           .orElseGet(() ->
                   new ResponseEntity<>(
                           NOT_FOUND
                   )
           );
    }

}
