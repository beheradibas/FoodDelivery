package com.fooddelivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI foodDeliveryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Food Delivery Order Management API")
                .version("v1")
                .description("API documentation for the Food Delivery Order Management System."));
    }
}
