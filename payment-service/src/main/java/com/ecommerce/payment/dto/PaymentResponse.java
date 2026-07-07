package com.ecommerce.payment.dto;

import com.ecommerce.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,
        Long commandeId,
        BigDecimal montant,
        String modePaiement,
        PaymentStatus statut,
        LocalDateTime datePaiement

) {
}