package com.security.custom.dto;

import com.security.custom.model.ApplicationClientDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Required data to log out a user related with a {@link ApplicationClientDetails}.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "Required data to log out a user")
public class LogoutRequestDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 64)
    private String username;

}
