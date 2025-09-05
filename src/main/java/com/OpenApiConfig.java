package com;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("REST APIs GESTÃO DE ACESSSO")
                        .version("V1")
                        .description("API para gerenciamento de colaboradores, rotas de transporte e controle de acessos em portarias")
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/Ferrari65/Backend-gestao_acesso")));
    }
}
