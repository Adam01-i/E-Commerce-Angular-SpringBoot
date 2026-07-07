package user_service.exception;

/**
 * Levée lorsque l'email ou le mot de passe fourni lors d'une tentative de
 * connexion est incorrect, ou lorsque l'ancien mot de passe fourni lors
 * d'un changement de mot de passe ne correspond pas.
 * Traduite par le GlobalExceptionHandler en réponse HTTP 401 UNAUTHORIZED.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
