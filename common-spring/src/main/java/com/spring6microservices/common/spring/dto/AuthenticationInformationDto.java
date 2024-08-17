package com.spring6microservices.common.spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Required information to send as response in the {@code login} request
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "jwtId" })
@Data
@NoArgsConstructor
@Schema(description = "Returned data after authenticate a user")
public class AuthenticationInformationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -4007535195077048326L;

    @Schema(description = "Identifier of every authentication information instance. Usually a UUID autogenerated value", requiredMode = RequiredMode.REQUIRED)
    @JsonProperty(value = "jti")
    private String jwtId;

    @Schema(description = "Token-based authentication to allow an application to access the microservices", requiredMode = RequiredMode.REQUIRED)
    @JsonProperty(value = "access_token")
    private String accessToken;

    @Schema(description = "Access token expiration time in seconds", requiredMode = RequiredMode.REQUIRED)
    @JsonProperty(value = "expires_in")
    private int expiresIn;

    @Schema(description = "Special token used to obtain additional access tokens", requiredMode = RequiredMode.REQUIRED)
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @Schema(description = "What type is the provided access token. For example: Bearer", requiredMode = RequiredMode.REQUIRED)
    @JsonProperty(value = "token_type")
    private String tokenType;

    @Schema(description = "Values related with the internal mechanism to limit an application's access to a user's account")
    private String scope;

    @Schema(description = "Extra data returned by security service")
    private Map<String, Object> additionalInfo;

}