package com.spring6microservices.common.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Required information to send as first response when trying to authenticate a user, when the selected flow uses PKCE.
 *
 * @see <a href="http://google.com">https://oauth.net/2/pkce/</a>.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "authorizationCode" })
@Data
@NoArgsConstructor
@Schema(description = "Returned data after first request to authenticate a user (using PKCE flow)")
public class AuthenticationInformationAuthorizationCodeDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6037535194089156343L;

    @Schema(description = "Unique identifier provided after the first authentication request using PKCE flow. Usually a UUID autogenerated value", requiredMode = RequiredMode.REQUIRED)
    private String authorizationCode;

}
