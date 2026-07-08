package com.ecommerce.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Version allégée de la catégorie, utilisée uniquement en imbrication dans
 * ProductResponseDTO. Casse volontairement le cycle Product -> Category ->
 * produits -> Product... qui provoquait la sérialisation infinie côté entités :
 * cette classe ne porte pas la liste des produits de la catégorie.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySimpleDTO {
    private Long id;
    private String nom;
}
