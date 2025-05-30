package com.security.custom;

import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.model.enums.PermissionEnum;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.dto.*;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.enums.token.TokenType;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.AuthenticationRequestDetails;
import com.spring6microservices.common.spring.dto.AuthenticationInformationAuthorizationCodeDto;
import com.spring6microservices.common.spring.dto.AuthenticationInformationDto;
import com.spring6microservices.common.spring.dto.AuthorizationInformationDto;
import com.spring6microservices.common.spring.enums.HashAlgorithm;
import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.security.custom.enums.token.TokenKey.AUTHORITIES;
import static com.security.custom.enums.token.TokenKey.NAME;
import static com.security.custom.enums.token.TokenKey.USERNAME;

@UtilityClass
public class TestDataFactory {

    public static ApplicationClientDetails buildApplicationClientDetailsJWE(final String id) {
        return ApplicationClientDetails.builder()
                .id(id)
                .applicationClientSecret("Spring6Microservices-application_client_secret")
                .signatureAlgorithm(TokenSignatureAlgorithm.HS256)
                .signatureSecret("hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k")
                .securityHandler(SecurityHandler.SPRING6_MICROSERVICES)
                .tokenType(TokenType.JWE)
                .encryptionAlgorithm(TokenEncryptionAlgorithm.DIR)
                .encryptionMethod(TokenEncryptionMethod.A128CBC_HS256)
                .encryptionSecret("dirEncryptionSecret##9991a2(jwe)")
                .accessTokenValidityInSeconds(900)
                .refreshTokenValidityInSeconds(3600)
                .build();
    }


    public static ApplicationClientDetails buildApplicationClientDetailsJWS(final String id) {
        return ApplicationClientDetails.builder()
                .id(id)
                .applicationClientSecret("Spring6Microservices-application_client_secret")
                .signatureAlgorithm(TokenSignatureAlgorithm.HS256)
                .signatureSecret("hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k")
                .securityHandler(SecurityHandler.SPRING6_MICROSERVICES)
                .tokenType(TokenType.JWS)
                .encryptionAlgorithm(null)
                .encryptionMethod(null)
                .encryptionSecret(null)
                .accessTokenValidityInSeconds(900)
                .refreshTokenValidityInSeconds(3600)
                .build();
    }


    public static AuthenticationInformationAuthorizationCodeDto buildAuthenticationInformationAuthorizationCodeDto(final String authorizationCode) {
        return AuthenticationInformationAuthorizationCodeDto.builder()
                .authorizationCode(authorizationCode)
                .build();
    }


    public static AuthenticationInformationDto buildAuthenticationInformationDto(final String id) {
        return AuthenticationInformationDto.builder()
                .id(id)
                .application(
                        SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
                )
                .accessToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0s"
                        + "Im5hbWUiOiJuYW1lIHZhbHVlIiwianRpIjoidW5pcXVlIGlkZW50aWZpZXIiLCJhZ2UiOjIzLCJpYXQiOjUwMDAwMDAwM"
                        + "DAsImV4cCI6NTAwMDAwMDAwMH0.pKoZm2qHaA1zOw9MPCT_1ho2WtDmcYcn2eNr73r_CWg")
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWU"
                        + "iOiJuYW1lIHZhbHVlIiwianRpIjoidW5pcXVlIGlkZW50aWZpZXIiLCJhdGkiOiJyZWZyZXNoIHRva2VuIGlkZW50aWZpZXIiLCJ"
                        + "hZ2UiOjIzLCJpYXQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.vwelPSmcasRZItPfBZ_wwqazR_74U0kHF-d_b7DKO3g")
                .expiresIn(250)
                .additionalInformation(
                        new LinkedHashMap<>() {{
                            put(NAME.getKey(), "name value");
                        }}
                )
                .build();
    }


    public static AuthenticationRequestDetails buildAuthenticationRequestDetails(final String authorizationCode) {
        return new AuthenticationRequestDetails(
                authorizationCode,
                SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId(),
                "challenge test",
                HashAlgorithm.SHA_384
        );
    }


    public static AuthenticationRequestDetails buildAuthenticationRequestDetails(final String authorizationCode,
                                                                                 final String applicationClientId) {
        return new AuthenticationRequestDetails(
                authorizationCode,
                applicationClientId,
                "challenge test",
                HashAlgorithm.SHA_384
        );
    }


    public static AuthenticationRequestDetails buildAuthenticationRequestDetails(final String authorizationCode,
                                                                                 final String applicationClientId,
                                                                                 final String challenge,
                                                                                 final HashAlgorithm challengeMethod) {
        return new AuthenticationRequestDetails(
                authorizationCode,
                applicationClientId,
                challenge,
                challengeMethod
        );
    }


    public static AuthenticationRequestLoginDto buildAuthenticationRequestLoginDto(final String username,
                                                                                   final String password) {
        return new AuthenticationRequestLoginDto(
                username,
                password
        );
    }


    public static AuthenticationRequestLoginAuthorizedDto buildAuthenticationRequestLoginAuthorizedDto(final String challengeMethod) {
        return new AuthenticationRequestLoginAuthorizedDto(
                "challenge test",
                challengeMethod
        );
    }


    public static AuthenticationRequestLoginAuthorizedDto buildAuthenticationRequestLoginAuthorizedDto(final String challenge,
                                                                                                       final String challengeMethod) {
        return new AuthenticationRequestLoginAuthorizedDto(
                challenge,
                challengeMethod
        );
    }


    public static AuthenticationRequestLoginTokenDto buildAuthenticationRequestLoginTokenDto(final String username,
                                                                                             final String password,
                                                                                             final String authorizationCode,
                                                                                             final String verifier) {
        return new AuthenticationRequestLoginTokenDto(
                username,
                password,
                authorizationCode,
                verifier
        );
    }


    public static AuthorizationInformationDto buildAuthorizationInformationDto(final String username,
                                                                               final Set<String> authorities,
                                                                               final Map<String, Object> additionalInformation) {
        return AuthorizationInformationDto.builder()
                .application(
                        SecurityHandler.SPRING6_MICROSERVICES.getApplicationClientId()
                )
                .username(username)
                .authorities(authorities)
                .additionalInformation(additionalInformation)
                .build();
    }


    public static ClearCacheRequestDto buildClearCacheRequestDto(final boolean applicationClientDetails,
                                                                 final boolean applicationUserBlackList,
                                                                 final boolean authenticationRequestDetails) {
        return new ClearCacheRequestDto(
                applicationClientDetails,
                applicationUserBlackList,
                authenticationRequestDetails
        );
    }


    public static LogoutRequestDto buildLogoutRequestDto(final String username) {
        return new LogoutRequestDto(
                username
        );
    }


    public static RawAuthenticationInformationDto buildRawAuthenticationInformationDto(final String username,
                                                                                       final List<String> authorities) {
        return RawAuthenticationInformationDto.builder()
                .accessAuthenticationInformation(
                        new LinkedHashMap<>() {{
                            put(USERNAME.getKey(), username);
                            put(AUTHORITIES.getKey(), authorities);
                        }}
                )
                .refreshAuthenticationInformation(
                        new LinkedHashMap<>() {{
                            put(USERNAME.getKey(), username);
                        }}
                )
                .additionalAuthenticationInformation(
                        new LinkedHashMap<>() {{
                            put(NAME.getKey(), "name value");
                        }}
                )
                .build();
    }


    public static User buildUser(final String username,
                                 final String password,
                                 final boolean isActive) {
        Role role = new Role(
                1,
                RoleEnum.ROLE_ADMIN.name()
        );
        role.addPermission(
                PermissionEnum.CREATE_ORDER
        );
        return User.builder()
                .id(1L)
                .name("name value")
                .username(username)
                .password(password)
                .active(isActive)
                .roles(
                        Set.of(role)
                )
                .build();
    }

}
