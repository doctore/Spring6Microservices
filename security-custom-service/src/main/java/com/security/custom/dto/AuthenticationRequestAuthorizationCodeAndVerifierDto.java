package com.security.custom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Required data to authenticate a user using flow with PKCE (second request).
 *
 * @see <a href="http://google.com">https://oauth.net/2/pkce/</a>
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = { "authorizationCode" })
@NoArgsConstructor
@Schema(description = "Required data to authenticate a user using flow with PKCE (second request)")
@ToString(exclude = { "verifier" })
public class AuthenticationRequestAuthorizationCodeAndVerifierDto {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1)
    private String authorizationCode;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1)
    private String verifier;

}
