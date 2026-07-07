package user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import user_service.dto.ChangePasswordRequest;
import user_service.dto.UpdateProfileRequest;
import user_service.dto.UserResponse;
import user_service.service.UserService;

import java.util.List;

/**
 * Contrôleur REST exposant :
 *  - les opérations sur le compte de l'utilisateur authentifié (/me/**),
 *    accessibles à tout utilisateur connecté quel que soit son rôle ;
 *  - les opérations d'administration des comptes, protégées par
 *    @PreAuthorize("hasRole('ADMIN')").
 *
 * L'email de l'utilisateur courant est extrait du jeton JWT via l'objet
 * Authentication injecté par Spring Security (renseigné par
 * JwtAuthenticationFilter), jamais transmis explicitement par le client.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion du profil et administration des comptes")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Consulter son propre profil")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @Operation(summary = "Modifier son propre profil")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    @Operation(summary = "Modifier son propre mot de passe")
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lister l'ensemble des utilisateurs (réservé à l'administrateur)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(summary = "Consulter le détail d'un utilisateur (réservé à l'administrateur)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @Operation(summary = "Recherche interne utilisateur")
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getUserInternal(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                userService.findUserById(id)
        );
    }

    @Operation(summary = "Activer un compte utilisateur (réservé à l'administrateur)")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @Operation(summary = "Désactiver un compte utilisateur (réservé à l'administrateur)")
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}
