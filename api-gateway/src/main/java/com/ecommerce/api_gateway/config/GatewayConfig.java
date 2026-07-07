package com.ecommerce.api_gateway.config;

import com.ecommerce.api_gateway.security.GatewaySecurityFilter;
import com.ecommerce.api_gateway.security.JwtValidator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Câblage transversal de la Gateway : CORS (seule origine autorisée : le Frontend
 * Angular) et enregistrement explicite du filtre de sécurité JWT, dans cet ordre
 * (CORS avant sécurité, y compris sur les réponses d'erreur 401/403).
 */
@Configuration
public class GatewayConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsFilter corsFilter) {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public GatewaySecurityFilter gatewaySecurityFilter(JwtValidator jwtValidator) {
        return new GatewaySecurityFilter(jwtValidator);
    }

    @Bean
    public FilterRegistrationBean<GatewaySecurityFilter> gatewaySecurityFilterRegistration(
            GatewaySecurityFilter gatewaySecurityFilter) {
        FilterRegistrationBean<GatewaySecurityFilter> registration =
                new FilterRegistrationBean<>(gatewaySecurityFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }
}
