package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO retourné par les endpoints /api/categories.
 * Ne porte pas la liste des produits : consulter GET /api/produits?categoryId=
 * pour obtenir les produits d'une catégorie, plutôt que de les imbriquer ici
 * (évite de renvoyer une liste potentiellement énorme à chaque appel catégorie).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private String statut;
}
