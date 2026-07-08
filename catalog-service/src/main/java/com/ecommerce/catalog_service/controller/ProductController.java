package com.ecommerce.catalog_service.controller;

import com.ecommerce.catalog_service.dto.request.BulkPricingRequestDTO;
import com.ecommerce.catalog_service.dto.request.CreateProductRequestDTO;
import com.ecommerce.catalog_service.dto.request.UpdateProductRequestDTO;
import com.ecommerce.catalog_service.dto.response.BulkPricingDTO;
import com.ecommerce.catalog_service.dto.response.ProductResponseDTO;
import com.ecommerce.catalog_service.exception.BusinessException;
import com.ecommerce.catalog_service.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.catalog_service.dto.response.ProduitDisponibiliteResponseDTO;
import java.util.List;

/**
 * Contrôleur produits.
 *
 * Différence majeure avec l'ancienne version : plus aucun try/catch ici.
 * Les erreurs (produit introuvable, catégorie introuvable, quantité
 * invalide) sont levées comme des exceptions métier par le service et
 * interceptées globalement par GlobalExceptionHandler. Le contrôleur se
 * contente de recevoir un Request DTO, d'appeler le service, et de
 * retourner un Response DTO — jamais l'entité Product.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Produits & Prix de gros")
public class ProductController {

    private final CatalogService catalogService;

    // ==========================================
    // PRODUITS
    // ==========================================

    @GetMapping("/produits")
    @Operation(summary = "Lister les produits (public : uniquement ACTIF ; admin : statut=ALL, paginé)")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statut) {
        boolean includeHidden = "ALL".equalsIgnoreCase(statut);
        return ResponseEntity.ok(catalogService.getAllProducts(PageRequest.of(page, size), includeHidden));
    }

    @GetMapping("/produits/{id}")
    @Operation(summary = "Détail d'un produit (public)")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }

    @GetMapping("/produits/recherche")
    @Operation(summary = "Rechercher un produit (public)")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam("q") String query,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(catalogService.searchProducts(query, "ALL".equalsIgnoreCase(statut)));
    }

    @GetMapping("/produits/filtrer")
    @Operation(summary = "Filtrer par catégorie/prix/stock (public)")
    public ResponseEntity<List<ProductResponseDTO>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double maxPrix,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(
                catalogService.filterProducts(categoryId, maxPrix, minStock, "ALL".equalsIgnoreCase(statut)));
    }

    @PostMapping("/produits")
    @Operation(summary = "Créer un produit (admin)")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO request) {
        return new ResponseEntity<>(catalogService.saveProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/produits/{id}")
    @Operation(summary = "Modifier un produit (admin)")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id, @Valid @RequestBody UpdateProductRequestDTO request) {
        return ResponseEntity.ok(catalogService.updateProduct(id, request));
    }

    @DeleteMapping("/produits/{id}")
    @Operation(summary = "Supprimer un produit (admin)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        catalogService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/produits/{id}/masquer")
    @Operation(summary = "Masquer un produit (admin)")
    public ResponseEntity<ProductResponseDTO> maskProduct(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeProductStatus(id, "MASQUE"));
    }

    @PutMapping("/produits/{id}/afficher")
    @Operation(summary = "Afficher un produit (admin)")
    public ResponseEntity<ProductResponseDTO> showProduct(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeProductStatus(id, "ACTIF"));
    }

    @PutMapping("/produits/{id}/stock")
    @Operation(summary = "Réapprovisionner le stock (admin)")
    public ResponseEntity<ProductResponseDTO> restock(@PathVariable Long id, @RequestParam Integer quantite) {
        return ResponseEntity.ok(catalogService.restockProduct(id, quantite));
    }

    @Operation(summary = "Vérifier la disponibilité du stock (interne)")
    @GetMapping("/produits/{id}/disponibilite")
    public ResponseEntity<ProduitDisponibiliteResponseDTO> verifierDisponibilite(
            @PathVariable Long id,
            @RequestParam Integer quantite
    ) {

        ProductResponseDTO produit = catalogService.getProductById(id);

        boolean disponible =
                produit.getStock() >= quantite
                        && "ACTIF".equals(produit.getStatut());

        return ResponseEntity.ok(
                new ProduitDisponibiliteResponseDTO(
                        produit.getId(),
                        quantite,
                        disponible
                )
        );
    }

    // ==========================================
    // PRIX DE GROS
    // ==========================================

    @GetMapping("/produits/{id}/prix-gros")
    @Operation(summary = "Lister les paliers d'un produit")
    public ResponseEntity<List<BulkPricingDTO>> getProductBulkPricings(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getBulkPricingsByProduct(id));
    }

    @PostMapping("/produits/{id}/prix-gros")
    @Operation(summary = "Ajouter un palier (admin)")
    public ResponseEntity<BulkPricingDTO> addBulkPricing(
            @PathVariable Long id, @Valid @RequestBody BulkPricingRequestDTO request) {
        return new ResponseEntity<>(catalogService.addBulkPricing(id, request), HttpStatus.CREATED);
    }

    @PutMapping("/prix-gros/{id}")
    @Operation(summary = "Modifier un palier (admin)")
    public ResponseEntity<BulkPricingDTO> updateBulkPricing(
            @PathVariable Long id, @Valid @RequestBody BulkPricingRequestDTO request) {
        return ResponseEntity.ok(catalogService.updateBulkPricing(id, request));
    }

    @DeleteMapping("/prix-gros/{id}")
    @Operation(summary = "Supprimer un palier (admin)")
    public ResponseEntity<Void> deleteBulkPricing(@PathVariable Long id) {
        catalogService.deleteBulkPricing(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/prix-gros/{id}/activer")
    @Operation(summary = "Activer un palier (admin)")
    public ResponseEntity<BulkPricingDTO> activateBulkPricing(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeBulkPricingStatus(id, "ACTIF"));
    }

    @PutMapping("/prix-gros/{id}/desactiver")
    @Operation(summary = "Désactiver un palier (admin)")
    public ResponseEntity<BulkPricingDTO> deactivateBulkPricing(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeBulkPricingStatus(id, "INACTIF"));
    }
}
