package user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO retourné par POST /api/auth/refresh-token.
 * Un nouveau jeton d'accès est émis ; le jeton de rafraîchissement fourni
 * en entrée reste valide jusqu'à sa propre expiration (aucune rotation
 * n'est demandée par le cahier des charges).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
