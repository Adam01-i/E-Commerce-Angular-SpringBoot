package user_service.exception;

/**
 * Levée lorsqu'une opération est tentée sans les droits suffisants, ou
 * lorsqu'un compte désactivé tente de s'authentifier.
 * Traduite par le GlobalExceptionHandler en réponse HTTP 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
