package com.ecommerce.catalog_service.mapper;

import com.ecommerce.catalog_service.dto.request.CreateCategoryRequestDTO;
import com.ecommerce.catalog_service.dto.response.CategoryResponseDTO;
import com.ecommerce.catalog_service.dto.response.CategorySimpleDTO;
import com.ecommerce.catalog_service.model.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper manuel Entity <-> DTO pour Category.
 *
 * Choix technique : mapper manuel plutôt que MapStruct. MapStruct nécessite
 * d'ajouter un processeur d'annotations (mapstruct-processor) à la
 * configuration Maven du compilateur, ce qui aurait modifié la chaîne de
 * build. Comme la consigne est de ne pas toucher au Dockerfile ni au
 * docker-compose (et donc de limiter les changements de build), un mapper
 * manuel explicite est plus sûr ici, quitte à migrer vers MapStruct plus
 * tard si le nombre d'entités grandit significativement.
 */
@Component
public class CategoryMapper {

    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .nom(category.getNom())
                .description(category.getDescription())
                .statut(category.getStatut())
                .build();
    }

    public CategorySimpleDTO toSimpleDTO(Category category) {
        if (category == null) {
            return null;
        }
        return CategorySimpleDTO.builder()
                .id(category.getId())
                .nom(category.getNom())
                .build();
    }

    public Category toEntity(CreateCategoryRequestDTO request) {
        Category category = new Category();
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());
        category.setStatut(request.getStatut() != null ? request.getStatut() : "ACTIF");
        return category;
    }
}
