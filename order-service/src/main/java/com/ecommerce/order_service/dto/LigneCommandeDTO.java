package com.ecommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeDTO {
    private Long id;
    private Long produitId;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
