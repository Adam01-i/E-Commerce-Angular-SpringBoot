package com.ecommerce.catalog_service.service;

import com.ecommerce.catalog_service.model.Category;
import com.ecommerce.catalog_service.model.Product;
import com.ecommerce.catalog_service.model.BulkPricing;
import com.ecommerce.catalog_service.repository.CategoryRepository;
import com.ecommerce.catalog_service.repository.ProductRepository;
import com.ecommerce.catalog_service.repository.BulkPricingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BulkPricingRepository bulkPricingRepository;

    // ==========================================
    // LOGIQUE CATÉGORIES
    // ==========================================
    @Override
    public Category saveCategory(Category category) {
        if(categoryRepository.findByNom(category.getNom()).isPresent()) {
            throw new IllegalStateException("Une catégorie avec ce nom existe déjà"); // Provoquera un 400 ou 409
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        category.setNom(categoryDetails.getNom());
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        if (category.getProduits() != null && !category.getProduits().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une catégorie contenant des produits"); // Conflit 409
        }
        categoryRepository.delete(category);
    }

    @Override
    public Category changeCategoryStatus(Long id, String status) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        category.setStatut(status);
        return categoryRepository.save(category);
    }

    // ==========================================
    // LOGIQUE PRODUITS
    // ==========================================
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable, boolean includeHidden) {
        if (includeHidden) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByStatut("ACTIF", pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
    }

    @Override
    public List<Product> searchProducts(String query, boolean includeHidden) {
        List<Product> results = productRepository.findByNomContainingIgnoreCase(query);
        return includeHidden ? results : filterActifs(results);
    }

    @Override
    public List<Product> filterProducts(Long categoryId, Double maxPrix, Integer minStock, boolean includeHidden) {
        // Idéalement à optimiser avec des Specifications JPA, mais voici une version simple
        List<Product> results = categoryId != null
                ? productRepository.findByCategoryId(categoryId)
                : productRepository.findAll();
        return includeHidden ? results : filterActifs(results);
    }

    // Le catalogue public (visiteur/utilisateur) ne doit jamais exposer un produit masqué par l'admin.
    private List<Product> filterActifs(List<Product> products) {
        return products.stream().filter(p -> "ACTIF".equals(p.getStatut())).toList();
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setNom(productDetails.getNom());
        product.setDescription(productDetails.getDescription());
        product.setPrix(productDetails.getPrix());
        product.setStock(productDetails.getStock());
        product.setImagePrincipale(productDetails.getImagePrincipale());
        product.setImagesSecondaires(productDetails.getImagesSecondaires());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        // On pourrait vérifier s'il est présent dans des commandes en cours (via un autre service plus tard)
        productRepository.delete(product);
    }

    @Override
    public Product changeProductStatus(Long id, String status) {
        Product product = getProductById(id);
        product.setStatut(status);
        return productRepository.save(product);
    }

    @Override
    public Product restockProduct(Long id, Integer quantity) {
        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        return productRepository.save(product);
    }

    @Override
    public boolean checkAvailability(Long id, Integer quantity) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null || "MASQUE".equals(product.getStatut())) {
            return false;
        }
        return product.getStock() >= quantity;
    }

    // ==========================================
    // LOGIQUE PRIX DE GROS
    // ==========================================
    @Override
    public List<BulkPricing> getBulkPricingsByProduct(Long productId) {
        getProductById(productId); // Vérifie si le produit existe (404 si absent)
        return bulkPricingRepository.findByProductId(productId);
    }

    @Override
    public BulkPricing addBulkPricing(Long productId, BulkPricing bulkPricing) {
        Product product = getProductById(productId);
        bulkPricing.setProduct(product);
        return bulkPricingRepository.save(bulkPricing);
    }

    @Override
    public BulkPricing updateBulkPricing(Long id, BulkPricing bulkPricingDetails) {
        BulkPricing bp = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Palier introuvable"));
        bp.setQuantiteMinimale(bulkPricingDetails.getQuantiteMinimale());
        bp.setPrix(bulkPricingDetails.getPrix());
        return bulkPricingRepository.save(bp);
    }

    @Override
    public void deleteBulkPricing(Long id) {
        BulkPricing bp = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Palier introuvable"));
        bulkPricingRepository.delete(bp);
    }

    @Override
    public BulkPricing changeBulkPricingStatus(Long id, String status) {
        BulkPricing bp = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Palier introuvable"));
        bp.setStatut(status);
        return bulkPricingRepository.save(bp);
    }
}