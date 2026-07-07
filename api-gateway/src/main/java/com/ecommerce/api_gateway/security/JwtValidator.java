package com.ecommerce.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Valide les jetons JWT émis par le user-service. Utilise la même clé de
 * signature (secret partagé via la variable d'environnement JWT_SECRET) :
 * la Gateway ne fait qu'authentifier, elle n'émet jamais de jeton elle-même.
 */
@Component
public class JwtValidator {

    private final SecretKey signingKey;

    public JwtValidator(@Value("${application.security.jwt.secret-key}") String secretKey) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    /**
     * @return l'utilisateur authentifié si le jeton est syntaxiquement valide,
     * correctement signé, non expiré et de type "access".
     * @throws JwtException si le jeton est invalide pour n'importe quelle raison.
     */
    public AuthenticatedUser validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!"access".equals(claims.get("type", String.class))) {
            throw new JwtException("Le jeton fourni n'est pas un jeton d'accès");
        }

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);
        String email = claims.getSubject();

        return new AuthenticatedUser(id, email, role);
    }
}
