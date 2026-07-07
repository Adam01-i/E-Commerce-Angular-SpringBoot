package com.ecommerce.order_service.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitierPaiementRequest {
    private Long commandeId;
    private BigDecimal montant;
    private String modePaiement;
}
