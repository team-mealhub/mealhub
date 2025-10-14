package com.mealhub.backend.global.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mealhub-public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        String jwtSchemeName = "Authorization";

        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(jwtSchemeName, securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .version("1.0")
                        .title("Mealhub")
                        .description("Mealhub REST API Docs"));
    }
}
