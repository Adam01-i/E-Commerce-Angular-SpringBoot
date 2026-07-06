package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.CommandeDTO;
import com.ecommerce.order_service.dto.CreerCommandeRequest;
import com.ecommerce.order_service.dto.FactureDTO;
import com.ecommerce.order_service.entity.EtatCommande;
import com.ecommerce.order_service.service.CommandeService;
import com.ecommerce.order_service.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    private final FactureService factureService;

    @PostMapping("/api/commandes")
    public ResponseEntity<CommandeDTO> creerCommande(@Valid @RequestBody CreerCommandeRequest request) {
        CommandeDTO commande = commandeService.creerCommande(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commande);
    }

    @GetMapping("/api/commandes/{id}")
    public ResponseEntity<CommandeDTO> consulterCommande(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.consulterCommande(id));
    }

    @GetMapping("/api/commandes")
    public ResponseEntity<List<CommandeDTO>> listerCommandes(@RequestParam Long utilisateurId) {
        return ResponseEntity.ok(commandeService.listerCommandesUtilisateur(utilisateurId));
    }

    @GetMapping("/api/commandes/admin")
    public ResponseEntity<List<CommandeDTO>> listerToutesLesCommandes() {
        return ResponseEntity.ok(commandeService.listerToutesLesCommandes());
    }

    @PutMapping("/api/commandes/{id}/statut")
    public ResponseEntity<CommandeDTO> modifierStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        EtatCommande nouvelEtat = EtatCommande.valueOf(body.get("etat"));
        return ResponseEntity.ok(commandeService.modifierStatut(id, nouvelEtat));
    }

    @GetMapping("/api/factures/{commandeId}")
    public ResponseEntity<FactureDTO> consulterFacture(@PathVariable Long commandeId) {
        return ResponseEntity.ok(factureService.consulterFacture(commandeId));
    }

    @GetMapping("/api/historique")
    public ResponseEntity<List<CommandeDTO>> consulterHistorique(@RequestParam Long utilisateurId) {
        return ResponseEntity.ok(commandeService.listerCommandesUtilisateur(utilisateurId));
    }
}
