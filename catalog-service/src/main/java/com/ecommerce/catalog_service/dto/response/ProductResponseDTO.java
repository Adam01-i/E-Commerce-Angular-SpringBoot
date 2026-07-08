package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO retourné par l'ensemble des endpoints /api/produits.
 * C'est le SEUL objet exposé au client pour un produit : le contrôleur ne
 * manipule jamais l'entité Product. category est une CategorySimpleDTO
 * (pas la liste de ses produits) et prixGros une liste de BulkPricingDTO
 * (pas de référence retour vers le produit), ce qui élimine tout risque de
 * cycle de sérialisation JSON.
 */

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private Integer stock;
    private String statut;
    private String imagePrincipale;
    private String imagesSecondaires;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private CategorySimpleDTO category;
    private List<BulkPricingDTO> prixGros;
}
