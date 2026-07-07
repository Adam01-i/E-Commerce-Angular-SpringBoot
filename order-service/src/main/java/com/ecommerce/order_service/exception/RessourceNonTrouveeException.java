package com.ecommerce.order_service.exception;

public class RessourceNonTrouveeException extends RuntimeException {
    public RessourceNonTrouveeException(String message) {
        super(message);
    }
}
