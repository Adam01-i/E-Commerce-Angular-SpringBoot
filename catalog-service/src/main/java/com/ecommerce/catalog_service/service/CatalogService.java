package com.ecommerce.catalog_service.service;

import com.ecommerce.catalog_service.dto.request.BulkPricingRequestDTO;
import com.ecommerce.catalog_service.dto.request.CreateCategoryRequestDTO;
import com.ecommerce.catalog_service.dto.request.CreateProductRequestDTO;
import com.ecommerce.catalog_service.dto.request.UpdateProductRequestDTO;
import com.ecommerce.catalog_service.dto.response.BulkPricingDTO;
import com.ecommerce.catalog_service.dto.response.CategoryResponseDTO;
import com.ecommerce.catalog_service.dto.response.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrat du service catalogue. Toutes les méthodes retournent des DTO :
 * plus aucune entité JPA ne traverse la frontière service -> controller.
 */
public interface CatalogService {

    // --- Catégories ---
    CategoryResponseDTO saveCategory(CreateCategoryRequestDTO request);
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO updateCategory(Long id, CreateCategoryRequestDTO request);
    void deleteCategory(Long id);
    CategoryResponseDTO changeCategoryStatus(Long id, String status);

    // --- Produits ---
    ProductResponseDTO saveProduct(CreateProductRequestDTO request);
    Page<ProductResponseDTO> getAllProducts(Pageable pageable, boolean includeHidden);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> searchProducts(String query, boolean includeHidden);
    List<ProductResponseDTO> filterProducts(Long categoryId, Double maxPrix, Integer minStock, boolean includeHidden);
    ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO request);
    void deleteProduct(Long id);
    ProductResponseDTO changeProductStatus(Long id, String status);
    ProductResponseDTO restockProduct(Long id, Integer quantity);
    boolean checkAvailability(Long id, Integer quantity);

    // --- Prix de gros ---
    List<BulkPricingDTO> getBulkPricingsByProduct(Long productId);
    BulkPricingDTO addBulkPricing(Long productId, BulkPricingRequestDTO request);
    BulkPricingDTO updateBulkPricing(Long id, BulkPricingRequestDTO request);
    void deleteBulkPricing(Long id);
    BulkPricingDTO changeBulkPricingStatus(Long id, String status);
}
