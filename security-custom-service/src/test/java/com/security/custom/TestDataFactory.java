package com.security.custom;

import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.model.enums.PermissionEnum;
import com.security.custom.application.spring6microservice.model.enums.RoleEnum;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.model.ApplicationClientDetails;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class TestDataFactory {

    public static ApplicationClientDetails buildApplicationClientDetails(final String id) {
        return ApplicationClientDetails.builder()
                .id(id)
                .applicationClientSecret("Spring6Microservices-application_client_secret")
                .signatureAlgorithm(TokenSignatureAlgorithm.HS256)
                .signatureSecret("hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k")
                .securityHandler(SecurityHandler.SPRING6_MICROSERVICES)
                .encryptionAlgorithm(TokenEncryptionAlgorithm.DIR)
                .encryptionMethod(TokenEncryptionMethod.A128CBC_HS256)
                .encryptionSecret("dirEncryptionSecret##9991a2(jwe)")
                .accessTokenValidityInSeconds(900)
                .refreshTokenValidityInSeconds(3600)
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
