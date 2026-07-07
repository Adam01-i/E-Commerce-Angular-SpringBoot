package com.ecommerce.api_gateway.security;

/**
 * Identité extraite d'un jeton JWT valide, telle qu'émise par le user-service
 * (claims "id", "role" et "sub" pour l'email).
 */
public record AuthenticatedUser(Long id, String email, String role) {

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
