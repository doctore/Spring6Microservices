package com.security.custom.controller;

import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.service.AuthorizationService;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(value = RestRoutes.AUTHORIZATION.ROOT)
@Validated
public class AuthorizationController extends BaseController {

    public final AuthorizationService service;


    @Autowired
    public AuthorizationController(@Lazy final AuthorizationService service) {
        this.service = service;
    }


    /**
     *    Verifies provided {@code accessToken}, generating {@link AuthorizationInformationDto} if it is valid and belongs
     * to the given application's credentials included in the Basic Auth.
     *
     * @param accessToken
     *    Access token used to extract the authorization information
     *
     * @return the {@link AuthorizationInformationDto} with {@link HttpStatus#OK}
     */
    @Operation(
            summary = "Gets the authorization data of the user included in the given access token",
            description = "First validates the given token and then returns his/her: username, roles and additional information"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authorization information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthorizationInformationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given token does not verify included format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active, access token is not valid or not belongs "
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
                            description = "The provided accessToken has expired"
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
    @PostMapping(RestRoutes.AUTHORIZATION.CHECK_TOKEN)
    @Transactional(readOnly = true)
    public Mono<ResponseEntity<AuthorizationInformationDto>> checkToken(@RequestBody @Size(min = 1) final String accessToken) {
        log.info(
                format("Checking the token: %s and getting its authorization information",
                        accessToken
                )
        );
        return getPrincipal()
                .map(userDetails ->
                        new ResponseEntity<>(
                                service.checkAccessToken(
                                        accessToken,
                                        userDetails.getUsername()
                                ),
                                OK
                        )
                );
    }

}
