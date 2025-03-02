package com.security.custom.dto;

import com.security.custom.enums.HashAlgorithm;
import com.spring6microservices.common.spring.validator.enums.EnumHasInternalStringValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Required data to authenticate a user using flow with PKCE (first request).
 *
 * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = { "challenge" })
@NoArgsConstructor
@Schema(description = "Required data to authenticate a user using flow with PKCE (first request)")
@ToString
public class AuthenticationRequestLoginAuthorizedDto {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1)
    private String challenge;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @EnumHasInternalStringValue(enumClass= HashAlgorithm.class)
    private String challengeMethod;

}
