package user_service.util;

/**
 * Constantes applicatives partagées entre les différentes couches du
 * microservice, afin d'éviter la duplication de chaînes de caractères
 * "magiques" à travers le code.
 */
public final class AppConstants {

    private AppConstants() {
        // Classe utilitaire : instanciation interdite.
    }

    public static final String TOKEN_TYPE_BEARER = "Bearer";

    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_TYPE_ACCESS = "access";
    public static final String CLAIM_TYPE_REFRESH = "refresh";

    public static final String ROLE_PREFIX = "ROLE_";
}
