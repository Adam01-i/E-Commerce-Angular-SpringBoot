package com.ecommerce.catalog_service.controller;

import com.ecommerce.catalog_service.dto.ProduitDisponibiliteDTO;
import com.ecommerce.catalog_service.model.Product;
import com.ecommerce.catalog_service.model.BulkPricing;
import com.ecommerce.catalog_service.service.CatalogService;
import com.ecommerce.catalog_service.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "7.2.1 Produits & Prix de gros")
public class ProductController {

    private final CatalogService catalogService;
    private final ImageStorageService imageStorageService;

    // ==========================================
    // SECTION : PRODUITS
    // ==========================================

    @GetMapping("/produits")
    @Operation(summary = "Lister les produits (public : uniquement ACTIF ; admin : statut=ALL pour tout voir, paginé)")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statut) {
        boolean includeHidden = "ALL".equalsIgnoreCase(statut);
        return ResponseEntity.ok(catalogService.getAllProducts(PageRequest.of(page, size), includeHidden));
    }

    @GetMapping("/produits/{id}")
    @Operation(summary = "Détail d'un produit (public)")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.getProductById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @GetMapping("/produits/recherche")
    @Operation(summary = "Rechercher un produit (public)")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam("q") String query,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(catalogService.searchProducts(query, "ALL".equalsIgnoreCase(statut)));
    }

    @GetMapping("/produits/filtrer")
    @Operation(summary = "Filtrer par catégorie/prix/stock (public)")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double maxPrix,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(catalogService.filterProducts(categoryId, maxPrix, minStock, "ALL".equalsIgnoreCase(statut)));
    }

    @PostMapping(value = "/produits", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer un produit (admin)")
    public ResponseEntity<Product> createProduct(
            @Valid @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            product.setImagePrincipale(imageStorageService.store(image));
        }
        return new ResponseEntity<>(catalogService.saveProduct(product), HttpStatus.CREATED); // 201
    }

    @PutMapping(value = "/produits/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Modifier un produit (admin)")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            // Une nouvelle image remplace l'ancienne ; sans nouvel envoi, on ne touche pas
            // à l'image déjà enregistrée (voir CatalogServiceImpl.updateProduct).
            product.setImagePrincipale(image != null && !image.isEmpty() ? imageStorageService.store(image) : null);
            return ResponseEntity.ok(catalogService.updateProduct(id, product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @DeleteMapping("/produits/{id}")
    @Operation(summary = "Supprimer un produit (admin)")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            catalogService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @PutMapping("/produits/{id}/masquer")
    @Operation(summary = "Masquer un produit (admin)")
    public ResponseEntity<Product> maskProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeProductStatus(id, "MASQUE"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/produits/{id}/afficher")
    @Operation(summary = "Afficher un produit (admin)")
    public ResponseEntity<Product> showProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeProductStatus(id, "ACTIF"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/produits/{id}/stock")
    @Operation(summary = "Réapprovisionner le stock (admin)")
    public ResponseEntity<Product> restock(@PathVariable Long id, @RequestParam Integer quantite) {
        try {
            return ResponseEntity.ok(catalogService.restockProduct(id, quantite));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/produits/{id}/disponibilite")
    @Operation(summary = "Vérifier stock et prix courant (interne, OpenFeign)")
    public ResponseEntity<ProduitDisponibiliteDTO> checkAvailability(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.checkAvailability(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 si le produit n'existe pas du tout
        }
    }

    // ==========================================
    // SECTION : PRIX DE GROS
    // ==========================================

    @GetMapping("/produits/{id}/prix-gros")
    @Operation(summary = "Lister les paliers d'un produit")
    public ResponseEntity<List<BulkPricing>> getProductBulkPricings(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.getBulkPricingsByProduct(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/produits/{id}/prix-gros")
    @Operation(summary = "Ajouter un palier (admin)")
    public ResponseEntity<BulkPricing> addBulkPricing(@PathVariable Long id, @Valid @RequestBody BulkPricing bulkPricing) {
        try {
            return new ResponseEntity<>(catalogService.addBulkPricing(id, bulkPricing), HttpStatus.CREATED); // 201
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/prix-gros/{id}")
    @Operation(summary = "Modifier un palier (admin)")
    public ResponseEntity<BulkPricing> updateBulkPricing(@PathVariable Long id, @Valid @RequestBody BulkPricing bulkPricing) {
        try {
            return ResponseEntity.ok(catalogService.updateBulkPricing(id, bulkPricing));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @DeleteMapping("/prix-gros/{id}")
    @Operation(summary = "Supprimer un palier (admin)")
    public ResponseEntity<?> deleteBulkPricing(@PathVariable Long id) {
        try {
            catalogService.deleteBulkPricing(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @PutMapping("/prix-gros/{id}/activer")
    @Operation(summary = "Activer un palier (admin)")
    public ResponseEntity<BulkPricing> activateBulkPricing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeBulkPricingStatus(id, "ACTIF"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/prix-gros/{id}/desactiver")
    @Operation(summary = "Désactiver un palier (admin)")
    public ResponseEntity<BulkPricing> deactivateBulkPricing(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeBulkPricingStatus(id, "INACTIF"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}