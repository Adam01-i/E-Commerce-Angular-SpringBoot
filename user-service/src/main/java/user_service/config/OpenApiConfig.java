package user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration de la documentation Swagger/OpenAPI du User/Auth Service.
 * Déclare le titre, la version, la description, le(s) serveur(s) exposés
 * ainsi que le schéma de sécurité Bearer JWT, permettant de tester
 * directement les endpoints protégés depuis l'interface Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User/Auth Service API")
                        .version("1.0.0")
                        .description("API du microservice de gestion des utilisateurs et de l'authentification " +
                                "pour la plateforme E-Commerce (inscription, connexion, JWT, profil, rôles).")
                        .contact(new Contact()
                                .name("Équipe Projet E-Commerce")
                                .email("contact@ecommerce-projet.local")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Serveur local - User Service"),
                        new Server().url("http://localhost:8080/user-service").description("Via API Gateway")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
