package com.security.custom.controller;

import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.AuthenticationRequestLoginTokenDto;
import com.security.custom.dto.AuthenticationRequestLoginAuthorizedDto;
import com.security.custom.dto.AuthenticationRequestLoginDto;
import com.security.custom.dto.LogoutRequestDto;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.AuthenticationService;
import com.spring6microservices.common.spring.dto.AuthenticationInformationAuthorizationCodeDto;
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
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping(
        value = RestRoutes.AUTHENTICATION.ROOT
)
@Validated
public class AuthenticationController extends BaseController {

    private final AuthenticationService service;


    @Autowired
    public AuthenticationController(final AuthenticationService service) {
        this.service = service;
    }


    /**
     *    Generates the suitable {@link AuthenticationInformationDto} using the given user's login information and Basic Auth
     * data to extract the application is trying to log the provided user. This endpoint does not use PKCE flow.
     *
     * @param authenticationRequest
     *    {@link AuthenticationRequestLoginDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise.
     *
     * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
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
                                    schema = @Schema(
                                            implementation = AuthenticationInformationDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid information supplied in the body taking into account defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active or the given password does not belongs to the username. As part "
                                        + "of the Basic Auth, the username (application) does not exists or the given password does not belongs "
                                        + "to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            value = RestRoutes.AUTHENTICATION.LOGIN
    )
    @Transactional(
            readOnly = true
    )
    public Mono<ResponseEntity<AuthenticationInformationDto>> login(@RequestBody @Valid final AuthenticationRequestLoginDto authenticationRequest) {
        log.info(
                format("Requesting login with: %s",
                        authenticationRequest
                )
        );
        return getPrincipal()
                .map(applicationClientDetails ->
                        service.login(
                                        applicationClientDetails.getUsername(),
                                        authenticationRequest.getUsername(),
                                        authenticationRequest.getPassword()
                                )
                                .map(ai ->
                                        new ResponseEntity<>(
                                                ai,
                                                OK
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
     *    Generates the suitable {@link AuthenticationInformationAuthorizationCodeDto} using the given {@link AuthenticationRequestLoginAuthorizedDto}
     * and Basic Auth data to extract the application is trying to log the provided user. This endpoint is part of the PKCE flow,
     * more specifically the first request.
     *
     * @param authenticationRequest
     *    {@link AuthenticationRequestLoginAuthorizedDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationAuthorizationCodeDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise.
     *
     * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
     */
    @Operation(
            summary = "Logins a user in a given application as part of the PKCE flow (first request)",
            description = "Returns the authorization code to use in the next request, as part of the PKCE authentication flow"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authorization code in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = AuthenticationInformationAuthorizationCodeDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid information supplied in the body taking into account defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "As part of the Basic Auth, the username (application) does not exists or the given password "
                                        + "does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            value = RestRoutes.AUTHENTICATION.LOGIN_AUTHORIZED
    )
    public Mono<ResponseEntity<AuthenticationInformationAuthorizationCodeDto>> loginAuthorized(@RequestBody @Valid final AuthenticationRequestLoginAuthorizedDto authenticationRequest) {
        log.info(
                format("Requesting login authorized with: %s",
                        authenticationRequest
                )
        );
        return getPrincipal()
                .map(applicationClientDetails ->
                        service.loginAuthorized(
                                        applicationClientDetails.getUsername(),
                                        authenticationRequest
                                )
                                .map(ai ->
                                        new ResponseEntity<>(
                                                ai,
                                                OK
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
     *    Generates the suitable {@link AuthenticationInformationDto} using the given {@link AuthenticationRequestLoginTokenDto}
     * and Basic Auth data to extract the application is trying to log the user. This endpoint is part of the PKCE flow,
     * more specifically the second request.
     *
     * @param authenticationRequest
     *    {@link AuthenticationRequestLoginAuthorizedDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise.
     *
     * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
     */
    @Operation(
            summary = "Logins a user in a given application as part of the PKCE flow (second request)",
            description = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authentication information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = AuthenticationInformationDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid information supplied in the body taking into account defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "As part of the Basic Auth, the username (application) does not exists or the given password does "
                                        + "not belongs to this one. Using the stored user's credentials, if he/she is not active or the password "
                                        + "does not belongs to the username. Provided verifier does not match with stored challenge and challenge "
                                        + "method of the first request",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "As part of the Basic Auth, the username (application) of this second request does not match "
                                        + "with the one stored as part of the first request",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            value = RestRoutes.AUTHENTICATION.LOGIN_TOKEN
    )
    public Mono<ResponseEntity<AuthenticationInformationDto>> loginToken(@RequestBody @Valid final AuthenticationRequestLoginTokenDto authenticationRequest) {
        log.info(
                format("Requesting login token with: %s",
                        authenticationRequest
                )
        );
        return getPrincipal()
                .map(applicationClientDetails ->
                        service.loginToken(
                                        applicationClientDetails.getUsername(),
                                        authenticationRequest
                                )
                                .map(ai ->
                                        new ResponseEntity<>(
                                                ai,
                                                OK
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
     *    Logs out the {@link LogoutRequestDto#getUsername()} related with authenticated {@link ApplicationClientDetails}
     * (included in the request with Basic Auth).
     *
     * @param logoutRequest
     *    {@link LogoutRequestDto} with the required information to complete the log-out
     *
     * @return {@link HttpStatus#NO_CONTENT} if there is no error,
     *         {@link HttpStatus#UNPROCESSABLE_ENTITY} otherwise.
     */
    @Operation(
            summary = "Logs out a user in a given application",
            description = "Completes the logs out of a user in an application, any new request coming will be rejected until he/she logs in again"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful operation with no body in the response"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid information supplied in the body taking into account defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "As part of the Basic Auth, the username (application) does not exists or the given password does "
                                        + "not belongs to this one",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            value = RestRoutes.AUTHENTICATION.LOGOUT
    )
    public Mono<ResponseEntity<Void>> logout(@RequestBody @Valid final LogoutRequestDto logoutRequest) {
        log.info(
                format("Logs out using the request: %s",
                        logoutRequest
                )
        );
        return getPrincipal()
                .map(applicationClientDetails ->
                        service.logout(
                                applicationClientDetails.getUsername(),
                                logoutRequest
                        )
                )
                .map(logoutResult ->
                        logoutResult
                                ? new ResponseEntity<>(
                                        NO_CONTENT
                                  )
                                : new ResponseEntity<>(
                                        UNPROCESSABLE_ENTITY
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
                                    schema = @Schema(
                                            implementation = AuthenticationInformationDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given token does not verify defined format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active, refresh token is not valid or not belongs "
                                        + "to given application included as username in the Basic Auth. As part of the "
                                        + "Basic Auth, the username (application) does not exists or the given password "
                                        + "does not belongs to this one. If the application and the user inside the refresh "
                                        + "token were added in the blacklist",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDto.class
                                    )
                            )
                    )
            }
    )
    @PostMapping(
            value = RestRoutes.AUTHENTICATION.REFRESH
    )
    @Transactional(
            readOnly = true
    )
    public Mono<ResponseEntity<AuthenticationInformationDto>> refresh(@RequestBody @Size(min = 1) final String refreshToken) {
        log.info(
                format("Requesting refresh action using the provided token: %s",
                        refreshToken
                )
        );
        return getPrincipal()
                .map(applicationClientDetails ->
                        service.refresh(
                                        applicationClientDetails.getUsername(),
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

