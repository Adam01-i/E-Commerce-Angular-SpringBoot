package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.AjoutPanierRequest;
import com.ecommerce.order_service.dto.PanierDTO;
import com.ecommerce.order_service.service.PanierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;

    @GetMapping
    public ResponseEntity<PanierDTO> consulterPanier(@RequestParam Long utilisateurId) {
        return ResponseEntity.ok(panierService.consulterPanier(utilisateurId));
    }

    @PostMapping("/items")
    public ResponseEntity<PanierDTO> ajouterAuPanier(
            @RequestParam Long utilisateurId,
            @Valid @RequestBody AjoutPanierRequest request) {
        PanierDTO panier = panierService.ajouterAuPanier(utilisateurId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(panier);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> supprimerArticle(
            @RequestParam Long utilisateurId,
            @PathVariable Long id) {
        panierService.supprimerArticle(utilisateurId, id);
        return ResponseEntity.noContent().build();
    }
}
