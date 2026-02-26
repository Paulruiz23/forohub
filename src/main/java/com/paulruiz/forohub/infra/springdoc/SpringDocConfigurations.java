package com.paulruiz.forohub.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 Configuración de SpringDoc OpenAPI (Swagger)
 para documentación automática de la API
 */
@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("ForoHub API")
                        .description("API REST para gestión de foro de discusión con Spring Boot 3, " +
                                "incluyendo autenticación JWT, CRUD de tópicos, respuestas y usuarios. " +
                                "Desarrollado como parte del Challenge de Alura Latam.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Paul Stuart Ruiz Cabrera")
                                .email("paul.ruiz@example.com")
                                .url("https://github.com/Paulruiz23"))
                        .license(new License()
                                .name("Proyecto Educativo - Alura Latam")
                                .url("https://www.aluracursos.com/")));
    }
}