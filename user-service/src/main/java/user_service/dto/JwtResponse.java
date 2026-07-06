package user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO retourné par POST /api/auth/login et POST /api/auth/register.
 * Contient le jeton d'accès, le jeton de rafraîchissement et les
 * informations essentielles de l'utilisateur authentifié.
 * Ne contient JAMAIS le mot de passe.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
}
