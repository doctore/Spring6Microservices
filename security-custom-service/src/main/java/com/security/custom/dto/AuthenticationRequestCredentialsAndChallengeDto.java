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
 * @see <a href="http://google.com">https://oauth.net/2/pkce/</a>
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = { "username" })
@NoArgsConstructor
@Schema(description = "Required data to authenticate a user using flow with PKCE (first request)")
@ToString(exclude = { "password" })
public class AuthenticationRequestCredentialsAndChallengeDto {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 128)
    private String password;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1)
    private String challenge;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @EnumHasInternalStringValue(enumClass= HashAlgorithm.class)
    private String challengeMethod;

}
