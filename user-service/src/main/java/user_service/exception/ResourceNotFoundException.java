package user_service.exception;

/**
 * Levée lorsqu'une ressource demandée (typiquement un utilisateur par son
 * identifiant) n'existe pas en base de données.
 * Traduite par le GlobalExceptionHandler en réponse HTTP 404 NOT FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
