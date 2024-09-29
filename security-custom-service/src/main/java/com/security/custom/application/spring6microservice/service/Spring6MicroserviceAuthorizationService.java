package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.interfaces.IAuthorizationService;
import org.springframework.stereotype.Service;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "AuthorizationService")
public class Spring6MicroserviceAuthorizationService implements IAuthorizationService {


    /* TODO: PENDING TO DO:

        @Override
    public Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(final UserDetails userDetails) {
        return null;
        return ofNullable(userDetails)
                .map(user -> buildAuthenticationInformation((User)user));
    }


     @Override
    public String getUsernameKey() {
        return USERNAME.getKey();
    }


    // TODO: PENDING TO CHANGE BY PERMISSIONS
    @Override
    public String getRolesKey() {
        return AUTHORITIES.getKey();
    }


    private RawAuthenticationInformationDto buildAuthenticationInformation(final User user) {
        return RawAuthenticationInformationDto.builder()
                .accessTokenInformation(
                        getAccessTokenInformation(user)
                )
                .refreshTokenInformation(
                        getRefreshTokenInformation(user)
                )
                .additionalTokenInformation(
                        getAdditionalTokenInformation(user)
                )
                .build();
    }


    private Map<String, Object> getAccessTokenInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
            put(
                    NAME.getKey(),
                    user.getName()
            );
            put(
                    AUTHORITIES.getKey(),
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(toList())
            );
        }};
    }


    private Map<String, Object> getRefreshTokenInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
        }};
    }


    private Map<String, Object> getAdditionalTokenInformation(final User user) {
        return new HashMap<>() {{
            put(
                    USERNAME.getKey(),
                    user.getUsername()
            );
            put(
                    AUTHORITIES.getKey(),
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(toList())
            );
        }};
    }
     */

}
