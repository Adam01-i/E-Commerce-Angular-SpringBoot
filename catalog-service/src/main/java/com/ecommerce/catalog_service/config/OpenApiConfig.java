package com.ecommerce.catalog_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog Service API")
                        .version("1.0.0")
                        .description("API du microservice de gestion du catalogue (produits, catégories, " +
                                "stocks, prix de gros) pour la plateforme E-Commerce. Toutes les réponses " +
                                "utilisent des DTO dédiés (ProductResponseDTO, CategoryResponseDTO, " +
                                "BulkPricingDTO) : aucune entité JPA n'est jamais exposée directement."))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Serveur local - Catalog Service")
                ));
    }
}
