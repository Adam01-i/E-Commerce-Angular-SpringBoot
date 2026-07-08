package com.ecommerce.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Contrat consommé par order-service (OpenFeign, CatalogClient#verifierDisponibilite)
 * pour valider un ajout au panier ou une commande sans dupliquer la logique de stock/prix.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDisponibiliteDTO {
    private Long produitId;
    private boolean disponible;
    private Integer stock;
    private BigDecimal prixApplicable;
}
