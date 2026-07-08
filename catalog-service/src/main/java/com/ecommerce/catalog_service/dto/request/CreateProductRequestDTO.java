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
 * DTO reçu par POST /api/produits.
 * categoryId remplace toute référence directe à l'entité Category : le
 * client envoie un identifiant, jamais un objet catégorie complet.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequestDTO {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    private String nom;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix ne peut pas être négatif")
    private BigDecimal prix;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock;

    @Size(max = 20, message = "Le statut ne doit pas dépasser 20 caractères")
    private String statut;

    @Size(max = 255)
    private String imagePrincipale;

    private String imagesSecondaires;

    @NotNull(message = "L'identifiant de la catégorie est obligatoire")
    private Long categoryId;
}
