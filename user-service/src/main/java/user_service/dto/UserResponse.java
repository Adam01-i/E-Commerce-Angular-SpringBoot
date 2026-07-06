package user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import user_service.enums.Role;

import java.time.LocalDateTime;

/**
 * DTO exposé par l'ensemble des endpoints retournant des informations
 * utilisateur (profil courant, liste des utilisateurs, détail d'un
 * utilisateur). Le champ "password" de l'entité User n'est jamais exposé :
 * cette classe ne le porte pas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String avatarUrl;
    private Role profil;
    private Boolean isActive;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
