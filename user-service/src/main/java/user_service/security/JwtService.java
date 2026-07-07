package user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsable de la génération et de la validation des jetons JWT.
 *
 * Deux types de jetons distincts sont émis, différenciés par le claim
 * "type" ("access" / "refresh") :
 *  - le jeton d'ACCÈS, de courte durée de vie, transmis à chaque requête
 *    authentifiée dans l'en-tête Authorization ;
 *  - le jeton de RAFRAÎCHISSEMENT, de plus longue durée de vie, utilisé
 *    exclusivement par /api/auth/refresh-token pour obtenir un nouveau
 *    jeton d'accès sans redemander les identifiants.
 *
 * Conformément au cahier des charges, aucun jeton n'est persisté en base
 * (pas de table dédiée) : la validité d'un jeton de rafraîchissement repose
 * uniquement sur sa signature et sa date d'expiration (approche stateless).
 * Les deux secrets utilisent la même clé mais des claims différents afin de
 * garder une implémentation simple ; le claim "type" empêche qu'un jeton de
 * rafraîchissement soit utilisé à la place d'un jeton d'accès et inversement.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${application.security.jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${application.security.jwt.issuer}") String issuer) {
        this.signingKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
    }

    /**
     * Génère un jeton d'ACCÈS contenant l'identifiant, l'email et le rôle
     * de l'utilisateur.
     */
    public String generateAccessToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("role", role);
        claims.put("type", "access");
        return buildToken(claims, email, accessTokenExpiration);
    }

    /**
     * Génère un jeton de RAFRAÎCHISSEMENT contenant uniquement l'identifiant
     * et l'email de l'utilisateur (le rôle n'est pas nécessaire pour un
     * simple renouvellement de jeton d'accès).
     */
    public String generateRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("type", "refresh");
        return buildToken(claims, email, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Valide qu'un jeton est syntaxiquement correct, correctement signé,
     * non expiré, et correspond bien à l'email attendu.
     */
    public boolean isTokenValid(String token, String expectedEmail) {
        try {
            String email = extractEmail(token);
            return email.equals(expectedEmail) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Valide spécifiquement qu'un jeton est bien un jeton de rafraîchissement
     * valide (signature, expiration, claim type = "refresh").
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            boolean isRefreshType = "refresh".equals(claims.get("type", String.class));
            boolean notExpired = claims.getExpiration().after(new Date());

            return isRefreshType && notExpired;

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
