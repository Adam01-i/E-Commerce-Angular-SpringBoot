package com.ecommerce.catalog_service.service;

import com.ecommerce.catalog_service.dto.request.BulkPricingRequestDTO;
import com.ecommerce.catalog_service.dto.request.CreateCategoryRequestDTO;
import com.ecommerce.catalog_service.dto.request.CreateProductRequestDTO;
import com.ecommerce.catalog_service.dto.request.UpdateProductRequestDTO;
import com.ecommerce.catalog_service.dto.response.BulkPricingDTO;
import com.ecommerce.catalog_service.dto.response.CategoryResponseDTO;
import com.ecommerce.catalog_service.dto.response.ProductResponseDTO;
import com.ecommerce.catalog_service.exception.BusinessException;
import com.ecommerce.catalog_service.exception.ResourceNotFoundException;
import com.ecommerce.catalog_service.mapper.BulkPricingMapper;
import com.ecommerce.catalog_service.mapper.CategoryMapper;
import com.ecommerce.catalog_service.mapper.ProductMapper;
import com.ecommerce.catalog_service.model.BulkPricing;
import com.ecommerce.catalog_service.model.Category;
import com.ecommerce.catalog_service.model.Product;
import com.ecommerce.catalog_service.repository.BulkPricingRepository;
import com.ecommerce.catalog_service.repository.CategoryRepository;
import com.ecommerce.catalog_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service catalogue.
 *
 * Chaque méthode de lecture est annotée @Transactional(readOnly = true),
 * garantissant que la session Hibernate reste ouverte pendant toute la
 * construction du DTO par le mapper (résolution des relations LAZY comprise) :
 * c'est ce qui élimine la LazyInitializationException et, par conséquent,
 * les erreurs 500 sur GET /api/produits/{id}. Les méthodes d'écriture
 * restent en @Transactional classique.
 */
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BulkPricingRepository bulkPricingRepository;

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final BulkPricingMapper bulkPricingMapper;

    // ==========================================
    // CATÉGORIES
    // ==========================================

    @Override
    @Transactional
    public CategoryResponseDTO saveCategory(CreateCategoryRequestDTO request) {
        if (categoryRepository.findByNom(request.getNom()).isPresent()) {
            throw new BusinessException("Une catégorie avec ce nom existe déjà", true);
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CreateCategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());
        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        if (!category.getProduits().isEmpty()) {
            throw new BusinessException("Impossible de supprimer une catégorie contenant des produits", true);
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO changeCategoryStatus(Long id, String status) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
        category.setStatut(status);
        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    // ==========================================
    // PRODUITS
    // ==========================================

    @Override
    @Transactional
    public ProductResponseDTO saveProduct(CreateProductRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie introuvable avec l'id : " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable, boolean includeHidden) {
        Page<Product> page = includeHidden
                ? productRepository.findAll(pageable)
                : productRepository.findByStatut("ACTIF", pageable);
        return page.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String query, boolean includeHidden) {
        List<Product> results = productRepository.findByNomContainingIgnoreCase(query);
        return results.stream()
                .filter(p -> includeHidden || "ACTIF".equals(p.getStatut()))
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> filterProducts(Long categoryId, Double maxPrix, Integer minStock, boolean includeHidden) {
        List<Product> results = categoryId != null
                ? productRepository.findByCategoryId(categoryId)
                : productRepository.findAll();

        return results.stream()
                .filter(p -> includeHidden || "ACTIF".equals(p.getStatut()))
                .filter(p -> maxPrix == null || p.getPrix().doubleValue() <= maxPrix)
                .filter(p -> minStock == null || p.getStock() >= minStock)
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO request) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setStock(request.getStock());
        product.setImagePrincipale(request.getImagePrincipale());
        product.setImagesSecondaires(request.getImagesSecondaires());

        if (request.getCategoryId() != null
                && (product.getCategory() == null
                || !request.getCategoryId().equals(product.getCategory().getId()))) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Catégorie introuvable avec l'id : "
                                            + request.getCategoryId()));

            product.setCategory(category);
        }

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        // Remarque : la vérification "produit présent dans une commande déjà payée"
        // relève de l'Order Service ; ce contrôle inter-service sera ajouté via
        // OpenFeign lorsque l'Order Service sera développé.
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO changeProductStatus(Long id, String status) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        product.setStatut(status);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDTO restockProduct(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("La quantité de réapprovisionnement doit être strictement positive");
        }
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        product.setStock(product.getStock() + quantity);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
        if ("MASQUE".equals(product.getStatut())) {
            return false;
        }
        return product.getStock() >= quantity;
    }

    // ==========================================
    // PRIX DE GROS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<BulkPricingDTO> getBulkPricingsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produit introuvable avec l'id : " + productId);
        }
        return bulkPricingRepository.findByProductId(productId).stream()
                .map(bulkPricingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BulkPricingDTO addBulkPricing(Long productId, BulkPricingRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + productId));

        BulkPricing bulkPricing = bulkPricingMapper.toEntity(request);
        bulkPricing.setProduct(product);
        return bulkPricingMapper.toDTO(bulkPricingRepository.save(bulkPricing));
    }

    @Override
    @Transactional
    public BulkPricingDTO updateBulkPricing(Long id, BulkPricingRequestDTO request) {
        BulkPricing bulkPricing = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Palier introuvable avec l'id : " + id));
        bulkPricing.setQuantiteMinimale(request.getQuantiteMinimale());
        bulkPricing.setPrix(request.getPrix());
        return bulkPricingMapper.toDTO(bulkPricingRepository.save(bulkPricing));
    }

    @Override
    @Transactional
    public void deleteBulkPricing(Long id) {
        BulkPricing bulkPricing = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Palier introuvable avec l'id : " + id));
        bulkPricingRepository.delete(bulkPricing);
    }

    @Override
    @Transactional
    public BulkPricingDTO changeBulkPricingStatus(Long id, String status) {
        BulkPricing bulkPricing = bulkPricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Palier introuvable avec l'id : " + id));
        bulkPricing.setStatut(status);
        return bulkPricingMapper.toDTO(bulkPricingRepository.save(bulkPricing));
    }
}
