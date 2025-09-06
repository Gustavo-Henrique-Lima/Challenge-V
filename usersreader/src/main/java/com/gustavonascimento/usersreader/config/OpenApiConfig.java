package com.gustavonascimento.usersreader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI userReader() {
        return new OpenAPI().info(new Info().title("User Reader")
                .description(
                        "Documentação da API de leitura de usuários")
                .version("1.0"));
    }
}
