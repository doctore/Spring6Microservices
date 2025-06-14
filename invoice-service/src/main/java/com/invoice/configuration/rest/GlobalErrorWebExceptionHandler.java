package com.invoice.configuration.rest;

import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.enums.RestApiErrorCode;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import com.spring6microservices.common.spring.util.JsonUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.spring6microservices.common.spring.enums.RestApiErrorCode.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;

/**
 * Global exception handler to manage uncaught errors in the Rest layer (Controllers)
 */
@RestControllerAdvice
@Log4j2
@Order(
        Ordered.HIGHEST_PRECEDENCE
)
public class GlobalErrorWebExceptionHandler {

    /**
     * Method used to manage when a Rest request throws a {@link AccessDeniedException}.
     *
     * @param exception
     *    {@link AccessDeniedException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<Void> accessDeniedException(final ServerWebExchange exchange,
                                            final AccessDeniedException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                exchange,
                FORBIDDEN.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link AuthenticationException}.
     *
     * @param exception
     *    {@link AuthenticationException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(AuthenticationException.class)
    public Mono<Void> authenticationException(final ServerWebExchange exchange,
                                              final AuthenticationException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                exchange,
                FORBIDDEN.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link WebExchangeBindException}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link WebExchangeBindException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Void> webExchangeBindException(final ServerWebExchange exchange,
                                               final WebExchangeBindException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        List<String> errorMessages = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> "Field error in object '" + fe.getObjectName()
                         + "' on field '" + fe.getField()
                         + "' due to: " + fe.getDefaultMessage())
                .collect(
                        toList()
                );
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                exchange,
                null != exception.getStatusCode()
                        ? exception.getStatusCode().value()
                        : UNPROCESSABLE_ENTITY.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link ConstraintViolationException}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link ConstraintViolationException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<Void> constraintViolationException(final ServerWebExchange exchange,
                                                   final ConstraintViolationException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        List<String> errorMessages = getConstraintViolationExceptionErrorMessages(
                exception
        );
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                exchange,
                BAD_REQUEST.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link IllegalArgumentException}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link IllegalArgumentException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<Void> illegalArgumentException(final ServerWebExchange exchange,
                                               final IllegalArgumentException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        return buildErrorResponse(
                VALIDATION,
                List.of("There was an error in the provided information"),
                exchange,
                BAD_REQUEST.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link ServerWebInputException}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link ServerWebInputException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<Void> serverWebInputException(final ServerWebExchange exchange,
                                              final ServerWebInputException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        List<String> errorMessages = getServerWebInputExceptionErrorMessages(
                exception
        );
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                exchange,
                BAD_REQUEST.value()
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link UnauthorizedException}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link UnauthorizedException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Mono<Void> unauthorizedException(final ServerWebExchange exchange,
                                            final UnauthorizedException exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                exchange,
                UNAUTHORIZED.value()
        );
    }



    /**
     * Method used to manage when a Rest request throws a {@link Throwable}.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link Throwable} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    @ExceptionHandler(Throwable.class)
    public Mono<Void> throwable(final ServerWebExchange exchange,
                                final Throwable exception) {
        log.error(
                getErrorMessageUsingHttpRequest(exchange),
                exception
        );
        return buildErrorResponse(
                INTERNAL,
                List.of("Internal error in the application"),
                exchange,
                INTERNAL_SERVER_ERROR.value()
        );
    }


    /**
     * Using the given {@link ServerWebExchange} builds a message with information about the Http request.
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     *
     * @return error message with Http request information
     */
    private String getErrorMessageUsingHttpRequest(final ServerWebExchange exchange) {
        return format("There was an error trying to execute the request with: %s"
                        + "Http method = %s %s"
                        + "Uri = %s %s"
                        + "Header = %s",
                System.lineSeparator(),
                exchange.getRequest().getMethod(),
                System.lineSeparator(),
                exchange.getRequest().getURI(),
                System.lineSeparator(),
                exchange.getRequest().getHeaders().entrySet()
        );
    }


    /**
     * Gets the list of internal errors included in the given exception.
     *
     * @param exception
     *    {@link ServerWebInputException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getServerWebInputExceptionErrorMessages(final ServerWebInputException exception) {
        if (exception.getCause() instanceof TypeMismatchException ex) {
            return List.of(
                    format("There was an type mismatch error in %s. The provided value was %s and required type is %s",
                            exception.getMethodParameter(),
                            ex.getValue(),
                            ex.getRequiredType()
                    )
            );
        }
        else {
            return List.of(
                    format("There was an error in %s due to %s",
                            exception.getMethodParameter(),
                            exception.getReason()
                    )
            );
        }
    }


    /**
     * Gets the list of internal errors included in the given exception.
     *
     * @param exception
     *    {@link ConstraintViolationException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getConstraintViolationExceptionErrorMessages(final ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(c -> {
                    String rawParameterName = c.getPropertyPath().toString();
                    return rawParameterName.substring(
                            rawParameterName.lastIndexOf(".") + 1
                    )
                    + ": "
                    + c.getMessage();
                })
                .collect(
                        toList()
                );
    }


    /**
     * Builds the Http response related with an error, using the provided parameters.
     *
     * @param errorCode
     *    {@link RestApiErrorCode} included in the response
     * @param errorMessages
     *    {@link List} of error messages to include
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param httpStatus
     *    Http code used in the response
     *
     * @return {@link Mono} with the suitable Http response
     */
    private Mono<Void> buildErrorResponse(final RestApiErrorCode errorCode,
                                          final List<String> errorMessages,
                                          final ServerWebExchange exchange,
                                          final int httpStatus) {
        exchange.getResponse()
                .setRawStatusCode(httpStatus);
        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        ErrorResponseDto error = new ErrorResponseDto(errorCode, errorMessages);
        byte[] responseMessageBytes = JsonUtil.toJson(error)
                .orElse("")
                .getBytes(StandardCharsets.UTF_8);

        DataBuffer bufferResponseMessage = exchange.getResponse()
                .bufferFactory()
                .wrap(responseMessageBytes);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(bufferResponseMessage)
                );
    }

}