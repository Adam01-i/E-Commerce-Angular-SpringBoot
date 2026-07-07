package com.ecommerce.catalog_service.controller;

import com.ecommerce.catalog_service.model.Category;
import com.ecommerce.catalog_service.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "7.2.2 Catégories")
public class CategoryController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "Lister les catégories (public)")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @PostMapping
    @Operation(summary = "Créer une catégorie (admin)")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        try {
            Category saved = catalogService.saveCategory(category);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une catégorie (admin)")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        try {
            return ResponseEntity.ok(catalogService.updateCategory(id, category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie (admin)")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            catalogService.deleteCategory(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflit si liée à des produits
        }
    }

    @PutMapping("/{id}/masquer")
    @Operation(summary = "Masquer une catégorie (admin)")
    public ResponseEntity<Category> maskCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeCategoryStatus(id, "MASQUE"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/afficher")
    @Operation(summary = "Afficher une catégorie (admin)")
    public ResponseEntity<Category> showCategory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.changeCategoryStatus(id, "ACTIF"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}