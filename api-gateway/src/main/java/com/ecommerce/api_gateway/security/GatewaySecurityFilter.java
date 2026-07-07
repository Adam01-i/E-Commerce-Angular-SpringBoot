package com.ecommerce.api_gateway.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Point d'entrée unique de sécurité de l'API Gateway (cahier des charges 4.2/4.7) :
 * vérifie la présence et la validité du jeton JWT avant transmission au microservice
 * cible, et applique le contrôle de rôle (ADMIN) sur les routes qui l'exigent.
 * Ne contient aucune logique métier : uniquement de l'authentification/autorisation.
 *
 * Enregistré explicitement (avec ordre précis face au CorsFilter) par GatewayConfig,
 * plutôt que via @Component, pour éviter un double enregistrement comme filtre servlet.
 */
public class GatewaySecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(GatewaySecurityFilter.class);
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    // Routes internes réservées aux appels OpenFeign directs entre microservices :
    // jamais accessibles depuis l'extérieur via la Gateway.
    private static final String INTERNAL_SEGMENT = "/internal/";

    // Accessibles sans jeton, quelle que soit la méthode.
    private static final List<String> PUBLIC_ANY_METHOD = List.of(
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**"
    );

    // Accessibles sans jeton, uniquement en GET (consultation libre du catalogue - cahier 2.1.1).
    private static final List<String> PUBLIC_GET_ONLY = List.of(
            "/api/produits/**",
            "/api/categories/**"
    );

    // Nécessitent un jeton valide, mais réservées au rôle ADMIN.
    private static final List<AdminRule> ADMIN_RULES = List.of(
            new AdminRule(null, "/api/prix-gros/**"),
            new AdminRule("POST", "/api/produits/**"),
            new AdminRule("PUT", "/api/produits/**"),
            new AdminRule("DELETE", "/api/produits/**"),
            new AdminRule("POST", "/api/categories/**"),
            new AdminRule("PUT", "/api/categories/**"),
            new AdminRule("DELETE", "/api/categories/**"),
            new AdminRule("GET", "/api/commandes/admin"),
            new AdminRule("PUT", "/api/commandes/*/statut"),
            new AdminRule("GET", "/api/users"),
            new AdminRule("GET", "/api/users/*"),
            new AdminRule("PUT", "/api/users/*/activate"),
            new AdminRule("PUT", "/api/users/*/deactivate"),
            new AdminRule("GET", "/api/payments")
    );

    private final JwtValidator jwtValidator;

    public GatewaySecurityFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (HttpMethod.OPTIONS.matches(method)) {
            chain.doFilter(request, response);
            return;
        }

        if (path.contains(INTERNAL_SEGMENT)) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Ressource introuvable");
            return;
        }

        if (matchesAny(PUBLIC_ANY_METHOD, path)) {
            chain.doFilter(request, response);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && matchesAny(PUBLIC_GET_ONLY, path)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractBearerToken(request);
        if (token == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise");
            return;
        }

        AuthenticatedUser user;
        try {
            user = jwtValidator.validate(token);
        } catch (JwtException | IllegalArgumentException ex) {
            logger.debug("Jeton invalide : {}", ex.getMessage());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Jeton invalide ou expiré");
            return;
        }

        boolean requiresAdmin = ADMIN_RULES.stream().anyMatch(rule -> rule.matches(method, path));

        if (requiresAdmin && !user.isAdmin()) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "Accès réservé à l'administrateur");
            return;
        }

        chain.doFilter(new UserContextRequestWrapper(request, user), response);
    }

    private boolean matchesAny(List<String> patterns, String path) {
        return patterns.stream().anyMatch(p -> MATCHER.match(p, path));
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // Corps d'erreur entièrement construit à partir de valeurs internes (jamais d'entrée
    // utilisateur), une sérialisation JSON manuelle suffit donc sans dépendance Jackson.
    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"timestamp":"%s","status":%d,"message":"%s"}""".formatted(
                Instant.now(), status, message));
    }

    private record AdminRule(String method, String pattern) {
        boolean matches(String requestMethod, String requestPath) {
            if (pattern == null) {
                return false;
            }
            boolean methodOk = method == null || method.equalsIgnoreCase(requestMethod);
            return methodOk && MATCHER.match(pattern, requestPath);
        }
    }

    /**
     * Repropage l'identité extraite du jeton vers les microservices en aval sous forme
     * de headers de confiance (X-User-*). La Gateway étant l'unique point d'entrée,
     * ces headers ne peuvent pas être falsifiés par un client externe.
     */
    private static final class UserContextRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> overrides;

        UserContextRequestWrapper(HttpServletRequest request, AuthenticatedUser user) {
            super(request);
            this.overrides = Map.of(
                    "X-User-Id", String.valueOf(user.id()),
                    "X-User-Email", user.email() == null ? "" : user.email(),
                    "X-User-Role", user.role() == null ? "" : user.role()
            );
        }

        @Override
        public String getHeader(String name) {
            String override = overrides.get(name);
            return override != null ? override : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String override = overrides.get(name);
            return override != null
                    ? Collections.enumeration(List.of(override))
                    : super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            for (String key : overrides.keySet()) {
                if (!names.contains(key)) {
                    names.add(key);
                }
            }
            return Collections.enumeration(names);
        }
    }
}
