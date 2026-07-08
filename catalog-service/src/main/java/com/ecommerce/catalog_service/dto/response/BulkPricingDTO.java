package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO retourné pour chaque palier de prix de gros, aussi bien seul
 * (GET /api/produits/{id}/prix-gros) qu'imbriqué dans ProductResponseDTO.
 * Ne porte pas de référence vers le produit parent : la relation product
 * de l'entité BulkPricing n'est jamais exposée, évitant tout cycle.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPricingDTO {
    private Long id;
    private Integer quantiteMinimale;
    private BigDecimal prix;
    private String statut;
}
