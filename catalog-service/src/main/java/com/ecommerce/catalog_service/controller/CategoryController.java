package com.ecommerce.catalog_service.controller;

import com.ecommerce.catalog_service.dto.request.CreateCategoryRequestDTO;
import com.ecommerce.catalog_service.dto.response.CategoryResponseDTO;
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
@Tag(name = "Catégories")
public class CategoryController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "Lister les catégories (public)")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @PostMapping
    @Operation(summary = "Créer une catégorie (admin)")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CreateCategoryRequestDTO request) {
        return new ResponseEntity<>(catalogService.saveCategory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une catégorie (admin)")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CreateCategoryRequestDTO request) {
        return ResponseEntity.ok(catalogService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie (admin)")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        catalogService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/masquer")
    @Operation(summary = "Masquer une catégorie (admin)")
    public ResponseEntity<CategoryResponseDTO> maskCategory(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeCategoryStatus(id, "MASQUE"));
    }

    @PutMapping("/{id}/afficher")
    @Operation(summary = "Afficher une catégorie (admin)")
    public ResponseEntity<CategoryResponseDTO> showCategory(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.changeCategoryStatus(id, "ACTIF"));
    }
}
