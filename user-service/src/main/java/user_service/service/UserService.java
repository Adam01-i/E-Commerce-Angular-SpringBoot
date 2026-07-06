package user_service.service;

import user_service.dto.ChangePasswordRequest;
import user_service.dto.UpdateProfileRequest;
import user_service.dto.UserResponse;

import java.util.List;

/**
 * Contrat des opérations de gestion des comptes utilisateurs, couvrant
 * aussi bien les actions du compte courant (profil, mot de passe) que les
 * actions d'administration (liste, détail, activation, désactivation).
 */
public interface UserService {

    /**
     * Retourne le profil de l'utilisateur actuellement authentifié.
     */
    UserResponse getCurrentUser(String email);

    /**
     * Met à jour les informations de profil de l'utilisateur authentifié
     * (nom, prénom, téléphone, adresse, avatar).
     */
    UserResponse updateProfile(String email, UpdateProfileRequest request);

    /**
     * Modifie le mot de passe de l'utilisateur authentifié, après
     * vérification de l'ancien mot de passe.
     */
    void changePassword(String email, ChangePasswordRequest request);

    /**
     * Active un compte utilisateur (opération réservée à l'administrateur).
     */
    UserResponse activateUser(Long userId);

    /**
     * Désactive un compte utilisateur, lui interdisant toute nouvelle
     * connexion (opération réservée à l'administrateur).
     */
    UserResponse deactivateUser(Long userId);

    /**
     * Retourne la liste de l'ensemble des utilisateurs (opération réservée
     * à l'administrateur).
     */
    List<UserResponse> findAllUsers();

    /**
     * Retourne le détail d'un utilisateur par son identifiant (opération
     * réservée à l'administrateur).
     */
    UserResponse findUserById(Long userId);
}
