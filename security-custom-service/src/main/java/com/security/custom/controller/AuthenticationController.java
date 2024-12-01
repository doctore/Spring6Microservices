package com.security.custom.controller;

import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.AuthenticationRequestDto;
import com.security.custom.service.AuthenticationService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping(value = RestRoutes.AUTHENTICATION.ROOT)
@Validated
public class AuthenticationController extends BaseController {

    public final AuthenticationService service;


    @Autowired
    public AuthenticationController(@Lazy final AuthenticationService service) {
        this.service = service;
    }


    /**
     *    Generates the suitable {@link AuthenticationInformationDto} using the given user's login information and Basic Auth
     * data to extract the application is trying to log the provided user.
     *
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @Operation(
            summary = "Logins a user in a given application",
            description = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authentication information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationInformationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid username or password supplied in the body taking into account defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active or the given password does not belongs to the username. As part "
                                        + "of the Basic Auth, the username (application) does not exists or the given password does not belongs "
                                        + "to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "There is no error related with the security but was not possible to generate the response using "
                                        + "the provided request"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any other internal server error",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = RestRoutes.AUTHENTICATION.LOGIN)
    @Transactional(readOnly = true)
    public Mono<ResponseEntity<AuthenticationInformationDto>> login(@RequestBody @Valid final AuthenticationRequestDto authenticationRequestDto) {
        log.info(
                format("Requesting login with: %s",
                        authenticationRequestDto
                )
        );
        return getPrincipal()
                .map(userDetails ->
                        service.login(
                                        userDetails.getUsername(),
                                        authenticationRequestDto.getUsername(),
                                        authenticationRequestDto.getPassword()
                                )
                                .map(ai ->
                                        new ResponseEntity<>(
                                                ai,
                                                OK
                                        )
                                )
                                .orElseGet(() ->
                                        new ResponseEntity<>(
                                                HttpStatus.UNPROCESSABLE_ENTITY
                                        )
                                )
                );
    }


    /**
     *    Verifies provided {@code refreshToken}, generating {@link AuthenticationInformationDto} if it is valid and belongs
     * to the given application's credentials included in the Basic Auth.
     *
     * @param refreshToken
     *    Refresh token used to regenerate the authentication information
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNAUTHORIZED} otherwise.
     */
    @Operation(
            summary = "Refresh the authentication information of a user in a given application",
            description = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authentication information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationInformationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given token does not verify defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active, refresh token is not valid or not belongs "
                                        + "to given application included as username in the Basic Auth. As part of the "
                                        + "Basic Auth, the username (application) does not exists or the given password "
                                        + "does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "440",
                            description = "The provided refreshToken has expired"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any other internal server error",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = RestRoutes.AUTHENTICATION.REFRESH)
    @Transactional(readOnly = true)
    public Mono<ResponseEntity<AuthenticationInformationDto>> refresh(@RequestBody @Size(min = 1) final String refreshToken) {
        log.info(
                format("Requesting refresh action using the provided token: %s",
                        refreshToken
                )
        );
        return getPrincipal()
                .map(userDetails ->
                        service.refresh(
                                        userDetails.getUsername(),
                                        refreshToken
                                )
                                .map(ai ->
                                        new ResponseEntity<>(
                                                ai,
                                                OK
                                        )
                                )
                                .orElseGet(() ->
                                        new ResponseEntity<>(
                                                HttpStatus.UNAUTHORIZED
                                        )
                                )
                );
    }

}

