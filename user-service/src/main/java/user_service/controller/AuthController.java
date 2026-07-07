package user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.dto.JwtResponse;
import user_service.dto.LoginRequest;
import user_service.dto.RefreshTokenRequest;
import user_service.dto.RefreshTokenResponse;
import user_service.dto.RegisterRequest;
import user_service.service.AuthService;

/**
 * Contrôleur REST exposant les opérations publiques d'authentification.
 * Aucune logique métier n'est implémentée ici : chaque méthode se limite à
 * valider la requête entrante (via @Valid) et à déléguer le traitement à
 * AuthService.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion et rafraîchissement de jeton")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Créer un nouveau compte utilisateur")
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Authentifier un utilisateur et émettre un jeton JWT")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Rafraîchir un jeton d'accès expiré à partir d'un refresh token valide")
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
