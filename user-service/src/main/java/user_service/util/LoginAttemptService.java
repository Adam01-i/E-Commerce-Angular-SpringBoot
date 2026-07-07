package user_service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Composant assurant le verrouillage temporaire d'un compte après plusieurs
 * échecs de connexion consécutifs (règle de sécurité du cahier des charges).
 *
 * Le schéma de la table "users" imposé par le cahier des charges ne prévoit
 * aucune colonne dédiée au comptage des tentatives échouées : ce suivi est
 * donc réalisé en mémoire, par instance du service, via une simple table de
 * hachage indexée par email. Ce choix est cohérent avec un projet académique
 * mono-instance ; dans un contexte de production avec plusieurs instances du
 * service, ce composant serait remplacé par un stockage partagé (Redis).
 */
@Component
public class LoginAttemptService {

    @Value("${application.security-policy.max-failed-attempts}")
    private int maxFailedAttempts;

    @Value("${application.security-policy.lock-duration-minutes}")
    private long lockDurationMinutes;

    private final Map<String, Integer> attemptsByEmail = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockedUntilByEmail = new ConcurrentHashMap<>();

    /**
     * Enregistre un échec de connexion pour l'email donné et verrouille
     * temporairement le compte si le nombre maximal d'échecs est atteint.
     */
    public void registerFailedAttempt(String email) {
        int attempts = attemptsByEmail.merge(email, 1, Integer::sum);
        if (attempts >= maxFailedAttempts) {
            lockedUntilByEmail.put(email, LocalDateTime.now().plusMinutes(lockDurationMinutes));
        }
    }

    /**
     * Réinitialise le compteur d'échecs après une connexion réussie.
     */
    public void resetAttempts(String email) {
        attemptsByEmail.remove(email);
        lockedUntilByEmail.remove(email);
    }

    /**
     * Indique si le compte associé à l'email est actuellement verrouillé.
     */
    public boolean isLocked(String email) {
        LocalDateTime lockedUntil = lockedUntilByEmail.get(email);
        if (lockedUntil == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(lockedUntil)) {
            lockedUntilByEmail.remove(email);
            attemptsByEmail.remove(email);
            return false;
        }
        return true;
    }
}
