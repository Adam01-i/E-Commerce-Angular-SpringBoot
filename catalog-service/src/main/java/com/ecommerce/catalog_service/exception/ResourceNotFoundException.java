package com.ecommerce.catalog_service.exception;

/**
 * Levée lorsqu'une ressource demandée (produit, catégorie, palier de prix
 * de gros) n'existe pas en base. Traduite en HTTP 404 par GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
