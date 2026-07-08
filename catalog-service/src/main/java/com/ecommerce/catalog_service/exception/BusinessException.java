package com.ecommerce.catalog_service.exception;

/**
 * Levée pour toute violation d'une règle métier : nom de catégorie déjà
 * utilisé, suppression d'une catégorie contenant des produits, quantité de
 * réapprovisionnement invalide, etc. Traduite en HTTP 400 ou 409 selon le
 * cas par GlobalExceptionHandler (voir le champ conflict).
 */
public class BusinessException extends RuntimeException {

    private final boolean conflict;

    public BusinessException(String message) {
        super(message);
        this.conflict = false;
    }

    public BusinessException(String message, boolean conflict) {
        super(message);
        this.conflict = conflict;
    }

    public boolean isConflict() {
        return conflict;
    }
}
