package com.security.custom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(
        description = "Configuration about the internal caches to clear"
)
public class ClearCacheRequestDto {

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean applicationClientDetails;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean applicationUserBlackList;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean authenticationRequestDetails;

}