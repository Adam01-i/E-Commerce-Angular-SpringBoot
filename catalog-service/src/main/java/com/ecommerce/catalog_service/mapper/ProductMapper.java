package com.ecommerce.catalog_service.mapper;

import com.ecommerce.catalog_service.dto.request.CreateProductRequestDTO;
import com.ecommerce.catalog_service.dto.response.ProductResponseDTO;
import com.ecommerce.catalog_service.model.BulkPricing;
import com.ecommerce.catalog_service.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper Entity <-> DTO pour Product.
 *
 * C'est ici que se résout la cause principale des erreurs 500 : toDTO()
 * est appelé DANS la transaction du service (voir CatalogServiceImpl,
 * méthodes @Transactional(readOnly = true)), donc la session Hibernate est
 * encore ouverte et category.getNom() ou la liste prixGros peuvent être
 * lues sans LazyInitializationException. Le contrôleur, lui, ne voit plus
 * jamais l'entité Product : il ne reçoit que ProductResponseDTO, déjà
 * entièrement construit et déconnecté de toute session Hibernate.
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final BulkPricingMapper bulkPricingMapper;

    public ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        List<BulkPricing> pricings = product.getPrixGros();

        return ProductResponseDTO.builder()
                .id(product.getId())
                .nom(product.getNom())
                .description(product.getDescription())
                .prix(product.getPrix())
                .stock(product.getStock())
                .statut(product.getStatut())
                .imagePrincipale(product.getImagePrincipale())
                .imagesSecondaires(product.getImagesSecondaires())
                .dateCreation(product.getDateCreation())
                .dateModification(product.getDateModification())
                .category(categoryMapper.toSimpleDTO(product.getCategory()))
                .prixGros(pricings == null ? Collections.emptyList()
                        : pricings.stream().map(bulkPricingMapper::toDTO).toList())
                .build();
    }

    /**
     * Construit une entité Product à partir d'une requête de création.
     * Ne renseigne PAS la catégorie ici : la résolution de categoryId en
     * entité Category (avec vérification d'existence -> 404 sinon) est une
     * responsabilité du service, pas du mapper, car elle nécessite un accès
     * au CategoryRepository.
     */
    public Product toEntity(CreateProductRequestDTO request) {
        Product product = new Product();
        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setStock(request.getStock());
        product.setStatut(request.getStatut() != null ? request.getStatut() : "ACTIF");
        product.setImagePrincipale(request.getImagePrincipale());
        product.setImagesSecondaires(request.getImagesSecondaires());
        return product;
    }
}
