package com.spring6microservices.common.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@Schema(description = "Authorization information about an specific user")
public class UsernameAuthoritiesDto {

    @Schema(description = "Identifier of the logged user", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Roles and/or permissions of the logged user", requiredMode = RequiredMode.REQUIRED)
    private Set<String> authorities;

    @Schema(description = "Extra data returned by security service", requiredMode = RequiredMode.REQUIRED)
    private Map<String, Object> additionalInfo;


    public UsernameAuthoritiesDto(final String username) {
        this(
                username,
                new HashSet<>(),
                new HashMap<>()
        );
    }


    public UsernameAuthoritiesDto(final String username,
                                  final Collection<String> authorities,
                                  final Map<String, Object> additionalInfo) {
        this.username = username;

        this.authorities = null == authorities
                ? new HashSet<>()
                : new HashSet<>(authorities);

        this.additionalInfo = null == additionalInfo
                ? new HashMap<>()
                : new HashMap<>(additionalInfo);
    }

}
