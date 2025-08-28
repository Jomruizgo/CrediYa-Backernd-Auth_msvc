package com.crediya.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Auth Microservice API",
        version = "1.0.0",
        description = "API para gestión de usuarios del microservicio de autenticación",
        contact = @Contact(
            name = "CrediYa Team",
            email = "dev@crediya.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Desarrollo"),
        @Server(url = "https://api.crediya.com", description = "Producción")
    }
)
public class OpenApiConfig {
}