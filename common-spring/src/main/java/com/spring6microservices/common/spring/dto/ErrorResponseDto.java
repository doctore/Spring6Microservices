package com.spring6microservices.common.spring.dto;

import com.spring6microservices.common.spring.enums.RestApiErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information included in an API error response")
public class ErrorResponseDto {

    @Schema(description = "Code with the root cause of the error", requiredMode = RequiredMode.REQUIRED)
    private RestApiErrorCode code;

    @Schema(description = "Details about the error")
    private List<String> errors;


    public ErrorResponseDto(final RestApiErrorCode code) {
        this(
                code,
                new ArrayList<>()
        );
    }


    public ErrorResponseDto(final RestApiErrorCode code,
                            final Collection<String> errors) {
        this.code = code;
        this.errors = null == errors
                ? new ArrayList<>()
                : new ArrayList<>(errors);
    }

}
