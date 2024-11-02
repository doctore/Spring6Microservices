package com.security.custom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(of = { "username" })
@NoArgsConstructor
@Schema(description = "Required data to authenticate a user")
@ToString(exclude = { "password" })
public class AuthenticationRequestDto {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 128)
    private String password;

}
