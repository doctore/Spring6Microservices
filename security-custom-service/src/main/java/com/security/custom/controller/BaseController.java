package com.security.custom.controller;

import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Log4j2
public abstract class BaseController {

    /**
     * Get the authenticated {@link UserDetails} to know the application is trying to use the provided web services.
     *
     * @return {@link UserDetails}
     *
     * @throws UnauthorizedException if the given {@code clientId} does not exist in database
     */
    protected Mono<UserDetails> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserDetails.class)
                .map(ud -> {
                    log.info(
                            format("Getting security data of the authorized application client details: %s",
                                    StringUtil.getOrElse(
                                            ud,
                                            UserDetails::getUsername
                                    )
                            )
                    );
                    return ud;
                });
    }

}
