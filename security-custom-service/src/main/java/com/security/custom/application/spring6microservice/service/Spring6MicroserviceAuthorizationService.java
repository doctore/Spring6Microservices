package com.security.custom.application.spring6microservice.service;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.interfaces.ApplicationClientAuthorizationService;
import org.springframework.stereotype.Service;

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
    public Optional<String> getUsername(final Map<String, Object> source) {
        return ofNullable(source)
                .map(s ->
                        (String) s.get(
                                USERNAME.getKey()
                        )
                );
    }


    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getAuthorities(final Map<String, Object> source) {
        return ofNullable(source)
                .map(s -> {
                    Object authorities = source.get(
                            AUTHORITIES.getKey()
                    );
                    return null == authorities
                            ? null
                            : new HashSet<>((List<String>) authorities);
                })
                .orElseGet(HashSet::new);
    }

}
