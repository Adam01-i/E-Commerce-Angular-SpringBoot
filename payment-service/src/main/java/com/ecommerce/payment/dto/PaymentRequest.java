package com.ecommerce.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotNull
        Long commandeId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal montant,

        @NotBlank
        String modePaiement

) {
}