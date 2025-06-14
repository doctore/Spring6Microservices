package com.order.configuration.rest;

import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.enums.RestApiErrorCode;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static com.spring6microservices.common.spring.enums.RestApiErrorCode.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> accessDeniedException(final AccessDeniedException exception,
                                                                  final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                FORBIDDEN
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link AuthenticationException}.
     *
     * @param exception
     *    {@link AuthenticationException} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> authenticationException(final AuthenticationException exception,
                                                                    final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                UNAUTHORIZED
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link ConstraintViolationException}.
     *
     * @param exception
     *    {@link ConstraintViolationException} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> constraintViolationException(final ConstraintViolationException exception,
                                                                         final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        List<String> errorMessages = getConstraintViolationExceptionErrorMessages(exception);
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                BAD_REQUEST
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link HttpMessageNotReadableException}.
     *
     * @param exception
     *    {@link HttpMessageNotReadableException} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> httpMessageNotReadableException(final HttpMessageNotReadableException exception,
                                                                            final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        return buildErrorResponse(
                VALIDATION,
                List.of("The was a problem in the parameters of the current request"),
                BAD_REQUEST
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link MethodArgumentNotValidException}.
     *
     * @param exception
     *    {@link MethodArgumentNotValidException} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> methodArgumentNotValidException(final MethodArgumentNotValidException exception,
                                                                            final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        List<String> errorMessages = getMethodArgumentNotValidExceptionErrorMessages(exception);
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                BAD_REQUEST
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link UnauthorizedException}.
     *
     * @param exception
     *    {@link UnauthorizedException} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> unauthorizedException(final UnauthorizedException exception,
                                                                  final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        return buildErrorResponse(
                SECURITY,
                List.of(exception.getMessage()),
                UNAUTHORIZED
        );
    }


    /**
     * Method used to manage when a Rest request throws a {@link Throwable}.
     *
     * @param exception
     *    {@link Throwable} thrown
     * @param request
     *    {@link WebRequest} received
     *
     * @return {@link ResponseEntity} with the suitable {@link ErrorResponseDto}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDto> throwable(final Throwable exception,
                                                      final WebRequest request) {
        log.error(
                getErrorMessageUsingHttpRequest(request),
                exception
        );
        return buildErrorResponse(
                INTERNAL,
                List.of("Internal error in the application"),
                INTERNAL_SERVER_ERROR
        );
    }


    /**
     * Using the given {@link ServerWebExchange} builds a message with information about the Http request
     *
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return error message with Http request information
     */
    private String getErrorMessageUsingHttpRequest(final WebRequest request) {
        HttpServletRequest httpRequest = ((ServletWebRequest)request).getRequest();
        return format("There was an error trying to execute the request with: %s"
                        + "Http method = %s %s "
                        + "Uri = %s",
                System.lineSeparator(),
                httpRequest.getMethod(),
                System.lineSeparator(),
                httpRequest.getRequestURI()
        );
    }


    /**
     * Get the list of internal errors included in the given exception.
     *
     * @param exception
     *    {@link MethodArgumentNotValidException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getMethodArgumentNotValidExceptionErrorMessages(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(fe ->
                        format("Field error in object: %s on field: %s due to: %s",
                                fe.getObjectName(),
                                fe.getField(),
                                fe.getDefaultMessage()
                        )
                )
                .collect(toList());
    }


    /**
     * Get the list of internal errors included in the given exception.
     *
     * @param exception
     *    {@link ConstraintViolationException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getConstraintViolationExceptionErrorMessages(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ce ->
                        format("Error in path: '%s' due to: %s",
                                ce.getPropertyPath(),
                                ce.getMessage()
                        )
                )
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
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link ResponseEntity} of {@link ErrorResponseDto} with the suitable Http response
     */
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(RestApiErrorCode errorCode, List<String> errorMessages, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        ErrorResponseDto error = new ErrorResponseDto(errorCode, errorMessages);
        return new ResponseEntity<>(error, headers, httpStatus);
    }

}
