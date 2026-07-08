package com.ecommerce.catalog_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO reçu par POST /api/categories et PUT /api/categories/{id}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequestDTO {

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private String description;

    @Size(max = 20, message = "Le statut ne doit pas dépasser 20 caractères")
    private String statut;
}
