package user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO reçu par PUT /api/users/me.
 * Permet à l'utilisateur connecté de modifier ses informations de profil.
 * L'email, le mot de passe et le rôle ne sont volontairement pas modifiables
 * via ce endpoint : l'email est l'identifiant de connexion (non prévu dans
 * le cahier des charges pour être modifiable ici), et le mot de passe suit
 * son propre flux dédié via ChangePasswordRequest.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    @Size(max = 255, message = "L'URL de l'avatar ne doit pas dépasser 255 caractères")
    private String avatarUrl;
}
