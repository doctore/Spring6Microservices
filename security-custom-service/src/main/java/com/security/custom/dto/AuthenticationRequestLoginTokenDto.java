package com.security.custom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Required data to authenticate a user using flow with PKCE (second request).
 *
 * @see <a href="https://oauth.net/2/pkce/">PKCE</a>
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(
        of = { "authorizationCode" }
)
@NoArgsConstructor
@Schema(
        description = "Required data to authenticate a user using flow with PKCE (second request)"
)
@ToString(
        exclude = { "password", "verifier" }
)
public class AuthenticationRequestLoginTokenDto {

    @Schema(
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String username;

    @Schema(
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1,
            max = 128
    )
    private String password;

    @Schema(
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1
    )
    private String authorizationCode;

    @Schema(
            requiredMode = RequiredMode.REQUIRED
    )
    @NotNull
    @Size(
            min = 1
    )
    private String verifier;

}
