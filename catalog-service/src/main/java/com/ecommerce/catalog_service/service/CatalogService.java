package com.ecommerce.catalog_service.service;

import com.ecommerce.catalog_service.model.Category;
import com.ecommerce.catalog_service.model.Product;
import com.ecommerce.catalog_service.model.BulkPricing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CatalogService {
    // --- Catégories ---
    Category saveCategory(Category category);
    List<Category> getAllCategories();
    Category updateCategory(Long id, Category categoryDetails);
    void deleteCategory(Long id);
    Category changeCategoryStatus(Long id, String status);

    // --- Produits ---
    Product saveProduct(Product product);
    Page<Product> getAllProducts(Pageable pageable, boolean includeHidden); // Paginé ; includeHidden réservé à l'admin (voit aussi les produits MASQUE)
    Product getProductById(Long id);
    List<Product> searchProducts(String query, boolean includeHidden);
    List<Product> filterProducts(Long categoryId, Double maxPrix, Integer minStock, boolean includeHidden);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    Product changeProductStatus(Long id, String status);
    Product restockProduct(Long id, Integer quantity);
    boolean checkAvailability(Long id, Integer quantity);

    // --- Prix de Gros ---
    List<BulkPricing> getBulkPricingsByProduct(Long productId);
    BulkPricing addBulkPricing(Long productId, BulkPricing bulkPricing);
    BulkPricing updateBulkPricing(Long id, BulkPricing bulkPricingDetails);
    void deleteBulkPricing(Long id);
    BulkPricing changeBulkPricingStatus(Long id, String status);
}