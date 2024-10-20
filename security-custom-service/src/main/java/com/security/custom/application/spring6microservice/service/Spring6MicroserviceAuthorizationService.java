package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import com.spring6microservices.common.core.util.MapUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.security.custom.enums.token.TokenKey.AUTHORITIES;
import static com.security.custom.enums.token.TokenKey.USERNAME;
import static java.util.Optional.ofNullable;

@Service(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "AuthorizationService")
public class Spring6MicroserviceAuthorizationService implements ApplicationClientAuthorizationService {

    @Override
    public Map<String, Object> getAdditionalAuthorizationInformation(final Map<String, Object> rawAuthorizationInformation) {
        if (MapUtil.isEmpty(rawAuthorizationInformation)) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        getUsername(rawAuthorizationInformation)
                .ifPresent(usename ->
                        result.put(
                                USERNAME.getKey(),
                                usename
                        )
                );
        result.put(
                AUTHORITIES.getKey(),
                this.getAuthorities(
                        rawAuthorizationInformation
                )
        );
        return result;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAuthorities(final Map<String, Object> rawAuthorizationInformation) {
        return ofNullable(rawAuthorizationInformation)
                .map(rai -> {
                    Object authorities = rai.get(
                            AUTHORITIES.getKey()
                    );
                    return null == authorities
                            ? null
                            : new HashSet<>((List<String>) authorities);
                })
                .orElseGet(HashSet::new);
    }


    @Override
    public Optional<String> getUsername(final Map<String, Object> rawAuthorizationInformation) {
        return ofNullable(rawAuthorizationInformation)
                .map(rai ->
                        (String) rai.get(
                                USERNAME.getKey()
                        )
                );
    }

}
