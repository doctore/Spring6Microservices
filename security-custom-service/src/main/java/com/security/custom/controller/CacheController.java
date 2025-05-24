package com.security.custom.controller;

import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.dto.ClearCacheRequestDto;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
import com.security.custom.service.cache.ApplicationUserBlackListCacheService;
import com.security.custom.service.cache.AuthenticationRequestDetailsCacheService;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping(
        value = RestRoutes.CACHE.ROOT
)
@Validated
public class CacheController extends BaseController {

    private final ApplicationClientDetailsCacheService applicationClientDetailsCacheService;

    private final ApplicationUserBlackListCacheService applicationUserBlackListCacheService;

    private final AuthenticationRequestDetailsCacheService authenticationRequestDetailsCacheService;


    @Autowired
    public CacheController(final ApplicationClientDetailsCacheService applicationClientDetailsCacheService,
                           final ApplicationUserBlackListCacheService applicationUserBlackListCacheService,
                           final AuthenticationRequestDetailsCacheService authenticationRequestDetailsCacheService) {
        this.applicationClientDetailsCacheService = applicationClientDetailsCacheService;
        this.applicationUserBlackListCacheService = applicationUserBlackListCacheService;
        this.authenticationRequestDetailsCacheService = authenticationRequestDetailsCacheService;
    }


    /**
     * Clear the internal caches used to store:
     *
     * <ul>
     *     <li>{@link ApplicationClientDetails}</li>
     *     <li>Pair {@link ApplicationClientDetails#getId()} and user's identifier (username)</li>
     *     <li>{@link AuthenticationRequestDetails}</li>
     * </ul>
     *
     * @param clearCacheRequest
     *    {@link ClearCacheRequestDto} with the caches to clear
     *
     * @return if it was possible to clear the caches: {@link HttpStatus#OK},
     *         {@link HttpStatus#INTERNAL_SERVER_ERROR} if there was an error.
     */
    @Operation(
            summary = "Clear the cache"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The caches were cleared successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
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
    @PutMapping(
            value = RestRoutes.CACHE.CLEAR
    )
    public Mono<ResponseEntity<?>> clear(@RequestBody final ClearCacheRequestDto clearCacheRequest) {
        log.info(
                format("Clearing the internal caches with: %s",
                        clearCacheRequest
                )
        );
        final String cleanedCacheMessage = "The cache: %s was cleared: %s";
        if (clearCacheRequest.isApplicationClientDetails()) {
            log.info(
                    format(cleanedCacheMessage,
                            applicationClientDetailsCacheService.getCacheName(),
                            applicationClientDetailsCacheService.clear()
                    )
            );
        }
        if (clearCacheRequest.isApplicationUserBlackList()) {
            log.info(
                    format(cleanedCacheMessage,
                            applicationUserBlackListCacheService.getCacheName(),
                            applicationUserBlackListCacheService.clear()
                    )
            );
        }
        if (clearCacheRequest.isAuthenticationRequestDetails()) {
            log.info(
                    format(cleanedCacheMessage,
                            authenticationRequestDetailsCacheService.getCacheName(),
                            authenticationRequestDetailsCacheService.clear()
                    )
            );
        }
        return Mono.just(
                new ResponseEntity<>(
                        OK
                )
        );
    }

}
