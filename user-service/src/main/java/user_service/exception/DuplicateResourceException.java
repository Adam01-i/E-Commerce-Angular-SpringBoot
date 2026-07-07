package user_service.exception;

/**
 * Levée lorsqu'une tentative de création entrerait en conflit avec une
 * ressource déjà existante (typiquement un email déjà utilisé lors de
 * l'inscription).
 * Traduite par le GlobalExceptionHandler en réponse HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
