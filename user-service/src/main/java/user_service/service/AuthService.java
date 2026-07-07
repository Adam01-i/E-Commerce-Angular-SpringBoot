package user_service.service;

import user_service.dto.JwtResponse;
import user_service.dto.LoginRequest;
import user_service.dto.RefreshTokenRequest;
import user_service.dto.RefreshTokenResponse;
import user_service.dto.RegisterRequest;

/**
 * Contrat des opérations d'authentification exposées par le User/Auth Service.
 */
public interface AuthService {

    /**
     * Crée un nouveau compte utilisateur avec le rôle UTILISATEUR par défaut,
     * après vérification de l'unicité de l'adresse email et chiffrement du
     * mot de passe avec BCrypt, puis retourne directement une paire de
     * jetons (accès + rafraîchissement) afin de connecter immédiatement
     * l'utilisateur nouvellement inscrit.
     */
    JwtResponse register(RegisterRequest request);

    /**
     * Authentifie un utilisateur à partir de son email et de son mot de
     * passe, vérifie que le compte est actif et non verrouillé, puis
     * retourne une paire de jetons (accès + rafraîchissement).
     */
    JwtResponse login(LoginRequest request);

    /**
     * Vérifie la validité d'un jeton de rafraîchissement et, si celui-ci
     * est valide, émet un nouveau jeton d'accès sans exiger une nouvelle
     * saisie des identifiants.
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
