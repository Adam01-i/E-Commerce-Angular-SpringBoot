package com.ecommerce.payment.exception;

public class PaymentAlreadyValidatedException extends RuntimeException {

    public PaymentAlreadyValidatedException(String message) {
        super(message);
    }

}