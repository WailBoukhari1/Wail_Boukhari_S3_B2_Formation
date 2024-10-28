package com.formation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Formation Management API")
                .description("API for managing courses, students, trainers, and classrooms")
                .version("1.0")
                .contact(new Contact()
                    .name("Formation Team")
                    .email("contact@formation.com")));
    }
}
