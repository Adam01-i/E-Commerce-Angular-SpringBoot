package user_service.exception;

/**
 * Exception générique utilisée pour toute violation d'une règle métier ne
 * relevant pas des cas plus spécifiques ci-dessus (par exemple : compte
 * verrouillé temporairement après plusieurs échecs de connexion).
 * Traduite par le GlobalExceptionHandler en réponse HTTP 400 BAD REQUEST.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
