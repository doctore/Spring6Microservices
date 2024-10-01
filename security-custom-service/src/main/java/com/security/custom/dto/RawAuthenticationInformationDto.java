package com.security.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Required information to generate the final response of an authentication request.
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class RawAuthenticationInformationDto {

    Map<String, Object> accessAuthenticationInformation;
    Map<String, Object> refreshAuthenticationInformation;
    Map<String, Object> additionalAuthenticationInformation;

}
