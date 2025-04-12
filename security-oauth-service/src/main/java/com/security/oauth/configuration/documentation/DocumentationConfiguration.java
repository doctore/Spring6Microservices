package com.security.oauth.configuration.documentation;

import com.security.oauth.configuration.rest.RestRoutes;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springdoc.core.utils.Constants.SWAGGER_UI_PREFIX;

@Configuration
@Getter
public class DocumentationConfiguration {

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    @Value("${springdoc.swagger-ui.path}")
    private String apiUiUrl;

    /**
     *    Required to configure the String Security because SpringDoc does an internal redirection from
     * {@link DocumentationConfiguration#apiUiUrl} to {@code http://server:port/context-path/}
     * {@link org.springdoc.core.utils.Constants#SWAGGER_UI_PREFIX}
     */
    private final String internalApiUiPrefix = RestRoutes.ROOT + SWAGGER_UI_PREFIX;

    @Value("${springdoc.documentation.apiVersion}")
    private String apiVersion;

    @Value("${springdoc.documentation.description}")
    private String description;

    @Value("${springdoc.documentation.title}")
    private String title;

    @Value("${springdoc.security.authorization}")
    private String securityAuthorization;

    @Value("${springdoc.security.schema}")
    private String securitySchema;


    /**
     * Defines specific Swagger configuration used by the project.
     *
     * @return {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securityAuthorization)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securityAuthorization,
                                        securityScheme()
                                )
                )
                .info(apiInfo());
    }


    /**
     * Includes more information related with the Rest Api documentation.
     *
     * @return {@link Info}
     */
    private Info apiInfo() {
        return new Info()
                .title(title)
                .description(description)
                .version(apiVersion);
    }


    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(securityAuthorization)
                .type(SecurityScheme.Type.HTTP)
                .scheme(securitySchema);
    }

}
