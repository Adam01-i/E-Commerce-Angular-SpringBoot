package com.ecommerce.catalog_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO reçu par PUT /api/produits/{id}.
 * Ne porte volontairement pas de champ statut : le changement de statut
 * (masquer/afficher) passe par les endpoints dédiés /masquer et /afficher,
 * pas par une mise à jour générale, pour éviter qu'une modification de
 * fiche produit ne change accidentellement sa visibilité.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequestDTO {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nom;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Le prix ne peut pas être négatif")
    private BigDecimal prix;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0,
            message = "Le stock ne peut pas être négatif")
    private Integer stock;

    @Size(max = 255)
    private String imagePrincipale;

    private String imagesSecondaires;

    /**
     * Optionnel.
     *
     * - Si fourni : changement de catégorie.
     * - Si absent : conservation de la catégorie actuelle.
     */
    private Long categoryId;
}